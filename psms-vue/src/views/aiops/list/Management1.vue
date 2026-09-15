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
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-range-picker
                placeholder="请选择工单日期范围"
                format="YYYY-MM-DD HH:mm:ss"
                allowClear
                v-model="pieValue"
                @change="onDateChange"
              >
              </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.realname" placeholder="请选择维护类型">
                <a-select-option value="1"> 净化器清洗 </a-select-option>
                <a-select-option value="2"> 净化器维修 </a-select-option>
                <a-select-option value="3"> 日常运维 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.comStatus" placeholder="请选择处理状态">
                <a-select-option value="1"> 待处理 </a-select-option>
                <a-select-option value="2"> 已派单 </a-select-option>
                <a-select-option value="3"> 已完成 </a-select-option>
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
  name: 'WarnList1',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '运维管理列表1',
      pieValue: [],
      endTime: '',
      startTime: '',
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '工单标题',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '运维工程师',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '餐饮企业联系人',
          align: 'center',
          dataIndex: 'contact',
        },
        {
          title: '餐饮企业信息',
          align: 'center',
          dataIndex: 'areaCode',
        },
        {
          title: '派单日期',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '即时派单',
          align: 'center',
          dataIndex: 'createTime',
        },
      ],
      //   url: {
      //     list: '/company/list',
      //   },
    }
  },
  created() {},
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    }
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
