<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-modal :visible="visible" @ok="handleOk" @cancel="handleCancel" width="80%">
          <a-row :gutter="24">
            <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-collapse v-model="activeKey">
                <a-collapse-panel key="1" header="企业告警信息">
                  <p>{{ toolTip }}</p>
                </a-collapse-panel>
              </a-collapse>
            </a-col>
            <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-card title="餐饮企业信息" style="width: 100%">
                <a-table
                  ref="table"
                  size="middle"
                  bordered
                  rowKey="id"
                  :columns="columns"
                  :dataSource="dataSource"
                  :pagination="false"
                  class="j-table-force-nowrap"
                  @change="handleTableChange"
                >
                  <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                  </template>
                </a-table>
                <a-card title="[接入状态:已接入] 040576888" style="width: 100%; margin-top: 20px">
                  <a-row :gutter="24">
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">测点名称:</span>
                      <span class="col-value">一号排放口</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">净化器技术路线:</span>
                      <span class="col-value">其他</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">排风机设计风量:</span>
                      <span class="col-value">5立方米/秒</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">SIM卡号:</span>
                      <span class="col-value">190999999</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">创建日期:</span>
                      <span class="col-value">2020-01-11</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">净化器电场模块:</span>
                      <span class="col-value">1个</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">标准灶头数:</span>
                      <span class="col-value">5个</span>
                    </a-col>
                    <a-col class="col-card" :sm="12" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
                      <span class="col-name">SIM卡截至日期:</span>
                      <span class="col-value">2021-09-02</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col class="col-card" :sm="12" :md="12" :xl="6">
                      <span class="col-name">接入日期:</span>
                      <span class="col-value">2020-01-11</span>
                    </a-col>
                  </a-row>
                </a-card>
              </a-card>
            </a-col>
            <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-card title="企业告警明细" style="width: 100%">
                <a slot="extra" href="#"> <a-icon type="vertical-align-bottom" />导出</a>
                <a-table
                  ref="table"
                  size="middle"
                  bordered
                  rowKey="id"
                  :columns="columns1"
                  :dataSource="dataSource1"
                  :pagination="false"
                  class="j-table-force-nowrap"
                  @change="handleTableChange"
                >
                  <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                  </template>
                </a-table>
              </a-card>
            </a-col>
          </a-row>
        </a-modal>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage'
import { setTimeout } from 'timers'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'WarnDetail',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  props: {},
  data() {
    return {
      visible: false,
      activeKey: ['1'],
      url: {
      list: '/company/list',
      },
      toolTip: '说明：餐饮企业（单位）基本信息、当前企业标签、告警明细列表、告警反馈（申诉）信息',
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
      form: {
        name: '', //机构名称
        shortNameCn: '', //中文简称
        shortNameEn: '', //英文简称
        address: '', //机构地址
        adminCode: '', //默认管理员登陆账号
        adminPwd: '', //登陆密码
        contact: '', //联系人
        contactTel: '', //联系人手机号
        lng: '',
        lat: '',
      },
      columns: [
        {
          title: '企业编码',
          align: 'center',
          dataIndex: 'name',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '企业名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '联系人手机号',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '所在区域',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '企业地址',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'name',
        },
      ],
      columns1: [
        {
          title: '告警详情',
          dataIndex: 'name',
          customRender: function (t, r, index) {
            return `上报时间:${r.code1}||颗粒物浓度${r.code2}`
          },
        },
        {
          title: '告警类型',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '告警时间',
          align: 'center',
          dataIndex: 'name',
        },
      ],
      dataSource: [
        {
          name: '新天地网吧',
        },
      ],
      dataSource1: [
        {
          name: '新天地网吧',
          code1: '2021-11-23 13:48:20',
          code2: '0.14 mg/m3',
          code3: '5 mg/m3',
          code4: '1.74 mg/m3',
        },
      ],
      //   url: {
      //     list: '/company/list',
      //   },
    }
  },
  computed: {},
  methods: {
    handleOk(e) {
      this.visible = false
    },
    open() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
  },
  created() {},
}
</script>
<style lang="less" scoped>
// /deep/ .ant-card {
//   border-radius: 10px;
// }
.col-card {
  padding: 20px;
  .col-name {
    color: #333;
    font-size: 14px;
    font-weight: 700;
    margin-right: 10px;
  }
  .col-value {
    color: #7f7f7f;
    font-weight: 400;
    font-size: 14px;
  }
}
</style>
