<template>
<!-- 已解除风控 -->
  <div>
    <div class="develop-chart-style" hoverable>
      <a-card>
        <a-row class="firm-table" :gutter="24">
          <a-col :sm="24" :md="24" :xl="24">
            <div>
              <a-table
                ref="table"
                size="middle"
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="false"
                :loading="loading"
                class="j-table-force-nowrap table-content"
                @change="handleTableChange"
                :scroll="{ y: 380 }"
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
          </a-col>
        </a-row>
      </a-card>
    </div>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'DischargedTable',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
        },
        {
          title: '企业名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '风控级别',
          align: 'center',
          dataIndex: 'num',
        },
        // {
        //   title: '风控评分',
        //   align: 'center',
        //   dataIndex: 'code4',
        // },
        {
          title: '风控日期',
          align: 'center',
          dataIndex: 'time',
        },
        // {
        //   title: '解除评分',
        //   align: 'center',
        //   dataIndex: 'code6',
        // },
         {
          title: '解除日期',
          align: 'center',
          dataIndex: 'clearTime',
        }
      ],
      url: {
        list: '/supervision/getClearRiskList',
      },
      dataSource: [],
    }
  },
  created() {},
  computed: {},
  methods: {},
  created() {
    this.$nextTick(() => {
      setTimeout(() => {}, 20)
    })
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
/deep/ .ant-card {
  border-radius: 16px;
}
/deep/.ant-table-thead tr th {
  background-color: #62a8ed;
  color: #e0edfa;
}
</style>
