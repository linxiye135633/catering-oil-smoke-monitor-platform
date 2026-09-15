<template>
  <div>
    <div class="develop-chart-style" hoverable>
      <a-card>
        <a-row class="chart-box" :gutter="24">
          <a-col :sm="24" :md="16" :xl="16">
            <div class="chart-left">
              <div class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>风控企业增长趋势 {{ date }}</span>
              </div>
              <div id="myChart" :style="{ height: '300px' }"></div>
            </div>
          </a-col>
          <a-col :sm="24" :md="8" :xl="8">
            <div class="chart-right">
              <div class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>风控企业各级别占比 {{ date }}</span>
              </div>
              <div id="myPieChart" :style="{ height: '300px' }"></div>
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
import { httpAction, getAction } from '@/api/manage'
export default {
  name: 'WindChartMoudle',
  components: {
    MiniProgress
  },
   props: {
    date: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      url: {
        chartList: '/supervision/getRiskLineChart',
        pieList: '/supervision/getRiskCount'
      }
    }
  },
  created() {},
  computed: {},
  methods: {
    drawLine() {
      getAction(this.url.chartList).then(res => {
        var myChart = echarts.init(document.getElementById('myChart'))
        // 绘制图表
        myChart.setOption({
          tooltip: {
            trigger: 'axis'
          },
          color: ['#fda982', '#62a8ed', '#5ddcf6'],
          legend: {
            data: ['高风险企业', '中风险企业', '低风险企业'],
            right: '12%',
            top: '10%'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            name: '月份',
            data: res.result.dax
          },
          yAxis: {
            type: 'value',
            name: '数量',
            splitLine: { show: false },
            axisLine: {
              show: true
            }
          },
          series: [
            {
              name: '高风险企业',
              type: 'line',
              data: res.result.data1
            },
            {
              name: '中风险企业',
              type: 'line',
              data: res.result.data2
            },
            {
              name: '低风险企业',
              type: 'line',
              data: res.result.data3
            }
          ]
        })
      })
    },
    drawPie() {
      getAction(this.url.pieList).then(res=>{
           var myChart = echarts.init(document.getElementById('myPieChart'))
      // 绘制图表
      myChart.setOption({
        tooltip: {
          trigger: 'item'
        },
        color: ['#fda67e', '#62a8ed', '#5ddcf6'],
        series: [
          {
            name: '风控企业各级别占比',
            type: 'pie',
            radius: '70%',
            data: [
              { value: res.result.a1, name: '高风险企业' },
              { value: res.result.a2, name: '中风险企业' },
              { value: res.result.a3, name: '低风险企业' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
      })
    }
  },
  mounted() {
      this.drawLine()
      this.drawPie()
  }
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
