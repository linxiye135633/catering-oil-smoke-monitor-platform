<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="请输入企业名称" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-cascader :options="areaData" @change="onChange" placeholder="请选择区域" />
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="请输入街道" v-model="queryParam.streetCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.status" placeholder="请选择状态">
                <a-select-option value="01"> 已接入 </a-select-option>
                <a-select-option value="02"> 已注销 </a-select-option>
                <a-select-option value="03"> 待接入 </a-select-option>
                <a-select-option value="04"> 已过期 </a-select-option>
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
        <!-- <span slot="tags" slot-scope="tags">
          <a-tag color="geekblue">
            {{ tags }}
          </a-tag>
        </span> -->
         <span slot="tags" slot-scope="text,record,index">
          <a-tag color="geekblue">
           {{index+1}}
          </a-tag>
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>
      </a-table>
    </div>
    <edit-point ref="modalForm" @ok="modalFormOk"></edit-point>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import areaData from './areaData' //省市区三级联动数据
import EditPoint from './EditPoint'

export default {
  name: 'PointList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    EditPoint,
  },
  data() {
    return {
      description: '餐饮企业列表',
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '测点MAC',
          align: 'center',
          dataIndex: 'pointMac',
        },
        {
          title: '测点名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '电场数量',
          align: 'center',
          dataIndex: 'modelNumber',
        },
        {
          title: '餐饮企业',
          align: 'center',
          dataIndex: 'enterpriseName',
        },
        {
          title: '测点状态',
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
          title: '接入日期',
          align: 'center',
          dataIndex: 'connDate',
        },
        {
          title: '创建日期',
          align: 'center',
          dataIndex: 'createTime',
            width: 147,
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
        list: '/point/list',
        delete: '/point/delete',
        deleteBatch: '/point/deleteBatch',
        exportXlsUrl: '/point/exportXls',
        importExcelUrl: '/point/importExcel',
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
    handleEdit: function (record) {
      this.$refs.modalForm.edit(record)
    },
    onChange(value) {
      this.selectdAreaData = value
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