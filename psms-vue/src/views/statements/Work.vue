<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="请输入报表任务名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
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
          <a style="color: #3cacdf" @click="downPdf(text)">{{ text }}</a>
        </span>
      </a-table>
    </div>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'WorkList',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '报表任务',
      visible: false,
      confirmLoading: false,
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '报表任务名称',
          align: 'center',
          dataIndex: 'name'
        },
        {
          title: '报表任务参数',
          align: 'center',
          dataIndex: 'msg'
        },
        {
          title: '报表任务状态',
          align: 'center',
          dataIndex: 'state'
        },
        {
          title: '提交任务时间',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          // title: '输出报表文件（点击文件名下载，72小时后文件失效）',
          title: '输出报表文件',
          align: 'center',
          dataIndex: 'fileName',
          scopedSlots: { customRender: 'action' }
        }
      ],
      endTime: '',
      startTime: '',
      url: {
        list: '/basestatistical/baseStatistical/list'
      }
    }
  },
  created() {},
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    downPdf(data) {
      window.open(`http://47.93.98.133:8080/psms/pdf/${data}`)
    }
  }
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
