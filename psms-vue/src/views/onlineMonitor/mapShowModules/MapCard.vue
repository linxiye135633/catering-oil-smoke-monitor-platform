<template>
  <a-card class="map-card" :headStyle="tstyle" :bordered="false">
    <div slot="title">
      <p class="title-text-big">
        测点MAC：{{ cardData.pointMac }}[{{ getOnlineStatus(cardData.createTime) }}]
        <a style="color: #fff"><a-icon type="close" @click="cardClose"/></a>
      </p>
      <p class="title-text">
        - ({{ cardData.name }}) <span>{{ cardData.createTime }}</span>
      </p>
    </div>
    <div class="p-text">
      <span>浓度值（mg/m3）</span>
      <div class="p-num">
        <div class="marker"></div>
        <span>油烟：{{ cardData.lampblackData }}</span>
        <div class="marker"></div>
        <span>颗粒物：{{ cardData.matterData }}</span>
        <div class="marker"></div>
        <span>NmHc：{{ cardData.nmhcData }}</span>
      </div>
    </div>
    <div class="p-text margin-top15">
      <span>排风机（运行中）</span>
      <div class="p-num">
        <div class="marker"></div>
        <span>电流：{{ cardData.fanCurrent }}A</span>
      </div>
    </div>
    <div class="p-text margin-top15">
      <span>净化器（运行中）</span>
      <div class="p-num">
        <div class="marker"></div>
        <span>电压：{{ cardData.secondVoltage }}V</span>
        <div class="marker"></div>
        <span>电流：{{ cardData.secondCurrent }}A</span>
        <div class="marker"></div>
        <span>压差：{{ cardData.purifierVoltageDiff }}Pa</span>
      </div>
    </div>
    <div class="tab-box">
      <a-tabs default-active-key="1" @change="callback">
        <a-tab-pane :forceRender="true" key="1" tab="测点信息">
          <div class="point-box">
            <div>
              <p>测点Mac：{{ cardData.pointMac }}</p>
              <p>排口名称：{{ cardData.portName }}</p>
              <p>所属单位：{{ cardData.name }}</p>
              <p>所在区域：{{ cardData.areaStreet }}</p>
              <p>单位地址：{{ cardData.address }}</p>
              <p>联系人员：{{ cardData.contact }}</p>
              <p>默认手机：{{ cardData.contactMobile }}</p>
              <p>服务商：{{ cardData.manufacturerCode }}</p>
            </div>
            <div>
              <p>标准灶头：{{ cardData.stoveNumber }}个</p>
              <p>设计风量：{{ cardData.airVolume }}立方米/秒</p>
              <p>技术路线：{{ cardData.techRoadmap }}</p>
              <!-- <p>扫码监测：微信扫码查看测点状态</p>
              <img src="../../../assets/2weima.png" alt="" /> -->
              <!-- <p class="href"><a>[单位档案]</a><a>[测点档案]</a><a>[历史数据]</a></p> -->
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane :forceRender="true" key="2" tab="电场" force-render>
          <h3>未获取到电场信息.</h3>
          <p>- 原因之一：未录入电场设备信息</p>
          <p>- 原因之二：未采集电场数据信息</p>
        </a-tab-pane>
        <a-tab-pane :forceRender="true" key="3" tab="告警">
          <p style="font-weight: 700"><a @click="goWarn">[ 更多告警 ]</a></p>
          <div class="emergency">
            <!-- <div class="emergency-list">
              <img src="../../../assets/warn-blue.png" alt="" />
              <div class="txt">
                哈哈哈哈啊哈哈哈哈 <span><a-icon type="clock-circle" />10:00</span>
              </div>
            </div>
            <div class="emergency-list">
              <img src="../../../assets/warn-blue.png" alt="" />
              <div class="txt">
                哈哈哈哈啊哈哈哈哈 <span><a-icon type="clock-circle" />09:00</span>
              </div>
            </div>
            <div class="emergency-list">
              <img src="../../../assets/warn-blue.png" alt="" />
              <div class="txt">
                哈哈哈哈啊哈哈哈哈 <span><a-icon type="clock-circle" />08:00</span>
              </div>
            </div> -->
          </div>
        </a-tab-pane>
        <a-tab-pane :forceRender="true" key="4" tab="油烟">
          <p style="font-weight: 700">
            排放浓度(mg/m3) [ 2021-10-27 ] <a class="margin-right10">[ 超标分析 ]</a>
            <a class="margin-right10">[ 时长分析 ]</a> <a class="margin-right10">[ 告警分析 ]</a>
          </p>
          <div id="landBlack" :style="{ width: '100%', height: '200px' }"></div>
        </a-tab-pane>
        <a-tab-pane :forceRender="true" key="5" tab="颗粒物" force-render>
          <p style="font-weight: 700">
            排放浓度(mg/m3) [ 2021-10-27 ] <a class="margin-right10">[ 超标分析 ]</a>
            <a class="margin-right10">[ 时长分析 ]</a> <a class="margin-right10">[ 告警分析 ]</a>
          </p>
          <div id="pm" :style="{ width: '100%', height: '200px' }"></div>
        </a-tab-pane>
        <a-tab-pane :forceRender="true" key="6" tab="NmHc">
          <p style="font-weight: 700">
            排放浓度(mg/m3) [ 2021-10-27 ] <a class="margin-right10">[ 超标分析 ]</a>
            <a class="margin-right10">[ 时长分析 ]</a> <a class="margin-right10">[ 告警分析 ]</a>
          </p>
          <div id="nmhc" :style="{ width: '100%', height: '200px' }"></div>
        </a-tab-pane>
        <!-- <a-tab-pane :forceRender="true" key="7" tab="运维">
          <p style="font-weight: 700">
            <a class="margin-right10">[ 运维申请 ]</a>
          </p>
        </a-tab-pane> -->
        <!-- <a-tab-pane ::forceRender="true" key="8" tab="反馈" force-render>
          <p style="font-weight: 700">
            <a class="margin-right10">[ 报警反馈 ]</a>
          </p>
        </a-tab-pane> -->
      </a-tabs>
    </div>
  </a-card>
</template>

<script>
import * as echarts from 'echarts'
import { getAction, postAction } from '@/api/manage'
import { setTimeout } from 'timers'
export default {
  name: 'MapCard',
  components: {},
  props: {
    pointMac: {
      type: String,
      default: '',
      required: false
    }
  },
  data() {
    return {
      tstyle: { color: '#0785fd', 'font-weight': 'bold' },
      cardData: {},
      url: {
        detailList: '/base/getNowTimeDataForMac',
        chartList: '/base/getStatByCreateTime'
      },
      dateStr: ''
    }
  },
  watch: {
    pointMac(val) {
      this.cardData = {}
      this.getCardData()
    }
  },
  created() {},
  computed: {},
  methods: {
    callback(key) {},
    cardClose() {
      this.cardData = {}
      this.$emit('cardOpen', false)
    },
    getCardData() {
      getAction(this.url.detailList, { pageNo: 1, pageSize: 1, pointMac: this.pointMac })
        .then(res => {
          this.cardData = res.result[0]
          this.getChartData()
        })
        .catch(err => {
          err
        })
    },
    getChartData() {
      //chart
      var myChartOne = echarts.init(document.getElementById('landBlack'))
      var myChartTwo = echarts.init(document.getElementById('pm'))
      var myChartThree = echarts.init(document.getElementById('nmhc'))
      getAction(this.url.chartList, { pointMac: this.cardData.pointMac, createTime: this.cardData.createTime })
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
            visualMap: {
              //根据series的data数据 改变折线颜色（待验证）
              show: true,
              align: 'auto',
              type: 'piecewise',
              dimension: 1,
              seriesIndex: 0,
              right: 'center',
              orient: 'horizontal',
              top: 10,
              pieces: [
                {
                  min: 0,
                  max: 1,
                  color: '#fac858'
                },
                {
                  min: 1,
                  max: 2,
                  color: '#91cc75'
                },
                {
                  min: 2,
                  max: 3,
                  color: '#5470c6'
                }
              ]
            },
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
                // name: '2-3',
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
            visualMap: {
              //根据series的data数据 改变折线颜色（待验证）
              show: true,
              align: 'auto',
              type: 'piecewise',
              dimension: 1,
              seriesIndex: 0,
              right: 'center',
              orient: 'horizontal',
              top: 10,
              pieces: [
                {
                  min: 0,
                  max: 5,
                  color: '#fac858'
                },
                {
                  min: 5,
                  max: 8,
                  color: '#91cc75'
                },
                {
                  min: 8,
                  max: 10,
                  color: '#5470c6'
                }
              ]
            },
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
            visualMap: {
              //根据series的data数据 改变折线颜色（待验证）
              show: true,
              align: 'auto',
              type: 'piecewise',
              dimension: 1,
              seriesIndex: 0,
              right: 'center',
              orient: 'horizontal',
              top: 10,
              pieces: [
                {
                  min: 0,
                  max: 10,
                  color: '#fac858'
                },
                {
                  min: 10,
                  max: 15,
                  color: '#91cc75'
                },
                {
                  min: 15,
                  max: 20,
                  color: '#5470c6'
                }
              ]
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
    goWarn() {
      this.$router.push({
        name: 'OnlineMonitor-AlarmList'
      })
    },
    getNowDate() {
      var nowDate = new Date()
      var year = nowDate.getFullYear()
      var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
      var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
      this.dateStr = year + '-' + month + '-' + day
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
    }
  },
  mounted() {
    this.getNowDate()
  }
}
</script>
<style lang="less" scoped>
.title-text-big {
  font-size: 20px;
  color: #fff;
  position: relative;
  a {
    position: absolute;
    right: 10px;
  }
}
.title-text {
  font-size: 14px;
  color: #fff;
  display: flex;
  position: relative;
  span {
    position: absolute;
    right: 10px;
  }
}
& /deep/.ant-card-head {
  background: #1890ff;
}
.p-text {
  display: flex;
  position: relative;
  .p-num {
    display: flex;
    position: absolute;
    right: 0;
    .marker {
      width: 12px;
      height: 12px;
      border: 1px solid #088;
      border-radius: 24px;
      background-color: #3c763d;
      margin: 5px 3px 0px 20px;
    }
  }
}
.point-box {
  display: flex;
  div {
    flex: 1;
  }
}
.href {
  a {
    margin-right: 15px;
    margin-top: 5px;
  }
}
.margin-top15 {
  margin-top: 15px;
}
.margin-right10 {
  margin-right: 10px;
}
.emergency-list {
  display: flex;
  line-height: 30px;
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
  }
}
& /deep/.ant-card-head-title {
  padding: 5px 0 0 0;
}
& /deep/.ant-tabs-nav .ant-tabs-tab {
  padding: 10px 0;
}
@media screen and (max-width: 400px) {
  .map-card {
    width: 100%;
  }
}
@media screen and (min-width: 400px) {
  .map-card {
    width: 600px;
  }
}
</style>
