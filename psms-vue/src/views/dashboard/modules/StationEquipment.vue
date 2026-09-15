<template>
  <div>
    <div class="develop-chart-style test-content" hoverable>
      <a-card class="chart-box">
        <a-row class="chart-box" :gutter="24">
          <a-col :sm="24" :md="24" :xl="24" class="chart-title">
            <img src="../../../assets/title-point.png" alt="" />
            <span>监测设备地域分布 {{ dateStr }}</span>
          </a-col>
          <a-col :sm="24" :md="12" :xl="10">
            <div class="chart-left">
              <div id="myChart-bar" :style="{ width: '100%', height: '300px' }"></div>
            </div>
          </a-col>
          <a-col :sm="24" :md="12" :xl="14">
            <div class="value-box">
              <div class="progress-img">
                <div class="data-value img-1">{{ data.a1 }}</div>
                <div class="text">净化器总数量(台)</div>
              </div>
              <div class="progress-img">
                <div class="data-value img-2">{{ data.a2 }}</div>
                <div class="text">排风机总数量(台)</div>
              </div>
              <div class="progress-img">
                <div class="data-value img-3">{{ data.a3 }}</div>
                <div class="text">检测仪总数量(台)</div>
              </div>
              <div class="progress-img">
                <div class="data-value img-4">{{ data.a4 }}</div>
                <div class="text">电场总数量(台)</div>
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
import { getAction, postAction } from '@/api/manage'
import store from '@/store/'
export default {
  name: 'StationEquipment',
  components: {},
  data() {
    return {
      url: '/home/getEquipmentPoint',
      data: {},
      dateStr: '',
    }
  },
  created() {},
  computed: {},
  methods: {
    drawLine() {
      getAction(this.url).then((res) => {
        this.data = res.result
        var myChart = echarts.init(document.getElementById('myChart-bar'))
        //   绘制图表
        myChart.setOption({
          tooltip: {
            trigger: 'axis',
          },
          color: ['#fda67e', '#62a8ed', '#b9b1f9', '#5ddcf6'],
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          legend: {
            data: ['净化器', '排风机', '监测仪', '电场'],
            top: '10%',
          },
          xAxis: {
            data: [store.getters.userInfo.realname],
            splitLine: {
              show: false,
            },
          },
          yAxis: {
            name: '台',
            splitLine: {
              show: false,
            },
            axisLine: {
              show: true,
            },
          },
          series: [
            {
              name: '净化器',
              type: 'bar',
              stack: '使用情况',
              data: [this.data.a1],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            {
              name: '排风机',
              type: 'bar',
              stack: '使用情况',
              data: [this.data.a2],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            ,
            {
              name: '监测仪',
              type: 'bar',
              stack: '使用情况',
              data: [this.data.a3],
              label: {
                show: true,
              },
              barWidth: 58,
            },
            {
              name: '电场',
              type: 'bar',
              stack: '使用情况',
              data: [this.data.a4],
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
  created() {
    var nowDate = new Date()
    var year = nowDate.getFullYear()
    var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
    var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
    this.dateStr = year + '-' + month + '-' + day
    this.$nextTick(() => {
      setTimeout(() => {
        this.drawLine()
      }, 20)
    })
  },
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 16px;
}
.chart-left {
  display: flex;
}
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
.value-box {
  height: 300px;
  display: flex;
  text-align: center;
  .progress-img {
    flex: 1;
    margin-right: 20px;
    padding-top: 10%;
    text-align: center;
    .img-1 {
      background-image: url('../../../assets/point-total1.png');
      background-repeat: no-repeat;
      background-size: 100% 100%;
    }
    .img-2 {
      background-image: url('../../../assets/point-total2.png');
      background-repeat: no-repeat;
      background-size: 100% 100%;
    }
    .img-3 {
      background-image: url('../../../assets/point-total3.png');
      background-repeat: no-repeat;
      background-size: 100% 100%;
    }
    .img-4 {
      background-image: url('../../../assets/point-total4.png');
      background-repeat: no-repeat;
      background-size: 100% 100%;
    }
    .data-value {
      margin: 0 auto;
      width: 130px;
      height: 130px;
      font-size: 36px;
      font-weight: 400;
      color: #62a8ed;
      line-height: 130px;
    }
    .text {
      width: 80px;
      height: 32px;
      font-size: 14px;
      font-weight: 400;
      color: #666666;
      line-height: 16px;
      margin: 0 auto;
    }
  }
}
</style>
