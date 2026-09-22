/**
 * screenPoll 大屏统一轮询服务（#E-25/#E-31，吴一萱）
 *
 * 职责：全屏唯一轮询入口——组件只订阅不自行请求，杜绝"五组件各轮各的"请求风暴。
 * 特性：
 * 1. 峰谷轮询参数化（#E-31）：峰时/谷时毫秒数可配（register options 或
 *    VUE_APP_POLL_PEAK/VUE_APP_POLL_VALLEY 环境变量），默认峰60s/谷300s（满足MUST≤5min）；
 *    峰谷时段：08:00-20:00 为峰时；
 * 2. 注册/反注册：组件 beforeDestroy 必须调用 unregister，防内存泄漏（稳定性NFR）；
 * 3. 页面不可见自动暂停（document.hidden），回来立即拉一次；
 * 4. 失败不中断：单次失败静默待下轮，由订阅方自行展示降级态（缓存值+黄标）。
 *
 * 用法：
 *   const poll = screenPoll.register({ key: 'aggregate', url: '/v2/dashboard/aggregate',
 *     peakInterval: 60000, valleyInterval: 300000, handler: d => {...} })
 *   poll.start();  // beforeDestroy: poll.stop()
 */
import { getAction } from '@/api/manage'

const DEFAULT_PEAK = Number(process.env.VUE_APP_POLL_PEAK) || 60 * 1000
const DEFAULT_VALLEY = Number(process.env.VUE_APP_POLL_VALLEY) || 5 * 60 * 1000
const PEAK_HOUR_RANGE = [8, 20] // [起,止) 8:00~20:00 峰时

function currentInterval (peakMs, valleyMs) {
  const hour = new Date().getHours()
  const [from, to] = PEAK_HOUR_RANGE
  const peak = hour >= from && hour < to
  return peak ? peakMs : valleyMs
}

class PollTask {
  constructor (opt) {
    this.key = opt.key
    this.url = opt.url
    this.params = opt.params || {}
    this.handler = opt.handler
    this.peakInterval = opt.peakInterval || DEFAULT_PEAK
    this.valleyInterval = opt.valleyInterval || DEFAULT_VALLEY
    this.timer = null
    this.loading = false
    this.running = false
  }
  async fetch () {
    if (this.loading) return
    this.loading = true
    try {
      const res = await getAction(this.url, this.params)
      if (res && res.success) {
        this.handler(res.result)
      }
    } catch (e) {
      // 静默失败：订阅方依据 handler 长时间未触发自行展示降级态
      console.warn(`[screenPoll] ${this.key} 拉取失败，待下轮重试`, e && e.message)
    } finally {
      this.loading = false
    }
  }
  start () {
    this.running = true
    this.fetch()
    this.schedule()
  }
  schedule () {
    clearTimeout(this.timer)
    this.timer = setTimeout(() => {
      this.fetch()
      this.schedule()
    }, currentInterval(this.peakInterval, this.valleyInterval))
  }
  /** 页面不可见：暂停轮询循环（保留注册与运行意图，回来 resume 续跑），落实 #E-25 不可见暂停 */
  pause () {
    clearTimeout(this.timer)
    this.timer = null
  }
  /** 页面重新可见：立即补拉一次并恢复循环（仅在曾 start 且当前未运行时） */
  resume () {
    if (!this.running || this.timer) return
    this.fetch()
    this.schedule()
  }
  stop () {
    this.running = false
    clearTimeout(this.timer)
    this.timer = null
  }
}

const tasks = new Map()
const visibilityHandler = () => {
  if (document.hidden) {
    tasks.forEach(t => t.pause())
  } else {
    tasks.forEach(t => t.resume())
  }
}
if (typeof document !== 'undefined') {
  document.addEventListener('visibilitychange', visibilityHandler)
}

export default {
  register (opt) {
    if (tasks.has(opt.key)) {
      tasks.get(opt.key).stop()
    }
    const task = new PollTask(opt)
    tasks.set(opt.key, task)
    return task
  },
  unregister (key) {
    const t = tasks.get(key)
    if (t) {
      t.stop()
      tasks.delete(key)
    }
  },
  /** 手动触发一次（演示/降级重试用） */
  refresh (key) {
    const t = tasks.get(key)
    if (t) t.fetch()
  }
}
