<template>
  <div>
    <div class="develop-chart-style test-content" hoverable>
      <a-row class="chart-box" :gutter="24">
        <a-col :sm="12" :md="12" :xl="12">
          <a-card>
            <a-row :gutter="12">
              <a-col :sm="12" :md="12" :xl="12" class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>企业地域分布 {{ dateStr }}</span>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :sm="24" :md="24" :xl="24" class="chart-title">
                <div>
                  <div id="myChart-bar-shop" :style="{ width: '100%', height: '300px' }"></div>
                </div>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
        <a-col :sm="12" :md="12" :xl="12">
          <a-card>
            <a-row :gutter="24">
              <a-col :sm="24" :md="24" :xl="24" class="chart-title">
                <div class="type-btn" style="color: #b2a9ce">
                  <div class="type-box">
                    <a href="javascript:void(0);" id="jy" class="btn" @click="getJyData">经营类别</a>
                    <a href="javascript:void(0);" id="dw" class="btn" @click="getDwData">单位类型</a>
                  </div>
                </div>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :sm="24" :md="24" :xl="24">
                <div
                  v-if="jyShow"
                  class="pie-chart"
                  id="myChart-pie-jy"
                  :style="{ width: '100%', height: '300px' }"
                ></div>
                <div
                  v-if="dwShow"
                  class="pie-chart"
                  id="myChart-pie-dw"
                  :style="{ width: '100%', height: '300px' }"
                ></div>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getAction, postAction } from '@/api/manage'
import store from '@/store/'
export default {
  name: 'CateringEnterprise',
  components: {},
  data() {
    return {
      jyShow: true,
      dwShow: false,
      url: {
        pieList: '/home/companyStat',
        barLisr: '/home/companyStat',
      },
      jyPie: [],
      dwPie: [],
      dateStr: '',
    }
  },
  created() {},
  computed: {},
  methods: {
    drawLine() {
      getAction(this.url.pieList).then((res) => {
        let obj = res.result.a2
        let arr = []
        Object.getOwnPropertyNames(obj).forEach((key) => {
          arr.push({ name: key, value: obj[key] })
        })
        var myChart = echarts.init(document.getElementById('myChart-bar-shop'))
        //   绘制图表
        myChart.setOption({
          tooltip: {
            trigger: 'axis',
          },
          color: ['#fda67e', '#62a8ed', '#b9b1f9', '#5ddcf6'],
          grid: {
            left: '3%',
            right: '5%',
            bottom: '3%',
            containLabel: true,
          },
          legend: {
            data: ['餐饮经营单位', '集体用餐配送单位', '单位食堂', '中央厨房'],
            top: '10%',
          },
          xAxis: {
            data: [store.getters.userInfo.realname],
            splitLine: {
              show: false,
            },
          },
          yAxis: {
            // name: '台',
            splitLine: {
              show: false,
            },
            axisLine: {
              show: true,
            },
          },
          series: [
            {
              name: '餐饮经营单位',
              type: 'bar',
              stack: '使用情况',
              data: [arr[0].value],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            {
              name: '集体用餐配送单位',
              type: 'bar',
              stack: '使用情况',
              data: [arr[1].value],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            {
              name: '单位食堂',
              type: 'bar',
              stack: '使用情况',
              data: [arr[2].value],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            ,
            {
              name: '中央厨房',
              type: 'bar',
              stack: '使用情况',
              data: [arr[3].value],
              label: {
                show: true,
              },
              barWidth: 58,
              itemStyle: { barBorderRadius: [8, 8, 0, 0] },
            },
          ],
        })
      })
    },
    getJyData() {
      this.jyShow = true
      this.dwShow = false
      document.getElementById('jy').style.color = '#72afd2'
      document.getElementById('dw').style.color = '#444444'
      this.getJyPie()
    },
    getDwData() {
      this.jyShow = false
      this.dwShow = true
      document.getElementById('jy').style.color = '#444444'
      document.getElementById('dw').style.color = '#72afd2'
      this.getDwPie()
    },
    getJyPie() {
      this.jyPie = []
      getAction(this.url.pieList).then((res) => {
        let obj = res.result.a1
        Object.getOwnPropertyNames(obj)
          .reverse()
          .forEach((key) => {
            this.jyPie.push({ name: key, value: obj[key] })
          })
        var myChartPieJy = echarts.init(document.getElementById('myChart-pie-jy'))
        //   绘制图表
        myChartPieJy.setOption({
          tooltip: {
            trigger: 'item',
          },
          legend: {
            orient: 'vertical',
            top: '20%',
            right: '10%',
          },
          color: ['#fda67e', '#62a8ed', '#b9b1f9', '#5ddcf6', '#f5779e'],
          series: [
            {
              type: 'pie',
              radius: '70%',
              // roseType: 'area',
              data: this.jyPie,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
              },
            },
          ],
        })
      })
    },
    getDwPie() {
      this.dwPie = []
      getAction(this.url.pieList).then((res) => {
        let obj = res.result.a2
        Object.getOwnPropertyNames(obj).forEach((key) => {
          this.dwPie.push({ name: key, value: obj[key] })
        })
        var myChartPieDw = echarts.init(document.getElementById('myChart-pie-dw'))
        //   绘制图表
        myChartPieDw.setOption({
          legend: {
            orient: 'vertical',
            top: '20%',
            right: '5%',
          },
          tooltip: {
            trigger: 'item',
          },
          color: ['#fda67e', '#62a8ed', '#b9b1f9', '#5ddcf6', '#f5779e'],
          series: [
            {
              type: 'pie',
              radius: '70%',
              // roseType: 'area',
              data: this.dwPie,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
              },
            },
          ],
        })
      })
    },
  },
  mounted() {
    var nowDate = new Date()
    var year = nowDate.getFullYear()
    var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
    var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
    this.dateStr = year + '-' + month + '-' + day
    this.$nextTick(() => {
      setTimeout(() => {
        this.getJyPie()
        this.getDwPie()
      }, 20)
      setTimeout(() => {
        this.drawLine()
      }, 30)
    })
  },
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 16px;
}
.chart-box {
  // height: 400px;
  .chart-title {
    span {
      width: 244px;
      height: 18px;
      font-size: 18px;
      font-weight: 400;
      color: #333333;
      line-height: 27px;
    }
  }
  .tab {
    display: flex;
    flex-direction: column;
  }
  .type-btn {
    display: flex;
    position: relative;
    z-index: 100;
    height: 29px;
    .type-box {
      position: absolute;
      right: 10px;
      .btn {
        margin-left: 20px;
        width: 80px;
        height: 20px;
        font-size: 20px;
        font-weight: 400;
        color: #333333;
        line-height: 30px;
      }
    }
  }
}
</style>
