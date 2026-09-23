<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="公示周期">
              <a-month-picker
                v-model="cycleMoment"
                format="YYYY-MM"
                placeholder="请选择月份"
                style="width: 100%"
                :allowClear="false"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 表格区域 -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="companyName"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <template slot="levelSlot" slot-scope="text">
          <a-tag :color="{ 1: 'red', 2: 'orange', 3: 'green' }[text] || 'green'">
            {{ { 1: '红牌', 2: '黄牌', 3: '绿牌' }[text] || '绿牌' }}
          </a-tag>
        </template>
        <template slot="scoreSlot" slot-scope="text">
          <span :style="{ color: text != null && text < 60 ? '#f5222d' : text != null && text < 80 ? '#fa8c16' : '#52c41a', fontWeight: 600 }">
            {{ text }}
          </span>
        </template>
      </a-table>
    </div>
  </a-card>
</template>

<script>
/**
 * CreditPublicList 信用公示页（#E-12，李晓倩；契约P-02：登录即可查）
 * 沿用1.0三段式范式；数据源 GET /v2/credit/public（仅已发布结果）。
 * 周期参数通过覆盖 getQueryParams 注入，加载/分页/提示全走 JeecgListMixin；
 * 该周期未发布时后端返回业务码25003，由混入统一弹出提示。
 */
import moment from 'moment'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'CreditPublicList',
  mixins: [JeecgListMixin],
  data () {
    return {
      description: '企业信用公示',
      cycleMoment: moment(),
      url: {
        list: '/v2/credit/public'
      },
      columns: [
        { title: '企业名称', align: 'center', dataIndex: 'companyName' },
        { title: '信用总分', align: 'center', dataIndex: 'totalScore', scopedSlots: { customRender: 'scoreSlot' } },
        { title: '信用等级', align: 'center', dataIndex: 'level', scopedSlots: { customRender: 'levelSlot' } },
        { title: '公示周期', align: 'center', dataIndex: 'cycle' },
        { title: '公示时间', align: 'center', dataIndex: 'publishTime' }
      ]
    }
  },
  watch: {
    cycleMoment () { this.loadData(1) }
  },
  methods: {
    /** 混入查询参数追加公示周期（YYYY-MM） */
    getQueryParams () {
      return Object.assign({}, this.queryParam, {
        cycle: this.cycleMoment ? this.cycleMoment.format('YYYY-MM') : '',
        pageNo: this.ipagination.current,
        pageSize: this.ipagination.pageSize
      })
    },
    searchReset () {
      this.cycleMoment = moment()
      this.queryParam = {}
      this.loadData(1)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
