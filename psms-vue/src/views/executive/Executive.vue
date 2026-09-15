<template>
  <div>
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="12" :style="{ padding: '30px' }">
        <a-card>
          <div slot="title">
            <a-icon type="apartment" style="margin-right: 10px" />行政机构
            <a-tooltip>
              <template slot="title">{{ toolTip.one }}</template>
              <a-icon class="card-question" style="color: #c2def8" type="question-circle" />
            </a-tooltip>
          </div>
          <div slot="extra" @click="showGovModal">
            <a-icon type="plus" href="#"></a-icon>
            新增机构
          </div>
          <a-table :row-selection="rowSelection" :columns="govColumns" :data-source="govData" bordered>
            <a slot="name" slot-scope="text">{{ text }}</a>
          </a-table>
        </a-card>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="12" :style="{ padding: '30px' }">
        <a-card>
          <div slot="title">
            <a-icon type="user" style="margin-right: 10px" />机构人员
            <a-tooltip>
              <template slot="title">{{ toolTip.two }}</template>
              <a-icon class="card-question" style="color: #c2def8" type="question-circle" />
            </a-tooltip>
          </div>
          <div slot="extra" v-if="govCode" @click="showPerModal">
            <a-icon type="plus" href="#"></a-icon>
            新增人员
          </div>
          <a-table :columns="personColumns" :data-source="personData" bordered>
            <a slot="name" slot-scope="text">{{ text }}</a>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
    <gov-Modal ref="govForm"></gov-Modal>
    <per-Modal ref="perForm" :code="govCode" :govName="govName"></per-Modal>
  </div>
</template>
<script>
import { getAction } from '@/api/manage'
import govModal from './AddGov.vue'
import perModal from './AddPer.vue'
export default {
  name: 'ExecutivePage',
  components: {
    govModal,
    perModal
  },
  data() {
    return {
      toolTip: {
        one: '指某个环保组织的下属分支机构，可以分为这些分支机构创建各自独立的登录账号。',
        two: '指某个下属分支机构的行政工作管理人员，可以为每个人创建一个登录账号，登录后仅可查看或管理属于本分支机构的餐饮企业与测点信息。',
        three:
          '可用于有多个分支机构的环保组织，例如：某市生态环境局一个独立的登录账号，可以查看本市的所有区的餐饮企业信息；同时可为每个区各自创建一个独立的登录账号，每个区仅允许查看本区的餐饮企业信息。',
      },
      govColumns: [
        {
          title: '机构编码',
          dataIndex: 'id',
          key: 'id',
          scopedSlots: { customRender: 'name' },
        },
        {
          title: '机构名称',
          dataIndex: 'name',
          key: 'name',
        },
        {
          title: '行政区划',
          dataIndex: 'area',
          key: 'area',
        },
      ],
      personColumns: [
        {
          title: '姓名',
          dataIndex: 'name',
          key: 'name',
          scopedSlots: { customRender: 'name' },
        },
        {
          title: '性别',
          dataIndex: 'sex',
          key: 'sex',
        },
        {
          title: '手机号',
          dataIndex: 'mobile',
          key: 'mobile',
        },
        {
          title: '部门',
          dataIndex: 'department',
          key: 'department',
        },
        {
          title: '平台账号',
          dataIndex: 'account',
          key: 'account',
        },
      ],
      govData: [],
      personData: [],

      url: {
        govList: '/Institution/list',
        personList: '/InstitutionPerson/list',
      },
      govCode: '',
      govName:''
    }
  },
  computed: {
    rowSelection() {
      return {
        type: 'radio',
        onChange: (selectedRowKeys, selectedRows) => {
          this.govCode = selectedRows[0].id
          this.govName = selectedRows[0].name
          this.getPerSonData(this.govCode)
        },
        getCheckboxProps: (record) => ({
          props: {
            disabled: record.name === 'Disabled User',
            name: record.name,
          },
        }),
      }
    },
  },
  methods: {
    getGovData() {
      getAction(this.url.govList, { isPage: true })
        .then((res) => {
          this.govData = res.result.records
        })
        .catch((err) => {
          err
        })
    },
    getPerSonData(code) {
      getAction(this.url.personList, { code: code })
        .then((res) => {
          this.personData = res.result.records
        })
        .catch((err) => {
          err
        })
    },
    showGovModal() {
      this.$refs.govForm.open()
    },
    showPerModal() {
      this.$refs.perForm.open()
    }
  },
  created() {
    this.getGovData()
  },
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 10px;
}
</style>
