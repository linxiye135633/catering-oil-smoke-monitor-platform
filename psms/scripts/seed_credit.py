# -*- coding: utf-8 -*-
"""
seed_credit.py 信用演示数据构造脚本（#E-45，沈福临）

用途：构造信用等级边界演示企业（答辩时评委点边界值——59.99/60/79.99/80，
     用例TC-E-019/020的现场演示数据），并预置一期完整评价结果。
用法：python scripts/seed_credit.py  (需 pymysql：pip install pymysql)
口径：等级边界红<60≤黄<80≤绿；总分=100+Σ(分值×次数×权重)，与CreditEngine一致。
"""
import uuid
import pymysql

DB = dict(host='127.0.0.1', port=3306, user='root', password='123456',
          database='psms', charset='utf8mb4')
CYCLE = '2026-09'

# 边界企业（companyId 对应 seed_alarm.sql 的演示企业；C04新增两家边界企业直接造record）
BOUNDARY_CASES = [
    # (企业名, 总分, 期望等级) —— 覆盖边界两侧：59.99/60 与 79.99/80
    ('演示·川味火锅（人民路店）', None, None),   # 由明细真实计算（预期红牌）
    ('演示·老城烧烤', None, None),               # 由明细真实计算（预期黄牌附近）
    ('演示·家常菜馆', None, None),               # 由明细真实计算（预期绿牌）
    ('演示·边界A(59.99)', '59.99', '1'),
    ('演示·边界B(60.00)', '60.00', '2'),
    ('演示·边界C(79.99)', '79.99', '2'),
    ('演示·边界D(80.00)', '80.00', '3'),
    ('演示·满分(100)', '100.00', '3'),
]


def level_of(score):
    """与 CreditEngine.judgeLevel 完全一致：<60红(1)，60≤x<80黄(2)，≥80绿(3)"""
    s = float(score)
    if s < 60:
        return '1'
    if s < 80:
        return '2'
    return '3'


def main():
    conn = pymysql.connect(**DB)
    cur = conn.cursor()
    rules = {}
    cur.execute("SELECT id, rule_code, score_value, weight, max_times FROM bu_credit_rule WHERE enabled='1' AND del_flag=0")
    for rid, code, val, w, mt in cur.fetchall():
        rules[code] = (rid, float(val), float(w), mt)

    # 1) 演示企业的真实明细（与 seed_alarm 数据量对应：C01 超标8次/日×7被封顶30，C02关机3次等）
    real = [
        ('演示·川味火锅（人民路店）', {'R-ALARM-COUNT': 30, 'R-FAN-OFF': 8}),          # 100-60-8=32 → 红
        ('演示·老城烧烤', {'R-ALARM-COUNT': 20, 'R-FAN-OFF': 3}),                     # 100-40-3=57 → 红(演示中低配可调)
        ('演示·家常菜馆', {'R-ALARM-COUNT': 5}),                                      # 100-10=90 → 绿
        ('演示·快餐店（中心店）', {'R-SELFCHECK': 6}),                                 # 100+6=106→钳制演示见满分
    ]
    for name, hits in real:
        cur.execute("SELECT id FROM base_company WHERE name=%s LIMIT 1", (name,))
        row = cur.fetchone()
        if not row:
            print(f'[skip] 企业不存在：{name}')
            continue
        write_record(cur, row[0], name, hits, rules)

    # 2) 边界企业：直接构造指定总分（不建企业档案，榜单演示用幽灵数据？——否，
    #    课程诚信要求可追溯：边界企业同样建档，明细为一笔"演示校准分"）
    for name, score, _ in BOUNDARY_CASES:
        if score is None:
            continue
        cid = 'E-DEMO-' + uuid.uuid4().hex[:8].upper()
        cur.execute(
            "INSERT INTO base_company(id, code, name, area_code, address, contact, lng, lat, status, creator, create_time) "
            "VALUES(%s,%s,%s,'131002','演示地址','演示',116.40,39.91,'01','seed',NOW()) "
            "ON DUPLICATE KEY UPDATE name=name", (cid, 'DEMO' + cid[-4:], name))
        write_boundary(cur, cid, name, float(score), rules)
    conn.commit()
    cur.execute("SELECT company_name, total_score, level FROM bu_credit_record WHERE cycle=%s ORDER BY total_score", (CYCLE,))
    print('=== %s 信用榜单 ===' % CYCLE)
    for n, s, l in cur.fetchall():
        print(f'{s:>8}  { {"1":"红牌","2":"黄牌","3":"绿牌"}[l] }  {n}')
    conn.close()


def write_record(cur, cid, name, hits, rules):
    """按真实明细计算落库（模拟 CreditEngine 的手工版，便于演示前预置）"""
    total = 100.0
    rid = str(uuid.uuid4())
    for code, times in hits.items():
        if code not in rules:
            continue
        rule_id, val, w, mt = rules[code]
        capped = min(times, mt) if mt else times
        total += val * capped * w
    total = max(total, 0.0)
    upsert(cur, rid, cid, name, round(total, 2))


def write_boundary(cur, cid, name, score, rules):
    """边界企业：一笔校准明细凑出指定分数（明细可追溯，答辩可解释）"""
    rid = str(uuid.uuid4())
    rule_id, val, w, _ = rules.get('R-ALARM-COUNT', (None, -2.0, 1.0, None))
    times = max(0, round((100 - score) / abs(val * w), 2))
    upsert(cur, rid, cid, name, round(score, 2), calib=(rule_id, times))


def upsert(cur, rid, cid, name, score, calib=None):
    lvl = level_of(score)
    cur.execute("DELETE FROM bu_credit_detail WHERE record_id IN "
                "(SELECT id FROM bu_credit_record WHERE company_id=%s AND cycle=%s)", (cid, CYCLE))
    cur.execute("DELETE FROM bu_credit_record WHERE company_id=%s AND cycle=%s", (cid, CYCLE))
    cur.execute(
        "INSERT INTO bu_credit_record(id, company_id, company_name, cycle, total_score, level, snapshot, publish_status, calc_time, create_by, create_time) "
        "VALUES(%s,%s,%s,%s,%s,%s,'[]','1',NOW(),'seed',NOW())",
        (rid, cid, name, CYCLE, score, lvl))
    if calib:
        cur.execute(
            "INSERT INTO bu_credit_detail(id, record_id, rule_id, rule_code, change_score, times, source_no, source_type, remark, create_by, create_time) "
            "VALUES(%s,%s,%s,'R-ALARM-COUNT',%s,%s,'SEED-CALIB','1','演示边界校准分','seed',NOW())",
            (str(uuid.uuid4()), rid, calib[0], round(score - 100, 2), calib[1]))
    print(f'[ok] {name}: {score} -> { {"1":"红","2":"黄","3":"绿"}[lvl] }牌')


if __name__ == '__main__':
    main()
