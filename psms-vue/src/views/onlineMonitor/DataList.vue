<template>
  <div class="develop-chart-style test-content" hoverable>
    <a-card :bordered="false">
      <div class="search-header">
        <a-select
          v-model="companyId"
          show-search
          placeholder="请选择一个接入单位"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangeCompany"
          allowClear
          style="width: 40%; margin-right: 20px"
        >
          <a-select-option v-for="(item, index) in searchData" :key="index" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-select
          v-if="companyId != undefined"
          v-model="pointId"
          show-search
          placeholder="请选择一个测点"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangePoint"
          allowClear
          style="width: 16%; margin-right: 20px"
        >
          <a-select-option v-for="(item, index) in addressData" :key="index + item.id" :value="item.name">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-input
          style="width: 16%; margin-right: 20px"
          v-if="companyId == undefined"
          placeholder="请选择一个测点"
          v-model="pointId"
          allowClear
        ></a-input>
        <a-range-picker format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange">
        </a-range-picker>
        <a-button style="margin-left: 20px" icon="search" @click="searchDatas" />
        <a-button style="margin-left: 20px" type="primary" @click="clear">重置</a-button>
      </div>
      <a-list :loading="loading" item-layout="horizontal" :data-source="detailList" :pagination="pagination">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-list-item-meta>
            <a slot="title">
              <div>
                <a-collapse accordion :expand-icon-position="'right'">
                  <a-collapse-panel key="1" v-model="activeKey">
                    <div class="header-row" slot="header">
                      <a-icon style="margin-top: 4px" type="windows" />
                      <span class="header-name" :style="getColor(item.createTime)">
                         [{{ index + 1 }}] [{{ item.pointMac }}]-({{ item.name }})/{{ item.manufacturerCode }}
                      </span>
                      <div class="header-circle">
                        <div class="marker"></div>
                        <span :style="getColor(item.createTime)">净化器</span>
                        <div class="marker"></div>
                        <span :style="getColor(item.createTime)">排风机</span>
                        <div :class="item.lampblackData > 1 ? 'marker-red' : 'marker-green'"></div>
                        <span :style="getColor(item.createTime)">油烟</span>
                        <div :class="item.matterData > 5 ? 'marker-red' : 'marker-green'"></div>
                        <span :style="getColor(item.createTime)">颗粒物</span>
                        <div :class="item.nmhcData > 10 ? 'marker-red' : 'marker-green'"></div>
                        <span style="margin-right: 10px" :style="getColor(item.createTime)">NmHc </span>
                        <a-icon style="margin-top: 4px" type="clock-circle" />
                        <span :style="getColor(item.createTime)">
                          {{ item.createTime }}
                        </span>
                      </div>
                    </div>
                       <div class="detail-row">
                      <span style="font-weight: 700">[排放值](mg/m3)</span>
                      <div :class="item.lampblackData > 1 ? 'marker-red' : 'marker-green'"></div>
                      <span>油烟:{{ item.lampblackData }}</span>
                      <div :class="item.matterData > 5 ? 'marker-red' : 'marker-green'"></div>
                      <span>颗粒物:{{ item.matterData }}</span>
                      <div :class="item.nmhcData > 10 ? 'marker-red' : 'marker-green'"></div>
                      <span>NmHc:{{ item.nmhcData }}</span>
                    </div>
                    <div class="detail-row">
                      <div style="font-weight: 700">
                        [净化器]
                        <span v-if="item.purifierStatus == 1" style="color: #3c763d">(运行中)</span>
                        <span v-else style="color: #dd4b39">(未运行)</span>
                      </div>
                      <div class="marker"></div>
                      <span>电压:{{ item.secondVoltage }} V</span>
                      <div class="marker"></div>
                      <span>电流:{{ item.secondCurrent }} A</span>
                      <div class="marker"></div>
                      <span>压差:{{ item.purifierVoltageDiff }} Pa</span>
                    </div>
                    <div class="detail-row">
                      <div style="font-weight: 700">
                        [排风机]
                        <span v-if="item.fanStatus == 1" style="color: #3c763d">(运行中)</span>
                        <span v-else style="color: #dd4b39">(未运行)</span>
                      </div>
                      <div class="marker"></div>
                      <span>电流{{ item.fanCurrent }} A</span>
                    </div>
                  </a-collapse-panel>
                </a-collapse>
              </div>
            </a>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </a-card>
  </div>
</template>

<script>
import { getAction, postAction } from '@/api/manage'
// import { mixinDevice } from '@/utils/mixin'
// import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'DataList',
  components: {},
  // mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      detailList: [],
      searchData: [],
      addressData: [],
      activeKey: '1',
      pagination: {
        onChange: page => {
          this.pagination.current = page
          this.activeKey = ['']
          this.getListData()
        },
        onShowSizeChange: (current, size) => {
          this.pagination.current = 1
          this.pagination.pageSize = size
          this.activeKey = ['']
          this.getListData()
        },
        pageSize: 10,
        pageSizeOptions: ['10', '50', '100'],
        total: 0,
        current: 1,
        pageSize: 10,
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共 ' + total + ' 条'
        },
        showQuickJumper: true,
        showSizeChanger: true
      },
      url: {
        detailList: '/base/getAllId',
        list: '/company/list',
        pointList: '/base/getPointByCompanyId'
      },
      companyId: undefined,
      pointId: undefined,
      loading: false,
      pieValue: [],
      endTime: '',
      startTime: '',
      dateStr: '',
      columns: [
        // {
        //   title: '电场',
        //   dataIndex: 'name',
        //   key: 'name',
        //   scopedSlots: { customRender: 'name' },
        // },
        // {
        //   title: '一次电压',
        //   dataIndex: 'age',
        //   key: 'age',
        // },
        {
          title: '二次电压',
          dataIndex: 'secondVoltage',
          key: 'address 1'
        },
        {
          title: '二次电流',
          dataIndex: 'secondCurrent',
          key: 'address 2'
        },
        {
          title: '二次功率',
          dataIndex: 'outputPower',
          key: 'address 3',
          customRender: function(t, r, index) {
            return <div class="marker"></div>
          }
        },
        {
          title: '保护',
          dataIndex: 'powerProtectStatus',
          key: 'address 4'
        },
        {
          title: '开路',
          dataIndex: 'powerOpenStatus',
          key: 'address 4'
        },
        {
          title: '短路',
          dataIndex: 'powerCircuitProtect',
          key: 'address 4'
        },
        {
          title: '超温',
          dataIndex: 'powerOvertempProtect',
          key: 'address 4'
        }
      ]
    }
  },
  computed: {},
  methods: {
     getColor(time) {
      if (time.substring(0, 10) == this.dateStr) {
        return 'color:#000'
      } else {
        return 'color:#808080'
      }
    },
    handleChangeCompany(value) {
      this.companyId = value
      this.getPointList()
    },
    handleChangePoint(value) {
      this.pointId = value
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    //获取数据
    getListData() {
      this.loading = true
      let params = {
        companyid: this.companyId ? this.companyId : '',
        pageSize: this.pagination.pageSize,
        pageNo: this.pagination.current,
        endTime: this.endTime,
        startTime: this.startTime,
        pointid: this.pointId
      }
      getAction(this.url.detailList, params)
        .then(res => {
          this.$set(this.pagination, 'total', res.result.total)
          this.$nextTick(() => {
            this.loading = false
            this.detailList = res.result.records
          })
        })
        .catch(err => {
          this.loading = false
          err
        })
    },
    //获取所有餐饮企业
    loadData() {
      getAction(this.url.list).then(res => {
        res.result.forEach(item => {
          this.searchData.push({ id: item.id, name: item.name })
        })
      })
    },
    //重置
    clear() {
      this.companyId = undefined
      this.pointId = undefined
      this.startTime = ''
      this.endTime = ''
      this.getListData()
    },
    //获取测点
    getPointList() {
      this.addressData = []
      this.pointId = undefined
      getAction(this.url.pointList, { companyid: this.companyId }).then(res => {
        res.result.records.forEach(item => {
          this.addressData.push({ id: item.id, name: item.pointMac })
        })
      })
    },
    onDateChange(value, dateString) {
      console.log('---------dateString')
      console.log(dateString)
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    searchDatas() {
      this.getListData()
    },
     getNowDate() {
      var nowDate = new Date()
      var year = nowDate.getFullYear()
      var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
      var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
      this.dateStr = year + '-' + month + '-' + day
    }
  },
  created() {
    this.loadData()
    this.getNowDate()
  }
}
</script>
<style lang="less" scoped>
.header-name {
  margin-left: 10px;
}
.header-row {
  display: flex;
  position: relative;
  .header-circle {
    display: flex;
    position: absolute;
    right: 1px;
  }
}
.marker {
  width: 12px;
  height: 12px;
  border: 1px solid #088;
  border-radius: 24px;
  background-color: #3c763d;
  margin: 5px 3px 0px 20px;
}
.marker-green {
  width: 12px;
  height: 12px;
  border: 1px solid #088;
  border-radius: 24px;
  background-color: #3c763d;
  margin: 5px 3px 0px 20px;
}
.marker-red {
  width: 12px;
  height: 12px;
  border: 1px solid red;
  border-radius: 24px;
  background-color: red;
  margin: 5px 3px 0px 20px;
}
.detail-row {
  display: flex;
  margin-bottom: 15px;
}
</style>
