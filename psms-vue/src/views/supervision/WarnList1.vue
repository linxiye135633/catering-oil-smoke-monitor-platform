<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="企业名称/测点MAC" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
               <a-range-picker placeholder="请选择汇总日期范围" format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange"> </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.realname" placeholder="请选择反馈状态">
                <a-select-option value="1"> 待反馈 </a-select-option>
                <a-select-option value="2"> 已反馈 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.comStatus" placeholder="请选择企业告警类型">
                <a-select-option value="1"> 营业时段停机 </a-select-option>
                <a-select-option value="2"> 没有联动开启 </a-select-option>
                <a-select-option value="3"> 二次电压过低 </a-select-option>
                <a-select-option value="4"> 设备联网异常 </a-select-option>
                <a-select-option value="5"> 烟气排放超标 </a-select-option>
                <a-select-option value="6"> 高压电场异常 </a-select-option>
                <a-select-option value="7"> 设备压差异常 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="12" :lg="12" :md="12" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" icon="download" @click="handleExportXls('政策文件')" style="margin-left: 8px"
                >导出</a-button
              >
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <span slot="tags" slot-scope="tags">
          <a-tag color="geekblue">
            {{ tags }}
          </a-tag>
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="edit(record)" style="color:#00a65a"><a-icon type="plus"/>申诉</a>
        </span>
      </a-table>
    </div>
    <!-- <company-view ref="modalForm"></company-view> -->
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'WarnList1',
  components: {
    
  },
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '告警反馈列表1',
        pieValue: [],
      endTime: '',
      startTime: '',
      dataSource:[],
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '企业名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '测点编码',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '告警类型',
          align: 'center',
          dataIndex: 'contact',
        },
        {
          title: '告警次数',
          align: 'center',
          dataIndex: 'areaCode',
        },
        {
          title: '汇总周期',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '生成时间',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '反馈时间',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        },
      ],
      url: {
        list: '/company/list',
        delete: '/company/delete',
        deleteBatch: '/policyFiles/policyFiles/deleteBatch',
        exportXlsUrl: '/policyFiles/policyFiles/exportXls',
        importExcelUrl: 'policyFiles/policyFiles/importExcel',
      },
    }
  },
  created() {
 
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
       onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    view(data) {
      this.$refs.modalForm.open()
    },
     loadData() {
        let that = this
        this.dataSource = []
      },
  },
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
