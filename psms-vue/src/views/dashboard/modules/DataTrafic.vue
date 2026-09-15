<template>
  <div>
    <div class="develop-chart-style test-content" hoverable>
      <a-card class="chart-box">
        <div id="myChart-bar-2" :style="{ width: '100%', height: '300px' }"></div>
      </a-card>
      <div class="num-box">
        <div class="num-top">
          <div class="box-blue">
            <p>{{ warnData.a1 }} 家餐企</p>
            <p>设备未联动开启告警</p>
            <a href="javascript:void(0);" @click="goRouter">更多<a-icon style="margin-left: 8px" type="right-circle"/></a>
          </div>
          <div class="box-green">
            <p>{{ warnData.a2 }} 家餐企</p>
            <p>营业时段停机告警</p>
            <a href="javascript:void(0);" @click="goRouter">更多<a-icon style="margin-left: 8px" type="right-circle"/></a>
          </div>
        </div>
        <div class="num-bottom">
          <div class="box-orange">
            <p>{{ warnData.a3 }} 家餐企</p>
            <p>电场工况异常告警</p>
            <a href="javascript:void(0);" @click="goRouter">更多<a-icon style="margin-left: 8px" type="right-circle"/></a>
          </div>
          <div class="box-red">
            <p>{{ warnData.a4 }} 家餐企</p>
            <p>排放超标异常告警</p>
            <a href="javascript:void(0);" @click="goRouter">更多<a-icon style="margin-left: 8px" type="right-circle"/></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { setTimeout } from 'timers'
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'DataTrafic',
  components: {},
  data() {
    return {
      url: {
        chartList: 'home/getFlow',
        warnNum: '/home/getLabel' //告警次数
      },
      warnData: {}
    }
  },
  created() {},
  computed: {},
  methods: {
    //条数
    // tsDrawLine() {
    //   getAction(this.url.chartList).then(res => {
    //     console.log('rrrx', res)

    //   })

    // },
    //流量
    drawLine() {
      getAction(this.url.chartList).then(res => {
        console.log('rrrx', res)
        var myChartOne = echarts.init(document.getElementById('myChart-bar-2'))
        //   绘制图表
        myChartOne.setOption({
          tooltip: {
            trigger: 'axis'
          },
          color: ['#fda77f', '#62a8ed'],
          legend: {
            data: ['累计流量（M）'],
            right: '9%',
            top: '10%'
          },
          grid: {
            left: '2%',
            right: '10%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            name: '/日',
            boundaryGap: false,
            data: res.result.dataX
          },
          yAxis: {
            type: 'value',
            name: '流量（M）',
            splitLine: {
              show: false
            }
          },
          series: [
            // {
            //   name: '本月日均（M）',
            //   type: 'line',
            //   stack: 'Total',
            //   data: [120, 132, 101, 134, 90, 230, 210]
            // },
            {
              name: '累计流量（M）',
              type: 'line',
              stack: 'Total',
              data: res.result.data
            }
          ]
        })
      })
    },
    // callback(key) {
    //   if (key == 1) {
    //     this.$nextTick(() => {
    //       setTimeout(() => {
    //         this.tsDrawLine()
    //       }, 20)
    //     })
    //   } else {
    //     this.$nextTick(() => {
    //       setTimeout(() => {
    //         this.drawLine()
    //       }, 20)
    //     })
    //   }
    // },
    //获取告警次数
    getWarnNum() {
      getAction(this.url.warnNum)
        .then(res => {
          this.warnData = res.result
        })
        .catch(err => {
          err
        })
    },
    //点击更多跳转
    goRouter() {
      this.$router.push({
          name: 'supervision-AdhesiveLabel'
        })
    }
  },
  mounted() {
    this.$nextTick(() => {
      setTimeout(() => {
        this.drawLine()
      }, 20)
    })
    this.getWarnNum()
  }
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 16px;
  .ant-card-body {
    padding: 0;
  }
}
/deep/ .ant-tabs-bar .ant-tabs-tab {
  margin: 0 20px;
}
.test-content {
  display: flex;
  // width: 100%;
  .chart-box {
    flex: 1 1 60%;
    .type-btn {
      display: flex;
      .btn {
        flex: 1;
        margin-left: 20px;
        color: #444444;
        font-size: 14px;
      }
    }
  }
  .num-box {
    display: flex;
    flex: 1 1 40%;
    flex-direction: column;
    padding-left: 30px;
    box-sizing: border-box;
    color: #ffffff;
    font-size: 16px;
    .num-top,
    .num-bottom {
      flex: 1;
      display: flex;
      div {
        flex: 1;
        text-align: center;
        padding-top: 30px;
      }
      a {
        color: #ffffff;
      }
    }
    .box-blue {
      background-color: #00c0ef;
      border-right: 15px solid #f0f2f5;
      border-bottom: 10px solid #f0f2f5;
    }
    .box-green {
      background-color: #00a65a;
      border-left: 15px solid #f0f2f5;
      border-bottom: 10px solid #f0f2f5;
    }
    .box-orange {
      background-color: #f39c12;
      border-right: 15px solid #f0f2f5;
      border-top: 10px solid #f0f2f5;
    }
    .box-red {
      background-color: #dd4b39;
      border-left: 15px solid #f0f2f5;
      border-top: 10px solid #f0f2f5;
    }
  }
}
</style>
