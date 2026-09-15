<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 20px">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-range-picker format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange">
                </a-range-picker>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.comStatus" placeholder="请选择服务商" @select="select2">
                  <a-select-option value="1"> 11 </a-select-option>
                  <a-select-option value="2"> 3 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.comStatuss" placeholder="请选择接入单位" @select="select3">
                  <a-select-option value="1"> 22 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <div v-if="show == 1">
      <a-row :gutter="24" style="margin-bottom: 20px">
        <a-col :xl="24" :lg="8" :md="24" :sm="24">
          <a-card style="height: 200px" title="请选择以上查询条件" :bordered="false">
            <h2>未查询到统计数据</h2>
            <h1>-请确认所选择的查询条件</h1>
          </a-card>
        </a-col>
      </a-row>
    </div>
    <div v-if="show == 2">
      <a-row :gutter="24" style="margin-bottom: 20px">
        <a-col :xl="12" :lg="12" :md="12" :sm="24">
          <a-card style="height: 500px" title="告警事件次数占比" :bordered="false">
            <a-table :columns="columns" :pagination="false" :border="false" :data-source="dataSourse">
              <a slot="name" slot-scope="text">{{ text }}</a>
            </a-table>
          </a-card>
        </a-col>
        <a-col :xl="12" :lg="12" :md="12" :sm="24">
          <a-card style="height: 500px" title="月度告警次数TOP20" :bordered="false">
            <div id="myChart" :style="{ height: '400px' }"></div>
          </a-card>
        </a-col>
      </a-row>
    </div>
    <div v-if="show == 3">
      <a-row :gutter="24">
        <a-col :xl="24" :lg="24" :md="24" :sm="24">
          <a-card title="超标次数" :bordered="false">
            <div id="myChart1" :style="{ height: '200px' }"></div>
            <div id="myChart2" :style="{ height: '200px' }"></div>
            <div id="myChart3" :style="{ height: '200px' }"></div>
            <div id="myChart4" :style="{ height: '200px' }"></div>
            <div id="myChart5" :style="{ height: '200px' }"></div>
            <div id="myChart6" :style="{ height: '200px' }"></div>
            <div id="myChart7" :style="{ height: '200px' }"></div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import * as echarts from 'echarts'

export default {
  name: 'IvsWarnPage',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '告警分析',
      pieValue: [],
      endTime: '',
      startTime: '',
      show: 1,
      columns: [
        {
          title: '告警事件类型',
          align: 'center',
          dataIndex: 'itemTtypeext',
        },
        {
          title: '次数',
          align: 'center',
          dataIndex: 'itemValue',
        },
        {
          title: '占比',
          align: 'center',
          dataIndex: 'itemValues',
        },
      ],
      dataSourse: [
        {
          itemTtypeext: '颗粒物超标告警',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: '油烟超标告警',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: 'NmHc超标告警',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: '排风机停机告警',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: '净化器停机告警	',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: '净化器压差异常告警',
          itemValue: '0',
          itemValues: '0',
        },
        {
          itemTtypeext: '电场异常告警',
          itemValue: '0',
          itemValues: '0',
        },
      ],
    }
  },
  mounted() {},
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    drawLine() {
      var myChart = echarts.init(document.getElementById('myChart'))
      // 绘制图表
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
        },
        color: ['#fda67e', '#65a9ed'],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          data: [0, 0.2, 0.4, 0.6, 0.8, 1],
        },
        yAxis: {
          type: 'value',
          //   name: '数量',
          splitLine: { show: false },
          axisLine: {
            show: true,
          },
        },
        series: [
          {
            type: 'line',
            stack: 'Total',
            data: [],
          },
        ],
      })
    },
    drawLine3() {
      var myChart1 = echarts.init(document.getElementById('myChart1'))
      var myChart2 = echarts.init(document.getElementById('myChart2'))
      var myChart3 = echarts.init(document.getElementById('myChart3'))
      var myChart4 = echarts.init(document.getElementById('myChart4'))
      var myChart5 = echarts.init(document.getElementById('myChart5'))
      var myChart6 = echarts.init(document.getElementById('myChart6'))
      var myChart7 = echarts.init(document.getElementById('myChart7'))
      var pubOptions = {
        tooltip: {
          trigger: 'axis',
        },
        grid: {
          left: '3%',
          right: '1%',
          bottom: '3%',
          containLabel: true,
        },
        visualMap: {
          //根据series的data数据 改变折线颜色（待验证）
          show: true,
          align: 'auto',
          type: 'piecewise',
          dimension: 1,
          seriesIndex: 0,
          right: 'center',
          top: 10,
          orient: 'horizontal',
          pieces: [
            {
              min: 0,
              max: 20,
              color: '#009966',
            },
            {
              min: 20,
              max: 50,
              color: '#ffde33',
            },
            {
              min: 50,
              max: 80,
              color: '#ff9933',
            },
            {
              min: 80,
              max: 110,
              color: '#cc0033',
            },
            {
              min: 110,
              max: 200,
              color: '#660099',
            },
          ],
        },
        xAxis: {
          type: 'category',
          name: '日期',
          boundaryGap: false,
          data: [
            '01',
            '02',
            '03',
            '04',
            '05',
            '06',
            '07',
            '08',
            '09',
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
            '24',
            '25',
            '26',
            '27',
            '28',
            '29',
            '30',
          ],
        },
        yAxis: {
          type: 'value',
          name: '',
        },
        series: {
          type: 'line',
          stack: 'Total',
          data: [],
        },
      }
      myChart1.setOption(
        Object.assign(
          pubOptions,
          { yAxis: { name: '颗粒物超标告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [21, 22],
            },
          }
        )
      )
      myChart2.setOption(
        Object.assign(
          pubOptions,
          { yAxis: { name: '油烟超标告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [112, 23],
            },
          }
        )
      )
      myChart3.setOption(Object.assign(
          pubOptions,
          { yAxis: { name: 'NmHc超标告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [40, 10,60],
            },
          }
        ))
      myChart4.setOption(Object.assign(
          pubOptions,
          { yAxis: { name: '排风机停机告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [22, 62],
            },
          }
        ))
      myChart5.setOption(Object.assign(
          pubOptions,
          { yAxis: { name: '净化器停机告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [21, 22],
            },
          }
        ))
      myChart6.setOption(Object.assign(
          pubOptions,
          { yAxis: { name: '压差异常告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [21, 22],
            },
          }
        ))
      myChart7.setOption(Object.assign(
          pubOptions,
          { yAxis: { name: '电场异常告警次数' } },
          {
            series: {
              type: 'line',
              stack: 'Total',
              data: [21, 22],
            },
          }
        ))
    },
    searchReset() {
      this.show = 1
    },
    select2() {
      this.show = 2
      this.$nextTick(() => {
        this.drawLine()
      })
    },
    select3() {
      this.show = 3
      this.$nextTick(() => {
        this.drawLine3()
      })
    },
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
