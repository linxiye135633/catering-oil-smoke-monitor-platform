<template>
  <div>
    <div class="develop-chart-style" hoverable>
      <a-card>
        <a-row class="chart-box" :gutter="24">
          <a-col :sm="24" :md="14" :xl="14">
            <div class="chart-left">
              <div class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>餐饮企业接入趋势 {{dateStr}}</span>
              </div>
              <div id="myChart" :style="{ height: '300px' }"></div>
            </div>
          </a-col>
          <a-col :sm="24" :md="10" :xl="10">
            <div class="chart-right">
              <div class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>本月安装接入进度</span>
              </div>
              <div class="progress-main">
                <div class="progress-bar">
                  <p>
                    <span>餐饮企业安装进度（已接入/总数量）</span
                    ><a-progress
                      :strokeColor="'#62A8ED'"
                      :percent="topPer"
                    />
                  </p>
                  <p>
                    <span>测点调试接入进度（已接入/总数量）</span
                    ><a-progress
                      :strokeColor="'#FDA67E'"
                      :percent="bottomPer"
                    />
                  </p>
                </div>
                <div class="progress-pie">
                  <div class="progress-img">
                    <div class="data-value">{{ progressData.a5 }}</div>
                    <div class="text">累计待接入测点数</div>
                  </div>
                  <div class="progress-img">
                    <div class="data-value">{{ progressData.a6 }}</div>
                    <div class="text">累计待安装餐饮企业数</div>
                  </div>
                  <div class="progress-img">
                    <div class="data-value">{{ progressData.a7 }}</div>
                    <div class="text">本月已接入测点数</div>
                  </div>
                  <div class="progress-img">
                    <div class="data-value">{{ progressData.a8 }}</div>
                    <div class="text">本月已安装餐饮企业数</div>
                  </div>
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import MiniProgress from '@/components/chart/MiniProgress'
import { setTimeout } from 'timers'
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'DevelopChartMoudle',
  components: {
    MiniProgress,
  },
  data() {
    return {
      url: {
        list: '/home/statiJoinYear',
        progressList: '/home/getRateProgress',
      },
      progressData: {},
      topPer:0,
      bottomPer:0,
      dateStr:''
    }
  },
  created() {},
  computed: {
    
  },
  methods: {
    drawLine() {
      var myChart = echarts.init(document.getElementById('myChart'))
      let data1 = []
      let data2 = []
      let xData = []
      getAction(this.url.list).then((res) => {
        data1 = res.result.data1
        data2 = res.result.data2
        xData = res.result.dataX
        // 绘制图表
        myChart.setOption({
          tooltip: {
            trigger: 'axis',
          },
          color: ['#fda67e', '#65a9ed'],
          legend: {
            data: ['排口测点', '餐饮单位'],
            right: '12%',
            top: '10%',
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'category',
            name: '月份',
            data: xData,
          },
          yAxis: {
            type: 'value',
            name: '数量',
            splitLine: { show: false },
            axisLine: {
              show: true,
            },
          },
          series: [
            {
              name: '排口测点',
              type: 'line',
              data: data1,
            },
            {
              name: '餐饮单位',
              type: 'line',
              data: data2,
            },
          ],
        })
      })
    },
    getProgressData() {
      getAction(this.url.progressList)
        .then((res) => {
          this.progressData = res.result
          this.topPer = parseInt(res.result.a1/res.result.a2*100)
          this.bottomPer = parseInt(res.result.a3/res.result.a4*100)
        })
        .catch(err=>{
          err
        })
    }
  },
  mounted() {
     var nowDate = new Date()
    var year = nowDate.getFullYear()
    var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
    var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
    this.dateStr = year + '-' + month + '-' + day
    this.getProgressData()
    this.$nextTick(()=>{
      setTimeout(()=>{
           this.drawLine()
      },20)
    })
  },
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 16px;
}
.chart-box {
  .chart-left {
    display: flex;
    flex-direction: column;
  }
  .chart-right {
    display: flex;
    flex-direction: column;
    position: relative;
    .progress-main {
      .progress-bar {
        margin-top: 4%;
        p {
          display: flex;
          margin-bottom: 8%;
          span {
            width: 460px;
            height: 14px;
            font-size: 14px;
            font-family: SourceHanSansCN-Normal, SourceHanSansCN;
            font-weight: 400;
            color: #666666;
            line-height: 21px;
          }
        }
      }
      height: 300px;
      .progress-pie {
        display: flex;
        text-align: center;
        .progress-img {
          flex: 1;
          .text {
            width: 80px;
            height: 32px;
            font-size: 14px;
            font-family: SourceHanSansCN-Normal, SourceHanSansCN;
            font-weight: 400;
            color: #666666;
            line-height: 16px;
            margin: 0 auto;
          }
          .data-value {
            margin: 0 auto;
            width: 83px;
            height: 83px;
            font-size: 36px;
            font-family: SourceHanSansCN-Normal, SourceHanSansCN;
            font-weight: 400;
            color: #62a8ed;
            line-height: 83px;
            background-image: url('../../../assets/dataNum.png');
          }
        }
      }
    }
  }
  .chart-title {
    span {
      // width: 244px;
      height: 18px;
      font-size: 18px;
      font-family: SourceHanSansCN-Normal, SourceHanSansCN;
      font-weight: 400;
      color: #333333;
      line-height: 27px;
    }
  }
}
</style>
