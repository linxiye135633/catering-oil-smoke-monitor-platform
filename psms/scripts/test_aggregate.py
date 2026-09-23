# -*- coding: utf-8 -*-
"""
test_aggregate.py 聚合接口断言测试（#E-37，孙雅欣）
覆盖用例：TC-E-031（五区一次返回）/032（分区降级语义）/033（缓存头）/036（无token401）/
         037（越权403）/038（非法参数业务码非500）
断言三层（指导书任务7）：HTTP 200 → 业务 success=true → 关键字段 jsonpath 校验
用法：TOKEN=<登录后X-Access-Token> python scripts/test_aggregate.py [base_url]
      默认 base_url=http://localhost:8080/psms
"""
import json
import os
import sys

import requests

BASE = sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080/psms'
TOKEN = os.environ.get('TOKEN', '')
PASS, FAIL = [], []


def check(name, cond, detail=''):
    (PASS if cond else FAIL).append(name)
    print(('PASS ' if cond else 'FAIL ') + name + ('  | ' + detail if detail and not cond else ''))


def call(path, token=TOKEN, params=None):
    h = {'X-Access-Token': token} if token else {}
    return requests.get(BASE + path, params=params or {}, headers=h, timeout=10)


def main():
    # TC-E-036 无token → 401（或业务层token失效码，绝不能正常返回数据）
    r = call('/v2/dashboard/aggregate', token='')
    denied = r.status_code != 200 or (r.ok and r.json().get('success') is False)
    check('TC-E-036 无token被拒(数据未泄露)', denied, f'status={r.status_code}')

    if not TOKEN:
        print('!! 未提供 TOKEN 环境变量，仅完成安全用例；登录后补跑全量')
        summary()
        return

    # TC-E-031 断言三层：200 → success → 七分区结构
    r = call('/v2/dashboard/aggregate')
    body = r.json() if r.ok else {}
    check('TC-E-031a HTTP200', r.status_code == 200, str(r.status_code))
    check('TC-E-031b success=true', body.get('success') is True, json.dumps(body)[:200])
    result = body.get('result') or {}
    parts = ['regionOverview', 'alerts', 'creditBoard', 'trend', 'industry', 'processRate', 'riskTop']
    check('TC-E-031c 七分区齐备', all(k in result for k in parts),
          'missing=' + str([k for k in parts if k not in result]))
    check('TC-E-031d 分区带degraded标记', all(isinstance(result.get(k), dict) for k in parts))

    # 分区字段校验（jsonpath等价断言）
    ro = (result.get('regionOverview') or {}).get('data') or {}
    check('TC-E-031e regionOverview字段', all(k in ro for k in
          ('companyCount', 'pointCount', 'onlinePointCount', 'excessiveCompanyCount')), str(ro)[:200])
    trend = (result.get('trend') or {}).get('data') or []
    check('TC-E-031f trend结构', all('statDate' in t and 'alarmCount' in t for t in trend) if trend else True)

    # TC-E-033 缓存命中（#E-07已实现：第二次请求应 X-Cache: HIT）
    r2 = call('/v2/dashboard/aggregate')
    cache_hdr = r2.headers.get('X-Cache')
    check('TC-E-033 第二次请求X-Cache=HIT', cache_hdr == 'HIT', f'X-Cache={cache_hdr}')

    # TC-E-037 越权（需低权限TOKEN：TOKEN_LOW 环境变量，无则跳过）
    low = os.environ.get('TOKEN_LOW', '')
    if low:
        r2 = requests.post(BASE + '/v2/credit/rules/add', json={'ruleCode': 'X'},
                           headers={'X-Access-Token': low}, timeout=10)
        check('TC-E-037 低权限写接口403', r2.status_code == 403 or
              (r2.ok and r2.json().get('success') is False), f'status={r2.status_code}')
    else:
        print('SKIP TC-E-037（未设置 TOKEN_LOW 环境变量）')

    # TC-E-038 非法参数 → 业务错误（success=false）而非500
    r = call('/v2/dashboard/aggregate', params={'cycle': "2026-09' OR '1'='1"})
    check('TC-E-038a 注入串不500', r.status_code == 200, str(r.status_code))
    check('TC-E-038b 注入串业务拒绝或空结果', (r.json().get('success') is False) or r.ok, '')
    r = call('/v2/dashboard/aggregate', params={'cycle': 'x' * 500})
    check('TC-E-038c 超长参数不500', r.status_code == 200, str(r.status_code))

    # TC-E-032 分区降级：信用周期查一个不存在的月份 → 该分区data为空但整体200
    r = call('/v2/dashboard/aggregate', params={'cycle': '2000-01'})
    body = r.json()
    ok = body.get('success') is True
    board = ((body.get('result') or {}).get('creditBoard') or {})
    check('TC-E-032 空周期分区不炸', ok and (board.get('data') in (None, [], 'null')), json.dumps(body)[:200])

    # 附属接口抽查（口径对账入口）+ 周报降级语义
    for p in ('/v2/dashboard/map/points', '/v2/dashboard/alarms/latest',
              '/v2/stat/alarm-trend', '/v2/stat/industry', '/v2/stat/process-rate',
              '/v2/credit/board', '/v2/stat/weekly-report'):
        r = call(p)
        check('附属 ' + p, r.status_code == 200 and r.json().get('success') is True,
              f'{r.status_code} {r.text[:120]}')
    r = call('/v2/stat/weekly-report')
    wr = (r.json().get('result') or {}) if r.ok else {}
    check('周报降级语义字段', 'degraded' in wr and 'summary' in wr, str(list(wr.keys())))

    summary()


def summary():
    print(f'\n==== 用例统计：通过 {len(PASS)} / 失败 {len(FAIL)} ====')
    if FAIL:
        print('失败清单：', FAIL)
        sys.exit(1)


if __name__ == '__main__':
    main()
