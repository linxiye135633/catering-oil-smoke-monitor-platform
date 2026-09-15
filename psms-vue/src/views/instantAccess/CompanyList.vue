<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="请输入餐饮企业编码/企业名称/门头名称" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-cascader :options="areaData" @change="onChange" placeholder="请选择单位所在区域" />
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="请输入街道名称" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.realname" placeholder="请选择状态">
                <a-select-option value="1"> 已接入 </a-select-option>
                <a-select-option value="2"> 已注销 </a-select-option>
                <a-select-option value="3"> 待安装 </a-select-option>
                <a-select-option value="4"> 待接入 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="12" :lg="12" :md="12" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button @click="goCompanyAdd" type="primary" icon="plus" style="margin-left: 8px"
                >注册新餐饮企业</a-button
              >
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
          <!-- <a @click="view(record)">查看</a>
          <a-divider type="vertical" /> -->
          <a @click="edit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>
      </a-table>
    </div>
    <company-view ref="modalForm"></company-view>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import areaData from './areaData' //省市区三级联动数据
import CompanyView from './CompanyView'
export default {
  name: 'CompanyList',
  components: {
    CompanyView,
  },
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '餐饮企业列表',
      // 表头
      columns: [
        {
          title: '企业编码',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '接入状态',
          align: 'center',
          dataIndex: 'status',
          customRender: function (t, r, index) {
            return '已接入'
            // if (r.status == '01') {
            //   return '已接入'
            // } else {
            //   return '未接入'
            // }
          },
        },
        {
          title: '企业名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'contact',
          customRender: function (t, r, index) {
            return `${r.contact}/${r.contactMobile}`
          },
        },
        {
          title: '所在区域',
          align: 'center',
          dataIndex: 'areaCode',
        },
        {
          title: '创建日期',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          // fixed: 'right',
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
      dictOptions: {},
      superFieldList: [],
      //省市区数据
      areaData: [],
      selectdAreaData: [],
    }
  },
  created() {
    this.areaData = areaData
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
    onChange(value) {
      this.selectdAreaData = value
      this.queryParam.areaCode = value.toString()
    },
    goCompanyAdd() {
      this.$router.push({
        path: '/InstantAccess/CompanyAdd',
        query: {
          pageType: 'enroll',
        },
      })
    },
    edit(data) {
      this.$router.push({
        path: '/InstantAccess/CompanyAdd',
        query: {
          listData: data,
          pageType: 'edit',
        },
      })
    },
    view(data) {
      this.$refs.modalForm.open()
    },
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
/deep/.ant-table-thead tr th {
  background-color: #62a8ed;
  color: #e0edfa;
}
</style>
