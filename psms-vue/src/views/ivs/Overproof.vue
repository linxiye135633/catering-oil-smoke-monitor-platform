<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 20px" title="超标次数分析">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-month-picker
                  style="width: 100%"
                  allowClear
                  format="YYYY-MM"
                  v-model="timeRange"
                  @change="onDateChange1"
                >
                </a-month-picker>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select
                  show-search
                  placeholder="请选择接入单位"
                  option-filter-prop="children"
                  :filter-option="filterOption"
                  allowClear
                  v-model="companyName1"
                  @change="companyNameChange1"
                  style="width: 100%; margin-right: 20px"
                >
                  <a-select-option v-for="item in companyData1" :key="item.id" :value="item.name">
                    {{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-select
                v-model="pointName1"
                placeholder="请选择一个测点"
                @change="handleChangePoint1"
                style="width: 100%; margin-right: 20px"
              >
                <a-select-option v-for="item in addressData1" :key="item.id" :value="item.pointMac">
                  {{ item.pointMac }}
                </a-select-option>
              </a-select>
            </a-col>
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select
                  placeholder="请选择污染源"
                  v-model="badName1"
                  @change="selecteBadChange1"
                  style="width: 100%; margin-right: 20px"
                >
                  <a-select-option value="油烟" key="01"> 油烟 </a-select-option>
                  <a-select-option value="非甲烷总烃" key="02"> 非甲烷总烃 </a-select-option>
                  <a-select-option value="颗粒物" key="03"> 颗粒物 </a-select-option>
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
    <a-card
      :bordered="false"
      :title="(companyName1 == undefined ? '' : companyName1) + '超标次数(天)'"
      style="overflow: scroll"
    >
      <div style="width: 1650px">
        <div id="myChartTimesByDay" :style="{ height: '350px', width: '100%' }"></div>
      </div>
    </a-card>
    <a-card
      :bordered="false"
      :title="(companyName1 == undefined ? '' : companyName1) + '超标次数(小时)'"
      style="overflow: scroll"
    >
      <div style="width: 1650px">
        <div id="myChartTimesByHour" :style="{ height: '550px', width: '100%' }"></div>
      </div>
    </a-card>
    <!-- 污染走势 -->
    <a-card :bordered="false" style="margin-bottom: 20px" title="污染走势分析">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :xl="4" :lg="6" :md="6" :sm="12">
              <a-form-item>
                <a-button type="primary" style="margin-right: 10px" @click="changeSearchTime('YYYY', '月')"
                  >年</a-button
                >
                <a-button type="primary" style="margin-right: 10px" @click="changeSearchTime('YYYY-MM', '天')"
                  >月</a-button
                >
                <a-button type="primary" @click="changeSearchTime('YYYY-MM-DD', '小时')">日</a-button>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-time-picker
                  style="width: 100%"
                  :format="searchTime"
                  v-model="timeRange"
                  @change="onDateChange2"
                >
                </a-time-picker>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select
                  placeholder="请选择污染源"
                  v-model="badName2"
                  @change="selecteBadChange2"
                  style="width: 100%; margin-right: 20px"
                >
                  <a-select-option value="油烟" key="01"> 油烟 </a-select-option>
                  <a-select-option value="非甲烷总烃" key="02"> 非甲烷总烃 </a-select-option>
                  <a-select-option value="颗粒物" key="03"> 颗粒物 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select
                  show-search
                  placeholder="请选择接入单位"
                  option-filter-prop="children"
                  :filter-option="filterOption"
                  allowClear
                  v-model="companyName2"
                  @change="selecteChange"
                  style="width: 100%; margin-right: 20px"
                >
                  <a-select-option v-for="item in companyData" :key="item.id" :value="item.name">
                    {{ item.name }}
                  </a-select-option>
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
    <a-card
      :bordered="false"
      :title="
        (companyName2 == undefined ? '' : companyName2) +
        '污染走势(' +
        badTime +
        ')' +
        (badName == undefined ? '' : '---' + badName)
      "
      style="overflow: scroll"
    >
      <div style="width: 1650px">
        <div id="myChartBad" :style="{ height: '450px' }"></div>
      </div>
    </a-card>
  </div>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import * as echarts from 'echarts'
import { getAction } from '@/api/manage'
import { setTimeout } from 'timers'

export default {
  name: '超标分析',
  mixins: [mixinDevice],
  data() {
    return {
      description: '超标分析',
      companyData1: [], //公司列表
      companyData2: [], //公司列表
      addressData1: [], //测点列表
      addressData2: [], //测点列表
      timeRange: '', //查询条件-时间范围
      searchTime: 'YYYY-MM', //查询条件-时间范围
      companyId1: '', //查询条件-公司id
      companyId2: '', //查询条件-公司id
      companyName1: undefined, //查询条件-公司
      companyName2: undefined, //查询条件-公司
      pointName1: undefined, //测点查询
      pointName2: undefined, //测点查询
      pointId1: '', //测点查询
      pointId2: '', //测点查询
      badTime: '天', //污染源查询
      badName1: undefined, //污染源查询
      badName2: undefined, //污染源查询
      badId1: '', //污染源查询
      badId2: '', //污染源查询
      url: {
        companyList: '/company/list', //单位列表接口
        pointList: '/base/getPointByCompanyId', //测点列表接口
      },
    }
  },
  mounted() {
    this.setTime1() //设置默认时间
    this.setTime2() //设置默认时间
    this.getCompanyData1() //获取公司列表-默认展示第一个-获取测点列表-默认展示第一个
    this.getCompanyData2() //获取公司列表-默认展示第一个-获取测点列表-默认展示第一个
    this.drawChart1() //首次加载
    this.drawChart2() //首次加载
    this.drawChart3() //首次加载
  },
  methods: {
    setTime1() {},
    setTime2() {},
    //获取公司列表-并设置公司默认选中第一个公司
    getCompanyData1() {
      getAction(this.url.companyList).then((res) => {
        this.companyData1 = res.result
      })
      if (this.companyData1.length > 0) {
        this.companyName1 = this.companyData1[0].name
      }
    },
    getCompanyData2() {
      getAction(this.url.companyList).then((res) => {
        this.companyData2 = res.result
      })
    },
    //获取测点列表-并设置测点默认选中第一个测点
    getPointList1() {
      this.addressData1 = undefined
      this.pointId1 = undefined
      getAction(this.url.pointList, { companyid: this.companyId1 }).then((res) => {
        this.addressData1 = res.result.records
      })
      if (this.addressData1.length > 0) {
        this.pointName1 = this.addressData1[0].pointMac
      }
    },
    getPointList2() {
      this.addressData2 = undefined
      this.pointId2 = undefined
      getAction(this.url.pointList, { companyid: this.companyId2 }).then((res) => {
        this.addressData2 = res.result.records
      })
    },
    drawChart1() {
      var myChartTimesByDay = echarts.init(document.getElementById('myChartTimesByDay'))
      var optionsTimesByDay = {
        tooltip: {
          trigger: 'axis',
        },
        visualMap: {
          orient: 'horizontal',
          left: 'center',
          type: 'piecewise',
          pieces: [
            { min: 0, max: 20, color: 'rgb(0,214,97)' },
            { min: 21, max: 40, color: 'rgb(0,127,235)' },
            { min: 41, max: 60, color: 'rgb(255,112,0)' },
            { min: 61, max: 80, color: 'rgb(255,0,0)' },
            { min: 80, color: 'rgb(190,0,174)' },
          ],
        },
        toolbox: {
          show: true,
          feature: {
            magicType: { show: true, type: ['line', 'bar'] },
            saveAsImage: { show: true },
          },
        },
        calculable: true,
        xAxis: [
          {
            type: 'category',
            data: [
              1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
              30, 31,
            ],
          },
        ],
        yAxis: [
          {
            type: 'value',
          },
        ],
        series: [
          {
            type: 'bar',
            data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
            markLine: {
              data: [{ type: 'average', name: 'Avg' }],
            },
          },
        ],
      }
      myChartTimesByDay.setOption(optionsTimesByDay)
    },
    drawChart3() {
      var myChartBad = echarts.init(document.getElementById('myChartBad'))
      var optionsBad = {
        tooltip: {
          trigger: 'axis',
        },
        toolbox: {
          show: true,
          feature: {
            magicType: { show: true, type: ['line', 'bar'] },
            saveAsImage: { show: true },
          },
        },
        calculable: true,
        xAxis: [
          {
            name: '时间',
            type: 'category',
            data: [
              1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
              30, 31,
            ],
          },
        ],
        yAxis: [
          {
            name: 'mg/m3',
            type: 'value',
          },
        ],
        series: [
          {
            type: 'bar',
            data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' },
              ],
            },
          },
        ],
      }
      myChartBad.setOption(optionsBad)
    },
    drawChart2() {
      var myChartTimesByHour = echarts.init(document.getElementById('myChartTimesByHour'))
      var data = [
        [0, 0, 1],
        [0, 1, 1],
        [19, 2, 1],
        [2, 3, 1],
        [23, 4, 6],
        [8, 5, 1],
        [3, 6, 1],
        [1, 7, 2],
        [6, 8, 1],
        [9, 9, 1],
        [16, 10, 1],
        [15, 11, 1],
        [20, 18, 1],
        [0, 13, 8],
        [20, 14, 1],
        [0, 15, 1],
        [20, 20, 50],
      ]
      var optionsTimesByHour = {
        tooltip: {
          trigger: 'axis',
        },
        visualMap: {
          orient: 'horizontal',
          left: 'center',
          type: 'piecewise',
          pieces: [
            { min: 0, max: 4, color: 'rgb(0,214,97)' },
            { min: 5, max: 8, color: 'rgb(0,127,235)' },
            { min: 9, max: 12, color: 'rgb(255,112,0)' },
            { min: 13, max: 16, color: 'rgb(255,0,0)' },
            { min: 17, color: 'rgb(190,0,174)' },
          ],
        },
        toolbox: {
          show: true,
          feature: {
            magicType: { show: true, type: ['line', 'bar'] },
            saveAsImage: { show: true },
          },
        },
        calculable: true,
        xAxis: [
          {
            type: 'category',
            data: [
              1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
              30, 31,
            ],
            splitArea: {
              show: true,
            },
          },
        ],
        yAxis: [
          {
            type: 'category',
            data: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
            splitArea: {
              show: true,
            },
          },
        ],
        series: [
          {
            name: '超标次数',
            type: 'heatmap',
            data: data,
            label: {
              show: true,
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      }
      myChartTimesByHour.setOption(optionsTimesByHour)
    },
    changeSearchTime(i, k) {
      this.searchTime = i
      this.badTime = k
    },
    selecteChange1(value, id) {
      this.companyName1 = value
      this.companyId1 = id.data.key
      this.getPointList1() //查询测点
    },
    selecteChange2(value, id) {
      this.companyName2 = value
      this.companyId2 = id.data.key
      this.getPointList2() //查询测点
    },

    selecteBadChange(value, id) {
      this.badName = value
      this.badId = id.data.key
    },

    searchQuery() {},
    onDateChange(value, dateString) {
      this.timeRange = dateString
    },
    searchReset() {
      this.timeRange = undefined
      this.companyId = undefined
      this.companyName = undefined
      echarts.init(document.getElementById('myChartTimesByDay')).clear()
      echarts.init(document.getElementById('myChartTimesByHour')).clear()
      echarts.init(document.getElementById('myChartBad')).clear()
    },
    //下拉框搜索功能
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
