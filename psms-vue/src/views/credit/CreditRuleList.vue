<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="指标名称">
              <a-input placeholder="请输入指标名称" v-model="queryParam.ruleName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="分值类型">
              <a-select v-model="queryParam.scoreType" placeholder="请选择" allowClear>
                <a-select-option value="1">加分</a-select-option>
                <a-select-option value="2">减分</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="状态">
              <a-select v-model="queryParam.enabled" placeholder="请选择" allowClear>
                <a-select-option value="1">启用</a-select-option>
                <a-select-option value="0">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px" v-has="'credit:rule:add'">新增</a-button>
              <a-button type="primary" icon="download" @click="handleExportXls('信用评价指标')" style="margin-left: 8px">导出</a-button>
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
        <span slot="scoreTypeSlot" slot-scope="text">
          <a-tag :color="text === '1' ? 'green' : 'red'">{{ text === '1' ? '加分' : '减分' }}</a-tag>
        </span>
        <span slot="enabledSlot" slot-scope="text">
          <a-tag :color="text === '1' ? 'blue' : 'default'">{{ text === '1' ? '启用' : '停用' }}</a-tag>
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)" v-has="'credit:rule:edit'">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleToggle(record)" v-has="'credit:rule:edit'">{{ record.enabled === '1' ? '停用' : '启用' }}</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a v-has="'credit:rule:delete'">删除</a>
          </a-popconfirm>
        </span>
      </a-table>
    </div>

    <credit-rule-modal ref="modalForm" @ok="modalFormOk"></credit-rule-modal>
  </a-card>
</template>

<script>
/**
 * CreditRuleList 信用规则配置页（#E-46/#E-47，沈福临）
 * 严格沿用1.0三段式范式（参照 instantAccess/PointList.vue）：
 * a-card → table-page-search-wrapper → a-table + JModal；混入 JeecgListMixin；
 * 按钮权限 v-has；启停为子资源动作接口。
 */
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import CreditRuleModal from './modules/CreditRuleModal'
import { postAction } from '@/api/manage'

export default {
  name: 'CreditRuleList',
  mixins: [JeecgListMixin],
  components: { CreditRuleModal },
  data () {
    return {
      description: '信用评价指标管理',
      url: {
        list: '/v2/credit/rules/list',
        delete: '/v2/credit/rules/delete',
        deleteBatch: '/v2/credit/rules/deleteBatch',
        exportXlsUrl: '/v2/credit/rules/exportXls',
        importExcelUrl: 'sys/common/upload'
      },
      columns: [
        { title: '指标代码', align: 'center', dataIndex: 'ruleCode' },
        { title: '指标名称', align: 'center', dataIndex: 'ruleName' },
        { title: '分值类型', align: 'center', dataIndex: 'scoreType', scopedSlots: { customRender: 'scoreTypeSlot' } },
        { title: '单次分值', align: 'center', dataIndex: 'scoreValue' },
        { title: '权重', align: 'center', dataIndex: 'weight' },
        { title: '次数上限', align: 'center', dataIndex: 'maxTimes', customRender: t => (t == null ? '不限' : t) },
        { title: '状态', align: 'center', dataIndex: 'enabled', scopedSlots: { customRender: 'enabledSlot' } },
        { title: '规则说明', align: 'center', dataIndex: 'remark' },
        { title: '操作', dataIndex: 'action', align: 'center', width: 180, scopedSlots: { customRender: 'action' } }
      ]
    }
  },
  methods: {
    /** 启停：子资源动作 POST /v2/credit/rules/{id}/enable */
    handleToggle (record) {
      const target = record.enabled === '1' ? '0' : '1'
      postAction(`/v2/credit/rules/${record.id}/enable`, { enabled: target }).then(res => {
        if (res.success) {
          this.$message.success(res.message)
          this.loadData()
        } else {
          this.$message.warning(res.message)
        }
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
