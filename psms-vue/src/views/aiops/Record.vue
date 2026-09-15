<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="客户单位" allowClear v-model="queryParam.unit"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="12" :lg="12" :md="12" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
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
      </a-table>
    </div>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'RecordList',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '运维记录',
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '运维报告',
          align: 'center',
          dataIndex: 'report'
        },
        {
          title: '工单信息',
          align: 'center',
          dataIndex: 'information'
        },
        {
          title: '运维工程师',
          align: 'center',
          dataIndex: 'operations'
        },
        {
          title: '客户联系人',
          align: 'center',
          dataIndex: 'customer'
        },
        {
          title: '客户单位',
          align: 'center',
          dataIndex: 'unit'
        },
        {
          title: '派单日期',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '运维结果',
          align: 'center',
          dataIndex: 'results',
          customRender: function(t, r, index) {
            if (r.results == '1') {
              return '待处理'
            } else if (r.results == '2') {
              return '已派发'
            } else if (r.results == '3') {
              return '已完成'
            } else if (r.results == '4') {
              return '拒绝'
            }
          }
        },
        {
          title: '工单受理',
          align: 'center',
          dataIndex: 'acceptance'
        }
      ],
        url: {
          list: '/operations/baseOperations/list',
        },
    }
  },
  created() {},
  computed: {},
  methods: {
   
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
