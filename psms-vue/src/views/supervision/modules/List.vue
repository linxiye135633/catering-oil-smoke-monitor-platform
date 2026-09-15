<template>
  <div>
    <a-card :bordered="false">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-input placeholder="餐饮企业名称" v-model="queryParam.str"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-range-picker format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange">
              </a-range-picker>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.status" placeholder="请选择风控级别">
                  <a-select-option value="01"> 高风险 </a-select-option>
                  <a-select-option value="02"> 中风险 </a-select-option>
                  <a-select-option value="03"> 低风险 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.statusx" placeholder="请选择风控状态">
                  <a-select-option value="01">已加入风控企业 </a-select-option>
                  <a-select-option value="02"> 已解除风控企业 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                </span>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <!-- table区域-begin -->
      <div>
        <a-table
          ref="table"
          size="middle"
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
            <a-popconfirm title="确定解除吗?" @confirm="() => handleDelete(record.id)">
              <a style="color: red">X接触风控</a>
            </a-popconfirm>
          </span>
        </a-table>
      </div>
    </a-card>
  </div>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'CorporateList',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '企业治理',
      pieValue: [],
      endTime: '',
      startTime: '',
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'name' },
        },
        {
          title: '姓名',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '部门/职位',
          align: 'center',
          dataIndex: 'department',
          customRender: function (t, r, index) {
            return `${r.department}/${r.position}`
          },
        },
        {
          title: '行政机构',
          align: 'center',
          dataIndex: 'institutionCode',
        },
        {
          title: '所属组织',
          align: 'center',
          dataIndex: 'orgCode',
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status',
          customRender: function (t, r, index) {
            if (r.status == '01') {
              return '正常'
            } else {
              return '锁定'
            }
          },
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
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        },
      ],
      url: {
        list: '/InstitutionPerson/list',
        delete: '/InstitutionPerson/delete',
      },
      perData: {},
    }
  },
  created() {},
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
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
// /deep/ .ant-input {
//   border-color:#62A8ED
// }
</style>
