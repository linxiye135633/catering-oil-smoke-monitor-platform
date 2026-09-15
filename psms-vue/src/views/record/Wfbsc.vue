<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="单位/标题/联系人/手机/测点MAC" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.comStatus" placeholder="请选择状态">
                <a-select-option value="1"> 全部状态 </a-select-option>
                <a-select-option value="2"> 正常 </a-select-option>
                <a-select-option value="3"> 待审核 </a-select-option>
                <a-select-option value="4"> 已拒绝 </a-select-option>
                <a-select-option value="5"> 已锁定 </a-select-option>
                <a-select-option value="6"> 已注销 </a-select-option>
              </a-select>
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
  name: 'WfbscList',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '服务厂商',
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '状态',
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
          title: '名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'contact',
        },
        // {
        //   title: '手机号',
        //   align: 'center',
        //   dataIndex: 'areaCode',
        // },
        // {
        //   title: '地址',
        //   align: 'center',
        //   dataIndex: 'createTime',
        // },
        {
          title: '创建日期',
          align: 'center',
          dataIndex: 'createTime',
        },
      ],
      url: {
          list: '/Manufacturer/list',
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
