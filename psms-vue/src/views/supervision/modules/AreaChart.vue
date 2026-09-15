<template>
  <div>
    <div class="develop-chart-style" hoverable>
      <a-card style="height:380px">
        <a-row class="chart-box" :gutter="24">
          <a-col :sm="24" :md="24" :xl="24">
            <div class="chart-left">
              <div class="chart-title">
                <img src="../../../assets/title-point.png" alt="" />
                <span>风控企业地域分布 {{ date }}</span>
              </div>
              <div id="myAreaChart" :style="{ height: '300px' }"></div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { setTimeout } from 'timers'
import store from '@/store/'
import { httpAction, getAction } from '@/api/manage'
export default {
  name: 'AreaChartMoudle',
  components: {},
   props: {
    date: {
      type: String,
      default: ''
    },
  },
  data() {
    return {
      url:{
        barList: '/supervision/getRiskCount'
      }
    }
  },
  created() {},
  computed: {},
  methods: {
    drawBar() {
      getAction(this.url.barList).then(res=>{
         var myChart = echarts.init(document.getElementById('myAreaChart'))
      // 绘制图表
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
        },
        color: ['#fda67e', '#62a8ed',  '#5ddcf6'],
        grid: {
          left: '3%',
          right: '14%',
          bottom: '3%',
          containLabel: true,
        },
        legend: {
          data: ['高风险企业', '中风险企业', '低风险企业'],
          orient: 'vertical',
          top: '10%',
          right:'0px'
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
            name: '高风险企业',
            type: 'bar',
            stack: '使用情况',
            data: [res.result.a1],
            label: {
              show: true,
            },
            barWidth: 58,
          },
          {
            name: '中风险企业',
            type: 'bar',
            stack: '使用情况',
            data: [res.result.a2],
            label: {
              show: true,
            },
            barWidth: 58,
          },
          {
            name: '低风险企业',
            type: 'bar',
            stack: '使用情况',
            data: [res.result.a3],
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
  },
  mounted() {
        this.drawBar()
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
