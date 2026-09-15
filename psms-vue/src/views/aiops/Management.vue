<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="18" :style="{ marginBottom: '20px', paddingLeft: '20px' }">
        <div class="title-name" style="margin-bottom: 10px">
          <img src="../../assets/aiops1.png" alt="" />
          <span slot="extra">
            <a class="title-href" @click="openWorkAdd"><a-icon type="plus-circle" /> 增加 </a>
            <!-- <a class="title-href"  @click="goMore1"> 更多 > </a> -->
          </span>
        </div>
        <a-card style="height:320px">
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
                <a-popconfirm title="确定派送吗?" @confirm="() => handleSend(record, 2)">
                  <a>派送</a>
                </a-popconfirm>
              </span>
            </a-table>
          </div>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="24" :xl="6" :style="{ marginBottom: '20px', paddingLeft: '20px' }">
        <a-card style="margin-top: 40px">
          <div class="title-name">
            <img src="../../assets/aiops2.png" @click="openReady" alt="" />
            <div>
              <!-- <a style="float:right ;font-size: 18px;font-weight: 400;color: #666666" slot="extra" @click="openReady"> 更多 > </a> -->
              <a style="float:right ;font-size: 18px;font-weight: 400;color: #666666" slot="extra" @click="openReady">  > </a>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="18" :style="{ paddingLeft: '20px' }">
        <div class="title-name" style="margin-bottom: 10px">
          <img src="../../assets/aiops3.png" alt="" />
          <span slot="extra">
            <a class="title-href" style="margin-right: 10%" @click="openAddReport"
              ><a-icon type="plus-circle" /> 生成报告
            </a>
            <a style="float:right ;font-size: 18px;font-weight: 400;color: #666666" @click="goMore">  > </a>
          </span>
        </div>
        <a-card style="height:320px">
          <div>
            <a-table
              ref="table"
              size="middle"
              rowKey="id"
              :columns="columns1"
              :dataSource="ywReportData"
              :pagination="false"
              :loading="loading"
              class="j-table-force-nowrap table-content"
            >
              <template slot="htmlSlot" slot-scope="text">
                <div v-html="text"></div>
              </template>
            </a-table>
          </div>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="24" :xl="6" :style="{  paddingLeft: '20px' }">
        <a-card style="margin-top: 40px">
          <div class="title-name">
            <img src="../../assets/aiops4.png" alt="" />
            <span slot="extra">
              <a class="title-href" style="float:left" @click="openAdd"><a-icon type="plus-circle" /> 新增 </a>
              <!-- <a style="float:right ;font-size: 18px;font-weight: 400;color: #666666" @click="openPerson">
                更多 >
              </a> -->
               <a style="float:right ;font-size: 18px;font-weight: 400;color: #666666" @click="openPerson">
               >
              </a>
            </span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-modal
      title="新增运维工单"
      :visible="workVisible"
      :confirm-loading="confirmLoading"
      @ok="workHandleOk"
      @cancel="workHandleCancel"
    >
      <a-form-model ref="ruleForm" :model="workForm">
        <a-form-model-item label="工单信息" prop="information">
          <a-input v-model="workForm.information" placeholder="请输入运维工单信息" />
        </a-form-model-item>
        <a-form-model-item label="运维工程师">
          <a-select show-search placeholder="请选择运维工程师" @change="handleChange" allowClear style="width:100%">
            <a-select-option v-for="(item, index) in personList" :key="index + 'A'" :value="`${item.id}+${item.name}`">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="客户联系人" prop="customer">
          <a-input v-model="workForm.customer" placeholder="请输入客户联系人" />
        </a-form-model-item>
        <a-form-model-item label="客户单位" prop="unit">
          <a-input v-model="workForm.unit" placeholder="请输入客户单位" />
        </a-form-model-item>
        <a-form-model-item label="派单日期" prop="createTime">
          <a-date-picker
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择派单日期"
            style="width:100%"
            @change="onTimeChange"
          />
        </a-form-model-item>
        <a-form-model-item label="工单受理" prop="acceptance">
          <a-input v-model="workForm.acceptance" placeholder="请输入工单受理" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal
      title="新增运维人员"
      :visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-form-model ref="ruleForm" :model="form">
        <a-form-model-item label="工作单位(服务商)" prop="units">
          <a-input v-model="form.units" placeholder="请输入工作单位(服务商)" />
        </a-form-model-item>
        <a-form-model-item label="姓名" prop="name">
          <a-input v-model="form.name" placeholder="请输入运维工程师姓名" />
        </a-form-model-item>
        <a-form-model-item label="手机号" prop="phone">
          <a-input v-model="form.phone" placeholder="请输入运维工程师手机号" />
        </a-form-model-item>
        <a-form-model-item label="平台账号" prop="account">
          <a-input v-model="form.account" placeholder="请输入运维工程师登陆账号" />
        </a-form-model-item>
        <a-form-model-item label="预设密码" prop="password">
          <a-input v-model="form.password" placeholder="请输入运维工程师登陆密码" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal
      title="运维人员"
      :visible="personVis"
      :confirm-loading="confirmLoading"
      width="60%"
      @ok="personOk"
      @cancel="personOk"
      @change="handleTableChange"
    >
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="personColumns"
        :dataSource="personList"
        :pagination="false"
        :loading="loading"
     
        class="j-table-force-nowrap table-content"
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
    </a-modal>
    <a-modal
      title="已派发运维工单"
      :visible="readyVis"
      :confirm-loading="confirmLoading"
      width="60%"
      @ok="readyOk"
      @cancel="readyOk"
      @change="handleTableChange"
    >
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSourceReady"
        :pagination="false"
        :loading="loading"
      
        class="j-table-force-nowrap table-content"
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
          <a @click="handleSend(record, 3)">完成</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定拒绝吗?" @confirm="() => handleSend(record, 4)">
            <a>拒绝</a>
          </a-popconfirm>
        </span>
      </a-table>
    </a-modal>
    <a-modal
      title="已生成运维报告"
      :visible="reportVis"
      :confirm-loading="confirmLoading"
      width="60%"
      @ok="reportOk"
      @cancel="reportOk"
      @change="handleTableChange"
    >
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSourceReport"
        :pagination="false"
        :loading="loading"
      
        class="j-table-force-nowrap table-content"
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
          <a @click="reportSend(record)">生成</a>
        </span>
      </a-table>
    </a-modal>
  </div>
</template>

<script>
import { httpAction, getAction, putAction } from '@/api/manage'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { postAction } from '../../api/manage'
export default {
  name: 'AdhPage',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  data() {
    return {
      visible: false,
      personVis: false,
      workVisible: false,
      confirmLoading: false,
      readyVis: false,
      dataSource: [],
      dataSourceReady: [],
      dataSourceReport: [],
      ywReportData: [],
      personList: [],
      reportVis: false,
      columns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '运维报告',
          align: 'center',
          dataIndex: 'report'
        },
        {
          title: '工单信息',
          align: 'center',
          dataIndex: 'information'
        },
        {
          title: '运维工程师',
          align: 'center',
          dataIndex: 'operations'
        },
        {
          title: '客户联系人',
          align: 'center',
          dataIndex: 'customer'
        },
        {
          title: '客户单位',
          align: 'center',
          dataIndex: 'unit'
        },
        {
          title: '派单日期',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '运维结果',
          align: 'center',
          dataIndex: 'results',
          customRender: function(t, r, index) {
            if (r.results == '1') {
              return '待处理'
            } else if (r.results == '2') {
              return '已派发'
            } else if (r.results == '3') {
              return '已完成'
            } else if (r.results == '3') {
              return '拒绝'
            }
          }
        },
        {
          title: '工单受理',
          align: 'center',
          dataIndex: 'acceptance'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
        columns1: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '运维报告',
          align: 'center',
          dataIndex: 'report'
        },
        {
          title: '工单信息',
          align: 'center',
          dataIndex: 'information'
        },
        {
          title: '运维工程师',
          align: 'center',
          dataIndex: 'operations'
        },
        {
          title: '客户联系人',
          align: 'center',
          dataIndex: 'customer'
        },
        {
          title: '客户单位',
          align: 'center',
          dataIndex: 'unit'
        },
        {
          title: '派单日期',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '运维结果',
          align: 'center',
          dataIndex: 'results',
          customRender: function(t, r, index) {
            if (r.results == '1') {
              return '待处理'
            } else if (r.results == '2') {
              return '已派发'
            } else if (r.results == '3') {
              return '已完成'
            } else if (r.results == '3') {
              return '拒绝'
            }
          }
        },
        {
          title: '工单受理',
          align: 'center',
          dataIndex: 'acceptance'
        }
      ],
      personColumns: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '工作单位(服务商)',
          align: 'center',
          dataIndex: 'units'
        },
        {
          title: '姓名',
          align: 'center',
          dataIndex: 'name'
        },
        {
          title: '手机号',
          align: 'center',
          dataIndex: 'phone'
        },
        {
          title: '平台账号',
          align: 'center',
          dataIndex: 'account'
        },
        {
          title: '预设密码',
          align: 'center',
          dataIndex: 'password'
        }
      ],
      url: {
        list: '/operations/baseOperations/list',
        addList: '/operations/baseOperations/add', //增加代运维
        addPerson: '/baseoperationsperson/baseOperationsPerson/add', //增加运维人员
        personList: '/baseoperationsperson/baseOperationsPerson/list',
        sendList: '/operations/baseOperations/edit' //派送
      },
      form: {
        units: '',
        name: '',
        phone: '',
        account: '',
        password: ''
      },
      workForm: {
        report: 0,
        information: '',
        namoperationse: '',
        customer: '',
        unit: '',
        createTime: '',
        results: '',
        acceptance: '',
        userid: ''
      }
    }
  },
  created() {
    this.loadYw()
  },
  computed: {},
  methods: {
    goMore() {
      this.$router.push({
        name: 'aiops-OpsReport'
      })
    },
    goGenerate() {
      this.$router.push({
        name: 'aiops-GenerateReports'
      })
    },
    openAdd() {
      this.visible = true
    },
    openPerson() {
      this.personVis = true
      this.getPersonList()
    },
    openWorkAdd() {
      this.workVisible = true
      this.getPersonList()
    },
    openReady() {
      this.readyVis = true
      this.loadDataReady()
    },
    openAddReport() {
      this.reportVis = true
      this.loadDataReport()
    },
    reportOk() {
      this.reportVis = false
    },
    workHandleOk() {
      this.confirmLoading = true
      setTimeout(() => {
        postAction(this.url.addList, Object.assign(this.workForm, { results: 1 }))
          .then(res => {
            this.$message.success('添加成功')
          })
          .catch(err => {
            err
          })
        this.workForm = {}
        this.workVisible = false
        this.confirmLoading = false
        this.$nextTick(() => {
          this.loadData()
        })
      }, 1000)
    },
    workHandleCancel() {
      this.workVisible = false
    },
    handleOk(e) {
      this.confirmLoading = true
      setTimeout(() => {
        postAction(this.url.addPerson, this.form)
          .then(res => {
            this.$message.success('添加成功')
          })
          .catch(err => {
            err
          })
        this.form = {}
        this.personList = []
        this.visible = false
        this.confirmLoading = false
      }, 1000)
    },
    handleCancel(e) {
      this.visible = false
    },
    personOk() {
      this.personVis = false
    },
    readyOk() {
      this.readyVis = false
    },
    //获取运维人员
    getPersonList() {
      getAction(this.url.personList, { pageSize: 10000 })
        .then(res => {
          this.personList = res.result.records
        })
        .catch(err => {
          err
        })
    },
    //代运维列表
    loadData() {
      var params = Object.assign(this.getQueryParams(), { results: 1, pageSize: 10000 })
      getAction(this.url.list, params)
        .then(res => {
          this.dataSource = res.result.records
        })
        .catch(err => {
          err
        })
    },
    //已派发
    loadDataReady() {
      var params = Object.assign(this.getQueryParams(), { results: 2, pageSize: 10000 })
      getAction(this.url.list, params)
        .then(res => {
          this.dataSourceReady = res.result.records
        })
        .catch(err => {
          err
        })
    },
    handleChange(data) {
      this.workForm.userid = data.split('+')[0]
      this.workForm.operations = data.split('+')[1]
    },
    onTimeChange(value, dataString) {
      this.workForm.createTime = dataString
    },
    //派送 完成 拒绝
    handleSend(record, results) {
      putAction(this.url.sendList, Object.assign(record, { results: results }))
        .then(res => {
          this.$message.success('派送成功')
          this.loadData()
          this.loadDataReady()
          this.workForm = {}
        })
        .catch(err => {
          err
        })
    },
    //点击生成
    reportSend(record) {
      putAction(this.url.sendList, Object.assign(record, { report: 1 }))
        .then(res => {
          this.$message.success('生成成功')
          this.loadDataReport()
          this.loadYw()
        })
        .catch(err => {
          err
        })
    },
    //点击生成报告
    loadDataReport() {
      var params = Object.assign(this.getQueryParams(), { results: 3, report: 0, pageSize: 10000 })
      getAction(this.url.list, params)
        .then(res => {
          this.dataSourceReport = res.result.records
        })
        .catch(err => {
          err
        })
    },
    //查询运维报告
    loadYw() {
      var params = Object.assign(this.getQueryParams(), { results: 3, report: 1, pageSize: 10000 })
      getAction(this.url.list, params)
        .then(res => {
          this.ywReportData = res.result.records
        })
        .catch(err => {
          err
        })
    }
  }
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
/deep/.ant-table-thead tr th {
  background-color: #62a8ed;
  color: #e0edfa;
}
/deep/ .ant-card {
  border-radius: 16px;
}
.col-card {
  position: relative;
  img {
    width: 100%;
  }
  .a-href {
    position: absolute;
    left: 16%;
    bottom: 20%;
    width: 100px;
    height: 12px;
    font-size: 12px;
    font-weight: 400;
    color: #62a8ed;
    line-height: 18px;
  }
}
.title-name {
  display: flex;
  position: relative;
  height: 30px;
  .title-href {
    width: 100px;
    height: 18px;
    font-size: 18px;
    font-weight: 400;
    color: #666666;
    line-height: 27px;
  }
  a {
    position: absolute;
    right: 2%;
  }
}
/deep/ .ant-table-placeholder {
  border-bottom: none
}
</style>
