<template>
  <div>
    <a-card :bordered="false">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-input placeholder="姓名/机构/手机号/部门/职位" v-model="queryParam.str"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-select v-model="queryParam.status" placeholder="请选择状态">
                  <a-select-option value="01"> 正常 </a-select-option>
                  <a-select-option value="02"> 锁定 </a-select-option>
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
            <a @click="edit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </span>
        </a-table>
      </div>
    </a-card>
    <per-Modal ref="perForm"></per-Modal>
  </div>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import perModal from './AddPer.vue'
export default {
  name: 'AgencyList',
  components: {
    perModal,
  },
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '机构人员表',
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
    edit(data) {
      this.perData = data
      this.$refs.perForm.isEdit = true
      this.$refs.perForm.form = Object.assign({}, this.perData)
      this.$refs.perForm.open()
    },
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
/deep/.ant-table-thead tr th {
  background-color: #62a8ed;
  color: #e0edfa
}
// /deep/ .ant-input {
//   border-color:#62A8ED
// }
</style>
