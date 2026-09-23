# -*- coding: utf-8 -*-
"""
bench_aggregate.py 聚合接口性能对比（#E-38，孙雅欣；优化闭环留档数据来源）
对比场景（优化闭环①的证据：请求5→1）：
  A. 轮询5个独立接口（alarm-trend/industry/process-rate/alarms/latest/credit/board）
  B. 单次聚合接口 /v2/dashboard/aggregate
输出：p50/p95/平均 与 总耗时对比，结果写 stdout（W6优化记录表直接引用）。
用法：TOKEN=<token> python scripts/bench_aggregate.py [base_url] [N]
      默认 N=20 次/场景
"""
import os
import statistics
import sys
import time

import requests

BASE = sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080/psms'
N = int(sys.argv[2]) if len(sys.argv) > 2 else 20
TOKEN = os.environ.get('TOKEN', '')
HEADERS = {'X-Access-Token': TOKEN} if TOKEN else {}

SINGLE_PATHS = ['/v2/stat/alarm-trend', '/v2/stat/industry', '/v2/stat/process-rate',
                '/v2/dashboard/alarms/latest', '/v2/credit/board']
AGG_PATH = '/v2/dashboard/aggregate'


def time_get(path):
    t0 = time.perf_counter()
    r = requests.get(BASE + path, headers=HEADERS, timeout=15)
    ms = (time.perf_counter() - t0) * 1000
    if not r.ok or not r.json().get('success'):
        raise RuntimeError(f'{path} 请求异常: {r.status_code}')
    return ms


def bench(name, fn):
    samples = []
    for i in range(N):
        try:
            samples.append(fn())
        except Exception as e:
            print(f'[{name}] 第{i + 1}次失败：{e}')
    if not samples:
        print(f'[{name}] 无有效样本')
        return None
    p50 = statistics.median(samples)
    p95 = sorted(samples)[int(len(samples) * 0.95) - 1] if len(samples) >= 20 else max(samples)
    print(f'[{name}] n={len(samples)} avg={statistics.mean(samples):.0f}ms '
          f'p50={p50:.0f}ms p95={p95:.0f}ms total={sum(samples):.0f}ms')
    return {'p50': p50, 'p95': p95, 'total': sum(samples)}


def main():
    if not TOKEN:
        print('!! 缺少 TOKEN 环境变量（性能数据无效，请登录后取token再跑）')
        sys.exit(2)
    print(f'基准：{BASE} 每场景 {N} 次\n')

    def five_calls():
        return sum(time_get(p) for p in SINGLE_PATHS)

    a = bench('A 五接口轮询(串行5次)', five_calls)
    b = bench('B 聚合接口(单次)', lambda: time_get(AGG_PATH))

    if a and b:
        print(f'\n结论：聚合接口 p50 {b["p50"]:.0f}ms vs 五接口合计 p50 {a["p50"]:.0f}ms，'
              f'降幅 {(1 - b["p50"] / a["p50"]) * 100:.0f}%；'
              f'请求数 5→1（NFR：聚合<500ms 目标 {"达成" if b["p95"] < 500 else "未达成，进入#E-07缓存优化"}）')


if __name__ == '__main__':
    main()
