<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 20px">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
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
                  @change="select2"
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

    <div>
      <a-spin :spinning="loading">
        <a-row :gutter="24">
          <a-col :xl="24" :lg="24" :md="24" :sm="24">
            <a-card title="超标次数" :bordered="false">
              <div id="myChart1" :style="{ height: '200px' }"></div>
              <div id="myChart2" :style="{ height: '200px' }"></div>
              <div id="myChart3" :style="{ height: '200px' }"></div>
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
import * as echarts from 'echarts'
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'IvsWarnPage',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '告警分析',
      pieValue: undefined,
      companyId: undefined,
      companyData: [],
      url: {
        list: '/',
        companyList: '/company/list',
        chartList: '/analyse/alarmAnalysis'
      },
      loading: false
    }
  },
  mounted() {
    this.getCompanyData()
    this.drawLine()
  },
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.pieValue = dateString
      // this.drawLine()
    },
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
    //查询
    searchQuery() {
      this.drawLine()
    },
    drawLine() {
      this.loading = true
      getAction(this.url.chartList, { companyId: this.companyId, format: this.pieValue }).then(res => {
        var myChart1 = echarts.init(document.getElementById('myChart1'))
        var myChart2 = echarts.init(document.getElementById('myChart2'))
        var myChart3 = echarts.init(document.getElementById('myChart3'))
        var pubOptions = {
          tooltip: {
            trigger: 'axis'
          },
          grid: {
            left: '3%',
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
                max: 20,
                color: '#009966'
              },
              {
                min: 20,
                max: 50,
                color: '#ffde33'
              },
              {
                min: 50,
                max: 80,
                color: '#ff9933'
              },
              {
                min: 80,
                max: 110,
                color: '#cc0033'
              },
              {
                min: 110,
                max: 200,
                color: '#660099'
              }
            ]
          },
          xAxis: {
            type: 'category',
            name: '日期',
            boundaryGap: false,
            data: res.result.dax
          },
          yAxis: {
            type: 'value',
            name: ''
          },
          series: {
            type: 'line',
            // stack: 'Total',
            data: []
          }
        }
        myChart1.setOption(
          Object.assign(
            pubOptions,
            { yAxis: { name: '颗粒物超标告警次数' } },
            {
              series: {
                type: 'line',
                // stack: 'Total',
                data: res.result.data1
              }
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
                // stack: 'Total',
                data: res.result.data2
              }
            }
          )
        )
        myChart3.setOption(
          Object.assign(
            pubOptions,
            { yAxis: { name: 'NmHc超标告警次数' } },
            {
              series: {
                type: 'line',
                // stack: 'Total',
                data: res.result.data3
              }
            }
          )
        )
        this.loading = false
      })
    },
    searchReset() {
      this.pieValue = undefined
      this.companyId = undefined
      this.drawLine()
    },
    select2() {
      // this.$nextTick(() => {
      //   this.drawLine()
      // })
    }
  }
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>
