<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 20px">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-month-picker
                  style="width:100%"
                  allowClear
                  format="YYYY-MM"
                  v-model="pieValue"
                  @change="onDateChange"
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
                  v-model="companyId"
                  style="width: 100%; margin-right: 20px"
                >
                  <a-select-option v-for="(item, index) in companyData" :key="index" :value="item.id">
                    {{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <!-- <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.comStatus" placeholder="请选择服务商" @select="select2">
                  <a-select-option value="1"> 11 </a-select-option>
                  <a-select-option value="2"> 3 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col> -->
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
    <div v-if="show == 0">
      <a-card>
        <h1>请先选择接入单位</h1>
      </a-card>
    </div>
    <div v-if="show == 1">
      <a-spin :spinning="loading">
        <a-row :gutter="24" style="margin-bottom: 20px">
          <a-col :xl="24" :lg="24" :md="24" :sm="24">
            <a-card style="height: 500px" title="月度运行时长（h）TOP30" :bordered="false">
              <div id="myChartO1" :style="{ height: '400px' }"></div>
            </a-card>
          </a-col>
        </a-row>
      </a-spin>
    </div>
    <div v-if="show == 2">
      <a-spin :spinning="loading">
        <a-row :gutter="24">
          <a-col :xl="24" :lg="24" :md="24" :sm="24">
            <a-card style="height: 500px" title="月度运行时长（h）" :bordered="false">
              <div id="myChartO2" :style="{ height: '300px' }"></div>
            </a-card>
          </a-col>
        </a-row>
      </a-spin>
    </div>
  </div>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getAction, postAction } from '@/api/manage'
import * as echarts from 'echarts'

export default {
  name: 'OperationPage',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '运行',
      pieValue: undefined,
      show: 0,
      companyId: undefined,
      url: {
        list: '/',
        companyList: '/company/list',
        list1: '/analyse/runHourTime'
      },
      companyData: [],
      loading: false
    }
  },
  mounted() {
    this.getCompanyData()
    this.drawLine()
  },
  computed: {},
  methods: {
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    getCompanyData() {
      getAction(this.url.companyList).then(res => {
        res.result.forEach(item => {
          this.companyData.push({ id: item.id, name: item.name })
        })
      })
    },
    onDateChange(value, dateString) {
      this.pieValue = dateString
    },
    searchQuery() {
      if (this.companyId) {
        this.show = 2
        this.drawLine3()
      } else {
        this.show = 1
        this.drawLine()
      }
    },
    //
    drawLine() {
      this.loading = true
      setTimeout(() => {
        getAction(this.url.list1, { companyId: this.companyId, format: this.pieValue }).then(res => {
          var myChart1 = echarts.init(document.getElementById('myChartO1'))
          // 绘制图表
          myChart1.setOption({
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            legend: {},
            grid: {
              left: '2%',
              right: '10%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              name: '运行时长(h)',
              data: res.result.dax
            },
            yAxis: {
              type: 'value',
              name: '测点'
            },
            series: [
              {
                type: 'line',
                data: res.result.data
              }
            ]
          })
          this.loading = false
        })
      }, 2000)
    },
    drawLine3() {
      this.loading = true
      getAction(this.url.list1, { companyId: this.companyId, format: this.pieValue })
        .then(res => {
          var myChart3 = echarts.init(document.getElementById('myChartO2'))
          myChart3.setOption({
            tooltip: {
              trigger: 'axis'
            },
            grid: {
              left: '0%',
              right: '1%',
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
              top: 10,
              orient: 'horizontal',
              pieces: [
                {
                  min: 0,
                  max: 5,
                  color: '#009966'
                },
                {
                  min: 5,
                  max: 10,
                  color: '#ffde33'
                },
                {
                  min: 10,
                  max: 15,
                  color: '#ff9933'
                },
                {
                  min: 15,
                  max: 20,
                  color: '#cc0033'
                },
                {
                  min: 20,
                  max: 24,
                  color: '#660099'
                }
              ]
            },
            xAxis: {
              type: 'category',
              name: '/日',
              boundaryGap: false,
              data: res.result.dax
            },
            yAxis: {
              type: 'value',
              name:'h'
            },
            series: [
              {
                type: 'line',
                data: res.result.data
              }
            ]
          })
          this.loading = false
        })
        .catch(err => {
          err
        })
    },
    searchReset() {
      this.show = 0
      this.pieValue = undefined
      this.companyId = undefined
      echarts.init(document.getElementById('myChartO2')).clear()
      this.drawLine()
    }
  }
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
