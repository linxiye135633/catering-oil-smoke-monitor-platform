<template>
  <div class="develop-chart-style test-content" hoverable>
    <a-card :bordered="false">
      <div class="search-header" style="margin-bottom:10px">
        <a-select
          show-search
          placeholder="请选择一个接入单位"
          v-model="companyId"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangeCompany"
          style="width: 40%; margin-right: 20px"
          allowClear
        >
          <a-select-option v-for="(item, index) in searchData" :key="index" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-select
          v-if="companyId != undefined"
          show-search
          placeholder="请选择一个测点"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangePoint"
          allowClear
          v-model="pointMac"
          style="width: 16%; margin-right: 20px"
        >
          <a-select-option v-for="(item, index) in addressData" :key="index + item.id" :value="item.name">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-input
          style="width: 16%; margin-right: 20px"
          v-if="companyId == undefined"
          placeholder="请选择一个测点"
          v-model="pointMac"
          allowClear
        ></a-input>
        <!-- <a-range-picker format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange">
        </a-range-picker> -->
        <a-button style="margin-left: 20px" icon="search" @click="searchDatas" />
        <a-button style="margin-left: 20px" type="primary" @click="clear">重置</a-button>
      </div>
      <a-spin :spinning="spinning" style="width:400px">
        <div style="height:30px;line-height:30px;box-sizing:border-box">
          <span style="padding-right:40px">今日在线:{{ onlineData.onlineTime }}</span>
          <span>离线:{{ onlineData.offline }}</span>
        </div>
      </a-spin>
      <a-list item-layout="horizontal" :loading="loading" :data-source="detailList" :pagination="pagination">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-list-item-meta>
            <a slot="title">
              <div>
                <a-collapse accordion :expand-icon-position="'right'" @change="openCollapse(item)">
                  <a-collapse-panel :key="index">
                    <div class="header-row" slot="header">
                      <a-icon style="margin-top: 4px" type="windows" />
                      <span class="header-name" :style="getColor(item.createTime)">
                        [{{ index + 1 }}] [{{ getOnlineStatus(item.createTime) }}] [{{ item.pointMac }}]-({{
                          item.name
                        }})/{{ item.manufacturerCode }}
                      </span>
                      <div class="header-circle">
                        <div class="marker-green"></div>
                        <span :style="getColor(item.createTime)">净化器</span>
                        <div class="marker-green"></div>
                        <span :style="getColor(item.createTime)">排风机</span>
                        <div :class="item.lampblackData > 1 ? 'marker-red' : 'marker-green'"></div>
                        <span :style="getColor(item.createTime)">油烟</span>
                        <div :class="item.matterData > 5 ? 'marker-red' : 'marker-green'"></div>
                        <span :style="getColor(item.createTime)">颗粒物</span>
                        <div :class="item.nmhcData > 10 ? 'marker-red' : 'marker-green'"></div>
                        <span style="margin-right: 10px" :style="getColor(item.createTime)">NmHc </span>
                        <a-icon :style="getColor(item.createTime)" style="margin-top:4px" type="clock-circle" />
                        <span :style="getColor(item.createTime)">{{ item.createTime }} </span>
                      </div>
                    </div>
                    <div class="detail-row">
                      <span style="font-weight: 700">[排放值](mg/m3)</span>
                      <div :class="item.lampblackData > 1 ? 'marker-red' : 'marker-green'"></div>
                      <span>油烟:{{ item.lampblackData }}</span>
                      <div :class="item.matterData > 5 ? 'marker-red' : 'marker-green'"></div>
                      <span>颗粒物:{{ item.matterData }}</span>
                      <div :class="item.nmhcData > 10 ? 'marker-red' : 'marker-green'"></div>
                      <span>NmHc:{{ item.nmhcData }}</span>
                    </div>
                    <div class="detail-row">
                      <div style="font-weight: 700">
                        [净化器]
                        <span v-if="item.purifierStatus == 1" style="color: #3c763d">(运行中)</span>
                        <span v-else style="color: #dd4b39">(未运行)</span>
                      </div>
                      <div class="marker-green"></div>
                      <span>电压:{{ item.secondVoltage }} V</span>
                      <div class="marker-green"></div>
                      <span>电流:{{ item.secondCurrent }} A</span>
                      <div class="marker-green"></div>
                      <span>压差:{{ item.purifierVoltageDiff }} Pa</span>
                    </div>
                    <div class="detail-row">
                      <div style="font-weight: 700">
                        [排风机]
                        <span v-if="item.fanStatus == 1" style="color: #3c763d">(运行中)</span>
                        <span v-else style="color: #dd4b39">(未运行)</span>
                      </div>
                      <div class="marker-green"></div>
                      <span>电流{{ item.fanCurrent }} A</span>
                    </div>
                    <div class="detail-row">
                      <div class="emergency" style="font-weight: 700">告警次数 [2021-10-28]</div>
                      <div class="chart" style="font-weight: 700">排放浓度（mg/m3）[{{ item.createTime }}]</div>
                    </div>
                    <div class="detail-row">
                      <div class="emergency">
                        <div class="emergency-list">
                          <img src="../../assets/warn-blue.png" alt="" />
                          <div class="txt">
                            <a v-if="item.warn1A1 > 0">油烟浓度超标</a> <a v-if="item.warn1A2 > 0">颗粒物浓度超标</a>
                            <a v-if="item.warn1A3 > 0">NmHc浓度超标</a
                            ><a v-if="item.warn1A1 == 0 && item.warn1A2 == 0 && item.warn1A3 == 0">无</a>
                            <span><a-icon type="clock-circle" />{{ time1 }}:00</span>
                          </div>
                        </div>
                        <div class="emergency-list">
                          <img src="../../assets/warn-blue.png" alt="" />
                          <div class="txt">
                            <a v-if="item.warn2A1 > 0">油烟浓度超标</a> <a v-if="item.warn2A2 > 0">颗粒物浓度超标</a>
                            <a v-if="item.warn2A3 > 0">NmHc浓度超标</a
                            ><a v-if="item.warn2A1 == 0 && item.warn2A2 == 0 && item.warn2A3 == 0">无</a>
                            <span><a-icon type="clock-circle" />{{ time2 }}:00</span>
                          </div>
                        </div>
                        <div class="emergency-list">
                          <img src="../../assets/warn-blue.png" alt="" />
                          <div class="txt">
                            <a v-if="item.warn3A1 > 0">油烟浓度超标</a> <a v-if="item.warn3A2 > 0">颗粒物浓度超标</a>
                            <a v-if="item.warn3A3 > 0">NmHc浓度超标</a
                            ><a v-if="item.warn3A1 == 0 && item.warn3A2 == 0 && item.warn3A3 == 0">无</a>
                            <span><a-icon type="clock-circle" />{{ time3 }}:00</span>
                          </div>
                        </div>
                      </div>
                      <div class="chart">
                        <div :id="`landBlack${item.id}`" :style="{ width: '100%', height: '200px' }"></div>
                      </div>
                    </div>
                    <div class="detail-row">
                      <div class="emergency href">
                        <!-- <p><a>[单位档案]</a><a>[超标分析]</a><a>[更多告警]</a></p>
                        <p><a>[测点档案]</a><a>[时长分析]</a><a>[运维申请]</a></p>
                        <p><a>[历史数据]</a><a>[告警分析]</a><a>[报警反馈]</a></p> -->
                      </div>
                      <div class="chart">
                        <div :id="`pm${item.id}`" :style="{ width: '100%', height: '200px' }"></div>
                      </div>
                    </div>
                    <div class="detail-row">
                      <div class="emergency"></div>
                      <div class="chart">
                        <div :id="`nmhc${item.id}`" :style="{ width: '100%', height: '200px' }"></div>
                      </div>
                    </div>
                  </a-collapse-panel>
                </a-collapse>
              </div>
            </a>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </a-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getAction, postAction } from '@/api/manage'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { setTimeout, setInterval } from 'timers'
export default {
  name: 'OperatingState',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  data() {
    return {
      companyId: undefined,
      searchData: [],
      addressData: [],
      onlineData: {},
      spinning: false,
      url: {
        list: '/company/list',
        pointList: '/base/getPointByCompanyId',
        detailList: '/base/getNewestCreateTime',
        chartList: '/base/getStatByCreateTime',
        warnList: '/base/getDetailsByTime',
        onlineList: '/home/pointRateFlow'
      },
      pieValue: [],
      startTime: '',
      endTime: '',
      loading: false,
      detailList: [],
      pointMac: undefined,
      pagination: {
        onChange: page => {
          console.log('cccc')
          this.pagination.current = page
          this.activeKey = ['']
          this.getListData()
        },
        onShowSizeChange: (current, size) => {
          console.log('sss')
          this.pagination.current = 1
          this.pagination.pageSize = size
          this.activeKey = ['']
          this.getListData()
        },
        pageSize: 10,
        pageSizeOptions: ['10', '50', '100'],
        total: 0,
        current: 1,
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共 ' + total + ' 条'
        },
        showQuickJumper: true,
        showSizeChanger: true
      },
      dateStr: '',
      time1: '',
      time2: '',
      time3: '',
      timer: null //定时器
    }
  },
  computed: {},
  methods: {
    getColor(time) {
      if (time == null) {
        return 'color:#808080'
      } else if (time != null) {
        if (time.substring(0, 10) == this.dateStr) {
          return 'color:#000'
        } else {
          return 'color:#808080'
        }
      }
    },
    getOnlineStatus(time) {
      if (time == null) {
        return '离线'
      } else if (time != null) {
        if (time.substring(0, 10) == this.dateStr) {
          return '在线'
        } else {
          return '离线'
        }
      }
    },
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    handleChangeCompany(value) {
      this.companyId = value
      this.getPointList()
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    openCollapse(item) {
      this.$nextTick(() => {
        this.drawChart(item)
      })
    },
    //油烟chart
    drawChart(item) {
      var myChartOne = echarts.init(document.getElementById(`landBlack${item.id}`))
      var myChartTwo = echarts.init(document.getElementById(`pm${item.id}`))
      var myChartThree = echarts.init(document.getElementById(`nmhc${item.id}`))
      getAction(this.url.chartList, { pointMac: item.pointMac, createTime: item.createTime })
        .then(res => {
          let average1 = res.result.average1
          let average2 = res.result.average2
          let average3 = res.result.average3
          let top1 = res.result.top1
          let top2 = res.result.top2
          let top3 = res.result.top3
          myChartOne.setOption({
            tooltip: {
              trigger: 'axis'
            },
            grid: {
              left: '0%',
              bottom: '3%',
              containLabel: true
            },
            legend: {
              data: ['小时均值', '小时峰值']
            },
             color:['#fac858','#91cc75'],
            // visualMap: {
            //   //根据series的data数据 改变折线颜色（待验证）
            //   // show: true,
            //   show: false,
            //   align: 'auto',
            //   type: 'piecewise',
            //   dimension: 1,
            //   seriesIndex: 0,
            //   right: 0,
            //   top: 10,
            //   pieces: [
            //     {
            //       min: 0,
            //       max: 1,
            //       color: '#fac858'
            //     },
            //     {
            //       min: 1,
            //       max: 2,
            //       color: '#91cc75'
            //     },
            //     {
            //       min: 2,
            //       max: 3,
            //       color: '#5470c6'
            //     }
            //   ]
            // },
            xAxis: {
              type: 'category',
              name: '小时',
              boundaryGap: false,
              data: [
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '10',
                '11',
                '12',
                '13',
                '14',
                '15',
                '16',
                '17',
                '18',
                '19',
                '20',
                '21',
                '22',
                '23',
                '24'
              ]
            },
            yAxis: {
              type: 'value',
              name: '油烟'
            },
            series: [
              {
                name: '小时均值',
                type: 'line',
                data: average1
              },
              {
                name: '小时峰值',
                type: 'line',
                data: top1
              }
            ]
          })
          myChartTwo.setOption({
            tooltip: {
              trigger: 'axis'
            },
            grid: {
              left: '0%',
              bottom: '3%',
              containLabel: true
            },
            legend: {
              data: ['小时均值', '小时峰值']
            },
             color:['#fac858','#91cc75'],
            // visualMap: {
            //   //根据series的data数据 改变折线颜色（待验证）
            //   show: false,
            //   align: 'auto',
            //   type: 'piecewise',
            //   dimension: 1,
            //   seriesIndex: 0,
            //   right: 0,
            //   top: 10,
            //   pieces: [
            //     {
            //       min: 0,
            //       max: 5,
            //       color: '#fac858'
            //     },
            //     {
            //       min: 5,
            //       max: 8,
            //       color: '#91cc75'
            //     },
            //     {
            //       min: 8,
            //       max: 10,
            //       color: '#5470c6'
            //     }
            //   ]
            // },
            xAxis: {
              type: 'category',
              name: '小时',
              boundaryGap: false,
              data: [
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '10',
                '11',
                '12',
                '13',
                '14',
                '15',
                '16',
                '17',
                '18',
                '19',
                '20',
                '21',
                '22',
                '23',
                '24'
              ]
            },
            yAxis: {
              type: 'value',
              name: '颗粒物'
            },
            series: [
              {
                name: '小时均值',
                type: 'line',
                data: average2
              },
              {
                name: '小时峰值',
                type: 'line',
                data: top2
              }
            ]
          })
          myChartThree.setOption({
            tooltip: {
              trigger: 'axis'
            },
            grid: {
              left: '0%',
              bottom: '3%',
              containLabel: true
            },
            legend: {
              data: ['小时均值', '小时峰值']
            },
            color:['#fac858','#91cc75'],
            // visualMap: {
            //   //根据series的data数据 改变折线颜色（待验证）
            //   show: false,
            //   align: 'auto',
            //   type: 'piecewise',
            //   dimension: 1,
            //   seriesIndex: 0,
            //   right: 0,
            //   top: 10,
            //   pieces: [
            //     {
            //       min: 0,
            //       max: 10,
            //       color: '#fac858'
            //     },
            //     {
            //       min: 10,
            //       max: 15,
            //       color: '#91cc75'
            //     },
            //     {
            //       min: 15,
            //       max: 20,
            //       color: '#5470c6'
            //     }
            //   ]
            // },
            xAxis: {
              type: 'category',
              name: '小时',
              boundaryGap: false,
              data: [
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '10',
                '11',
                '12',
                '13',
                '14',
                '15',
                '16',
                '17',
                '18',
                '19',
                '20',
                '21',
                '22',
                '23',
                '24'
              ]
            },
            yAxis: {
              type: 'value',
              name: 'NmHc'
            },
            series: [
              {
                name: '小时均值',
                type: 'line',
                data: average3
              },
              {
                name: '小时峰值',
                type: 'line',
                data: top3
              }
            ]
          })
        })
        .catch(err => {
          err
        })
    },
    handleChangePoint(value) {
      this.pointMac = value
    },
    //获取所有餐饮企业
    loadData() {
      getAction(this.url.list).then(res => {
        res.result.forEach(item => {
          this.searchData.push({ id: item.id, name: item.name })
        })
      })
    },
    //获取点位数据
    getPointList() {
      this.addressData = []
      this.pointMac = undefined
      getAction(this.url.pointList, { companyid: this.companyId }).then(res => {
        res.result.records.forEach(item => {
          this.addressData.push({ id: item.id, name: item.pointMac })
        })
      })
    },
    //点击查询
    searchDatas() {
      this.getListData()
      this.getOnlineData()
    },
    //重置
    clear() {
      this.companyId = undefined
      this.pointMac = undefined
      this.startTime = ''
      this.endTime = ''
      this.getListData()
    },
    //获取数据
    getListData() {
      this.loading = true
      let params = {
        id: this.companyId,
        pageSize: this.pagination.pageSize,
        pageNo: this.pagination.current,
        // endTime: this.endTime,
        // startTime: this.startTime,
        pointMac: this.pointMac
      }
      getAction(this.url.detailList, params)
        .then(res => {
          this.$set(this.pagination, 'total', res.result.total)
          this.$nextTick(() => {
            this.loading = false
            let arr = res.result.records
            arr.forEach(item => {
              getAction(this.url.warnList, { pointMac: item.pointMac, hour: 1 }).then(res => {
                Object.assign(item, { warn1A1: res.result.a1, warn1A2: res.result.a2, warn1A3: res.result.a3 })
              })
              getAction(this.url.warnList, { pointMac: item.pointMac, hour: 2 }).then(res => {
                Object.assign(item, { warn2A1: res.result.a1, warn2A2: res.result.a2, warn2A3: res.result.a3 })
              })
              getAction(this.url.warnList, { pointMac: item.pointMac, hour: 3 }).then(res => {
                Object.assign(item, { warn3A1: res.result.a1, warn3A2: res.result.a2, warn3A3: res.result.a3 })
              })
            })
            setTimeout(() => {
              this.detailList = arr
            }, 200)
          })
        })
        .catch(err => {
          this.loading = false
          err
        })
    },
    getOnlineData() {
      this.spinning = true
      getAction(this.url.onlineList, { hours: '-24' })
        .then(res => {
          this.onlineData = res.result
          this.spinning = false
        })
        .catch(err => {
          this.spinning = false
          err
        })
    },
    setTimeData() {
      this.timer = window.setInterval(() => {
        this.getListData()
        this.getOnlineData()
      }, 120000)
    },
    getNowDate() {
      var nowDate = new Date()
      var year = nowDate.getFullYear()
      var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
      var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
      this.dateStr = year + '-' + month + '-' + day
    }
  },
  beforeDestroy() {
    window.clearInterval(this.timer)
    this.timer = null
  },
  mounted() {
    this.setTimeData()
    this.time1 = new Date().getHours()
    this.time2 = new Date().getHours() - 1
    this.time3 = new Date().getHours() - 2
    this.getNowDate()
  },
  created() {
    this.getListData()
    this.getOnlineData()
  }
}
</script>
<style lang="less" scoped>
.header-name {
  margin-left: 10px;
}
.header-row {
  display: flex;
  position: relative;
  .header-circle {
    display: flex;
    position: absolute;
    right: 1px;
  }
}
.marker-green {
  width: 12px;
  height: 12px;
  border: 1px solid #088;
  border-radius: 24px;
  background-color: #3c763d;
  margin: 5px 3px 0px 20px;
}
.marker-red {
  width: 12px;
  height: 12px;
  border: 1px solid red;
  border-radius: 24px;
  background-color: red;
  margin: 5px 3px 0px 20px;
}
.detail-row {
  display: flex;
  margin-bottom: 15px;
  .emergency {
    flex: 1 1 30%;
    margin-right: 10%;
    .emergency-list {
      display: flex;
      line-height: 60px;
      margin-bottom: 15px;
      .txt {
        position: relative;
        margin-left: 30px;
        width: 100%;
        border-bottom: 1px solid #f3f3f3;
        span {
          position: absolute;
          right: 0;
        }
        a {
          margin-right: 5px;
          color: red;
        }
      }
      img {
        height: 30px;
        margin-top: 15px;
      }
    }
  }
  .chart {
    flex: 1 1 70%;
  }
  .href {
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-top: 25px;
    p {
      flex: 1;
      line-height: 43px;
      display: flex;
      a {
        flex: 1;
      }
    }
  }
}
</style>
