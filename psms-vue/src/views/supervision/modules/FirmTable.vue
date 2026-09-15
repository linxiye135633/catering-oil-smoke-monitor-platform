<template>
  <!-- 新增风控企业 -->
  <div>
    <div class="develop-chart-style" hoverable>
      <a-card  style="height:380px">
   
            <div >
              <a-table
                ref="table"
                size="middle"
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="false"
                :loading="loading"
                class="j-table-force-nowrap"
               :scroll="{y:290}"
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
                  <a-popconfirm title="确定解除吗?" @confirm="() => deleteList(record.name)">
                    <a style="color: red">X解除风控</a>
                  </a-popconfirm>
                </span>
              </a-table>
            </div>
        
      
      </a-card>
    </div>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { httpAction, getAction } from '@/api/manage'
export default {
  name: 'FirmTable',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      columns: [
        {
          title: '编号',
          align: 'center',

          dataIndex: 'id'
        },
        {
          title: '企业名称',
          align: 'center',

          dataIndex: 'name'
        },
        {
          title: '风控级别',
          align: 'center',
          dataIndex: 'num'
        },
        // {
        //   title: '风控评分',
        //   align: 'center',
        //   dataIndex: 'code4',
        // },
        {
          title: '风控日期',
          align: 'center',

          dataIndex: 'time'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/supervision/getNewRisk',
        delete: '/supervision/getClearRisk'
      }
    }
  },
  created() {},
  computed: {},
  methods: {
    deleteList(name) {
      getAction(this.url.delete, { name })
        .then(res => {
          this.loadData()
        })
        .catch(err => {
          err
        })
    }
  },
  created() {}
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
