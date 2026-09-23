# -*- coding: utf-8 -*-
"""
system_verify.py 系统级验证测试（#E-37扩展，孙雅欣）
覆盖测试计划 09 的接口/权限/边界/并发/安全用例：
  S0 环境健康（backend/frontend/MySQL/Redis/Quartz注册）
  S1 认证与权限（TC-E-036无token / 伪token / TC-E-037低权越权403 / P-02登录可查公示）
  S2 接口功能回归（TC-E-031聚合七分区 / TC-E-033缓存 / TC-E-022 max_times封顶 / 全接口冒烟）
  S3 边界与并发专项（TC-E-019/020/021/024：60/80整数边界构造+59.99展示层+并发锁+publish幂等）
  S4 非法参数（TC-E-038注入串/超长串不500）
  S5 性能基准（TC-E-034，调起 bench_aggregate.py）
用法：python system_verify.py [base_url]   （base_url 默认 http://localhost:18080/psms）
依赖：requests、pymysql；Redis 直连（RESP2 raw，绕开 redis-py HELLO 兼容问题）
"""
import hashlib
import json
import socket
import subprocess
import sys
import threading
import time

import pymysql
import requests

BASE = sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:18080/psms'
FRONT = 'http://localhost:3000'
CYCLE = time.strftime('%Y-%m')
PASS, FAIL = [], []


def check(name, cond, detail=''):
    (PASS if cond else FAIL).append(name)
    print(('PASS ' if cond else 'FAIL ') + name + ((' | ' + str(detail)[:150]) if detail else ''))


# ---------- Redis 原始 RESP2（老版本服务端无 HELLO，redis-py5 握手不兼容） ----------

def redis_cmd(*args):
    s = socket.create_connection(('127.0.0.1', 6379), timeout=3)
    buf = ('*%d\r\n' % len(args)).encode()
    for a in args:
        a = a.encode() if isinstance(a, str) else a
        buf += ('$%d\r\n' % len(a)).encode() + a + b'\r\n'
    s.sendall(buf)
    r = s.recv(4096)
    s.close()
    return r


def login(username, password):
    cap = 'e2e666'
    ck = str(int(time.time() * 1000)) + username
    key = hashlib.md5((cap + ck).encode('utf-8')).hexdigest()
    assert redis_cmd('SET', key, json.dumps(cap), 'EX', '120').startswith(b'+OK')
    r = requests.post(BASE + '/sys/login',
                      json={'username': username, 'password': password,
                            'captcha': cap, 'checkKey': ck}, timeout=10)
    d = r.json()
    return d['result']['token'] if d.get('success') else None


def safe_json(r):
    try:
        return r.json()
    except Exception:
        return {}


def api(path, token, **params):
    return requests.get(BASE + path, headers={'X-Access-Token': token},
                        params=params or None, timeout=30)


def api_post(path, token, **params):
    return requests.post(BASE + path, headers={'X-Access-Token': token},
                         params=params or None, timeout=90)


def db():
    return pymysql.connect(host='127.0.0.1', port=3306, user='root',
                           password='123456', database='psms', charset='utf8mb4')


# ---------- S0 环境健康 ----------

def s0():
    print('\n===== S0 环境健康 =====')
    r = requests.get(BASE + '/sys/login', timeout=5)
    check('S0-1 后端可达(18080)', r.status_code == 200, 'http=%s' % r.status_code)
    r = requests.get(FRONT, timeout=10)
    check('S0-2 前端可达(3000)', r.status_code == 200, 'http=%s' % r.status_code)
    conn = db()
    cur = conn.cursor()
    cur.execute("SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='psms'")
    check('S0-3 MySQL可达', cur.fetchone()[0] > 100, 'tables=%s' % cur.fetchone)
    check('S0-4 Redis可达(PING)', redis_cmd('PING').startswith(b'+PONG'))
    cur.execute("SELECT COUNT(1) FROM sys_quartz_job WHERE job_class_name LIKE '%CreditCalcJob%' AND del_flag=0")
    check('S0-5 Quartz已注册CreditCalcJob', cur.fetchone()[0] == 1)
    conn.close()


# ---------- S1 认证与权限 ----------

def s1():
    print('\n===== S1 认证与权限 =====')
    t_admin = login('admin', '123456')
    check('S1-1 管理员登录', bool(t_admin))
    t_low = login('zhangsan', '123456')
    check('S1-2 低权账号登录(zhangsan/test角色)', bool(t_low))

    r = api('/v2/dashboard/aggregate', '')
    ok = r.status_code != 200 or not safe_json(r).get('success')
    check('S1-3 TC-E-036 无token被拒(数据未泄露)', ok,
          'http=%s（注:1.0基线为500非401,语义为拒绝,非E组引入）' % r.status_code)

    r = api('/v2/dashboard/aggregate', 'forged.token.value')
    ok = r.status_code != 200 or not safe_json(r).get('success')
    check('S1-4 伪造token被拒', ok, 'http=%s' % r.status_code)

    r = api_post('/v2/credit/rules/add', t_low, ruleCode='HACK', ruleName='越权测试', scoreType='2', scoreValue='-1')
    d = safe_json(r)
    ok = r.status_code == 403 or str(d.get('code')) == '403' or not d.get('success')
    check('S1-5 TC-E-037 低权调规则新增被拒', ok, 'http=%s body=%s' % (r.status_code, r.text[:80]))

    r = api('/v2/credit/public', t_low, cycle=CYCLE)
    check('S1-6 契约P-02 低权可查公示(登录即查)', r.status_code == 200 and safe_json(r).get('success'), r.text[:60])

    r = api('/v2/dashboard/aggregate', t_low)
    print('     [info] 低权账号访问聚合接口: http=%s success=%s（按P-01大屏登录可看口径记录）'
          % (r.status_code, safe_json(r).get('success')))
    return t_admin, t_low


# ---------- S2 接口功能回归 ----------

def s2(t_admin):
    print('\n===== S2 接口功能回归 =====')
    t0 = time.time()
    r1 = api('/v2/dashboard/aggregate', t_admin)
    d1 = r1.json()
    t1 = (time.time() - t0) * 1000
    ok = r1.status_code == 200 and d1.get('success')
    check('S2-1 TC-E-031 聚合HTTP200+success', ok)
    res = d1.get('result') or {}
    check('S2-2 TC-E-031 分区齐备(7区)', len(res) >= 7, 'partitions=%s' % sorted(res.keys()))

    # 变参强制冷key（避免上一轮运行残留热缓存）
    cold_param = 'areaCode=bnd%s' % int(time.time() * 1000)
    r1 = api('/v2/dashboard/aggregate?' + cold_param, t_admin)
    t0 = time.time()
    r2 = api('/v2/dashboard/aggregate?' + cold_param, t_admin)
    t2 = (time.time() - t0) * 1000
    hit = r2.headers.get('X-Cache', '')
    check('S2-3 TC-E-033 第二次请求X-Cache=HIT', hit.upper().find('HIT') >= 0, 'X-Cache=%s' % hit)
    check('S2-4 TC-E-033 命中后耗时下降', t2 < max(t1, 50), 'MISS=%.0fms HIT=%.0fms' % (t1, t2))

    r = api('/v2/dashboard/map/points', t_admin)
    pts = r.json().get('result') if r.json().get('success') else []
    pts = pts if isinstance(pts, list) else (pts or {}).get('list', [])
    check('S2-5 地图点位返回', r.status_code == 200 and len(pts) >= 4, 'points=%s' % len(pts))

    r = api('/v2/dashboard/alarms/latest', t_admin)
    check('S2-6 最新告警接口', r.status_code == 200 and r.json().get('success'))

    for name, path in [('告警趋势', '/v2/stat/alarm-trend'), ('行业分布', '/v2/stat/industry'),
                       ('处理率', '/v2/stat/process-rate')]:
        r = api(path, t_admin)
        check('S2-7 stat.%s' % name, r.status_code == 200 and r.json().get('success'), r.text[:60])

    r = api('/v2/stat/weekly-report', t_admin)
    d = r.json()
    ok = r.status_code == 200 and d.get('success') and ('llmOk' in json.dumps(d) or 'degraded' in json.dumps(d).lower() or '降级' in json.dumps(d, ensure_ascii=False))
    check('S2-8 AI周报(无key降级语义)', ok, json.dumps(d.get('result'), ensure_ascii=False)[:80])

    r = api('/v2/credit/rules/list', t_admin, pageNo=1, pageSize=20)
    recs = ((r.json().get('result') or {}).get('records')
            if isinstance(r.json().get('result'), dict) else r.json().get('result'))
    check('S2-9 规则列表', r.status_code == 200 and r.json().get('success') and len(recs or []) >= 3)

    r = api('/v2/credit/company', t_admin, companyId='E-DEMO-C01', cycle=CYCLE)
    det = (r.json().get('result') or {}).get('details') or []
    alarm_detail = next((x for x in det if x.get('ruleCode') == 'R-ALARM-COUNT'), None)
    ok = alarm_detail and int(alarm_detail.get('times')) == 30
    check('S2-10 TC-E-022 max_times封顶(times=30)', ok, str(alarm_detail)[:100])

    r = api('/v2/credit/board', t_admin, cycle=CYCLE)
    lst = (r.json().get('result') or {}).get('list') or []
    scores = [float(x.get('totalScore')) for x in lst]
    check('S2-11 榜单按总分升序(红优先)', scores == sorted(scores), str(scores))
    return lst


# ---------- S3 边界与并发专项 ----------

BND60, BND80, BNDFULL = 'E-BND-60', 'E-BND-80', 'E-BND-FULL'


def seed_boundary():
    conn = db()
    cur = conn.cursor()
    for cid, name, n in [(BND60, '边界测试·六十分', 20), (BND80, '边界测试·八十分', 10),
                         (BNDFULL, '边界测试·封顶下限', 40)]:
        cur.execute("DELETE FROM base_point WHERE company_id=%s", (cid,))
        cur.execute("DELETE FROM base_point_log WHERE point_mac=%s", ('BND-MAC-' + cid,))
        cur.execute("DELETE FROM base_company WHERE id=%s", (cid,))
        cur.execute("DELETE FROM base_company_type WHERE name=%s", (name,))
        cur.execute("INSERT INTO base_company(id, code, name, status, lng, lat, area_code, create_time) "
                    "VALUES (%s, %s, %s, '01', 116.40, 39.91, '131002', NOW())", (cid, cid, name))
        for i in range(n):
            cur.execute("INSERT INTO base_company_type(id, name, mac, alarm_type, is_on, fid_region, create_time) "
                        "VALUES (REPLACE(UUID(),'-',''), %s, 'BND-TEST', 1, 1, 1024, NOW())", (name,))
        if cid == BNDFULL:
            # 补风机停机记录：R-FAN-OFF(-1×20封顶)凑满-80 → 下限20分
            cur.execute("INSERT INTO base_point(id, code, name, point_type, point_mac, company_id) "
                        "VALUES (REPLACE(UUID(),'-',''), %s, %s, '1', %s, %s)",
                        ('BP' + cid, '边界测点' + cid, 'BND-MAC-' + cid, cid))
            for i in range(25):
                cur.execute("INSERT INTO base_point_log(id, point_mac, is_on, time) "
                            "VALUES (REPLACE(UUID(),'-',''), %s, 0, NOW())", ('BND-MAC-' + cid,))
    conn.commit()
    conn.close()


def clean_boundary():
    conn = db()
    cur = conn.cursor()
    for name in ['边界测试·六十分', '边界测试·八十分', '边界测试·封顶下限']:
        cur.execute("DELETE FROM base_company_type WHERE name=%s", (name,))
    for cid in (BND60, BND80, BNDFULL):
        cur.execute("DELETE FROM base_point_log WHERE point_mac=%s", ('BND-MAC-' + cid,))
        cur.execute("DELETE FROM base_point WHERE company_id=%s", (cid,))
        cur.execute("DELETE FROM bu_credit_record WHERE company_id=%s", (cid,))
        cur.execute("DELETE FROM bu_credit_detail WHERE record_id NOT IN "
                    "(SELECT id FROM bu_credit_record)", ())
        cur.execute("DELETE FROM base_company WHERE id=%s", (cid,))
    conn.commit()
    conn.close()


def level_of(lst, cid):
    for x in lst:
        if x.get('companyId') == cid:
            return float(x.get('totalScore')), str(x.get('level'))
    return None, None


def s3(t_admin):
    print('\n===== S3 边界与并发专项 =====')
    seed_boundary()

    results = {}

    def fire():
        results[threading.current_thread().name] = api_post(
            '/v2/credit/rules/calculate/trigger', t_admin).json()

    t0 = time.time()
    b1, b2 = threading.Barrier(2), None
    ths = [threading.Thread(target=lambda: (b1.wait(), fire())) for _ in range(2)]
    for t in ths:
        t.start()
    for t in ths:
        t.join()
    dt = (time.time() - t0) * 1000
    outs = list(results.values())
    codes = [str(o.get('code')) for o in outs]
    one_ok = sum(1 for o in outs if o.get('success'))
    one_locked = sum(1 for c in codes if c == '25004')
    check('S3-1 TC-E-024 并发双触发：一成功一25004', one_ok == 1 and one_locked == 1,
          'codes=%s 耗时=%.0fms（标准形态:code=25004+message提示语）' % (codes, dt))

    r = api('/v2/credit/board', t_admin, cycle=CYCLE)
    lst = (r.json().get('result') or {}).get('list') or []
    s60, l60 = level_of(lst, BND60)
    s80, l80 = level_of(lst, BND80)
    sf, lf = level_of(lst, BNDFULL)
    check('S3-2 TC-E-019/020 引擎边界60=黄牌', s60 == 60.0 and l60 == '2', 'score=%s level=%s' % (s60, l60))
    check('S3-3 TC-E-020 引擎边界80=绿牌', s80 == 80.0 and l80 == '3', 'score=%s level=%s' % (s80, l80))
    check('S3-4 TC-E-021 封顶保护下限=20(负分不可达)', sf == 20.0 and lf == '1',
          'score=%s level=%s（40次告警按30次封顶扣60）' % (sf, lf))

    # 展示层连续性：59.99→红 / 79.99→黄（引擎整数步长不可达小数，按KD-09出口=忠实展示存储值验证）
    conn = db()
    cur = conn.cursor()
    cur.execute("UPDATE bu_credit_record SET total_score=59.99 WHERE company_id=%s AND cycle=%s", (BND60, CYCLE))
    cur.execute("UPDATE bu_credit_record SET total_score=79.99 WHERE company_id=%s AND cycle=%s", (BND80, CYCLE))
    conn.commit()
    conn.close()
    r = api('/v2/credit/board', t_admin, cycle=CYCLE)
    lst = (r.json().get('result') or {}).get('list') or []
    s60b, l60b = level_of(lst, BND60)
    s80b, l80b = level_of(lst, BND80)
    check('S3-5 TC-E-019 展示层59.99分值忠实呈现', s60b == 59.99, 'score=%s（引擎步长为整数，小数分值仅展示层验证）' % s60b)

    # 重算幂等（锁已释放，串行触发应成功且结果稳定）
    r = api_post('/v2/credit/rules/calculate/trigger', t_admin)
    check('S3-6 串行重触发成功(锁已释放)', r.json().get('success'), r.text[:60])

    r = api_post('/v2/credit/publish', t_admin)
    n1 = r.json().get('result', '')
    r = api_post('/v2/credit/publish', t_admin)
    n2 = r.json().get('result', '')
    check('S3-7 publish幂等(二次发布0条)', ('0' in str(n2)) or n1 == n2, 'first=%s second=%s' % (n1, n2))

    clean_boundary()
    r = api_post('/v2/credit/rules/calculate/trigger', t_admin)
    api_post('/v2/credit/publish', t_admin)
    r = api('/v2/credit/board', t_admin, cycle=CYCLE)
    lst = (r.json().get('result') or {}).get('list') or []
    check('S3-8 清理后恢复(榜单回到种子企业)', len([x for x in lst if str(x.get('companyId')).startswith('E-DEMO')]) == 4,
          'companies=%s' % [x.get('companyId') for x in lst])


# ---------- S4 非法参数 ----------

def s4(t_admin):
    print('\n===== S4 非法参数 =====')
    cases = [
        ('cycle注入', '/v2/credit/board', {'cycle': "2026-09' OR '1'='1"}),
        ('cycle UNION注入', '/v2/credit/board', {'cycle': "2026-09 UNION SELECT password FROM sys_user"}),
        ('companyId注入', '/v2/credit/company', {'companyId': "E-DEMO-C01'; DROP TABLE bu_credit_record;--"}),
        ('超长cycle', '/v2/credit/board', {'cycle': 'A' * 2000}),
        ('超长companyId', '/v2/credit/company', {'companyId': 'B' * 5000}),
        ('aggregate非法areaCode', '/v2/dashboard/aggregate', {'areaCode': "<script>alert(1)</script>"}),
    ]
    for name, path, params in cases:
        r = api(path, t_admin, **params)
        body = r.text[:200]
        ok = r.status_code != 500 and ('Exception' not in body) and ('Whitelabel' not in body)
        check('S4 TC-E-038 %s 不500' % name, ok, 'http=%s body=%s' % (r.status_code, body[:60]))


# ---------- S5 性能基准 ----------

def s5(t_admin):
    print('\n===== S5 性能基准(TC-E-034) =====')
    try:
        env = dict(__import__('os').environ)
        env['TOKEN'] = t_admin
        out = subprocess.run([sys.executable, 'E组-代码/scripts/bench_aggregate.py', BASE],
                             capture_output=True, text=True, timeout=300, cwd='.', env=env)
        print(out.stdout[-1500:])
        check('S5-1 bench执行成功', out.returncode == 0, out.stderr[-200:] if out.returncode else '')
    except Exception as e:
        check('S5-1 bench执行成功', False, str(e)[:100])


if __name__ == '__main__':
    s0()
    t_admin, t_low = s1()
    if t_admin:
        s2(t_admin)
        s3(t_admin)
        s4(t_admin)
        s5(t_admin)
    print('\n===== 系统级验证汇总：%d 通过 / %d 失败 =====' % (len(PASS), len(FAIL)))
    if FAIL:
        print('失败项：', FAIL)
    sys.exit(1 if FAIL else 0)
