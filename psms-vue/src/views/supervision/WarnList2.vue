<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-input placeholder="企业名称/测点MAC" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-range-picker
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
              <a-select v-model="queryParam.realname" placeholder="请选择反馈类型">
                <a-select-option value="1"> 申诉 </a-select-option>
                <a-select-option value="2"> 报备 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item>
              <a-select v-model="queryParam.comStatus" placeholder="请选择处理状态">
                <a-select-option value="1"> 待处理 </a-select-option>
                <a-select-option value="2"> 已通过 </a-select-option>
                <a-select-option value="3"> 已拒绝 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="12" :lg="12" :md="12" :sm="24">
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
        <!-- <span slot="action" slot-scope="text, record">
          <a @click="edit(record)" style="color: #00a65a"><a-icon type="plus" />申诉</a>
        </span> -->
      </a-table>
    </div>
    <!-- <company-view ref="modalForm"></company-view> -->
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'WarnList2',
  components: {},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      description: '告警反馈列表2',
      pieValue: [],
      endTime: '',
      startTime: '',
      dataSource:[],
      // 表头
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '测点编码',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '反馈标题',
          align: 'center',
          dataIndex: 'contact',
        },
        {
          title: '企业名称',
          align: 'center',
          dataIndex: 'areaCode',
        },
        {
          title: '企业联系人',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '机构名称',
          align: 'center',
          dataIndex: 'contact',
        },
        {
          title: '处理状态',
          align: 'center',
          dataIndex: 'contact',
        },
        // {
        //   title: '审核处理',
        //   dataIndex: 'action',
        //   align: 'center',
        //   fixed: 'right',
        //   width: 147,
        //   scopedSlots: { customRender: 'action' },
        // },
      ],
      url: {
        list: '/supervision/getAlarmLists3',
      },
    }
  },
  created() {},
  computed: {},
  methods: {
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    view(data) {
      this.$refs.modalForm.open()
    },
     loadData() {
        let that = this
        this.dataSource = []
      },
  },
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
