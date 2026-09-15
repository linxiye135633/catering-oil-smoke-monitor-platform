<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
        <a-collapse v-model="activeKey">
          <a-collapse-panel key="1" header="指标解释">
            <p>{{ toolTip.toolTip1 }}</p>
            <p>{{ toolTip.toolTip2 }}</p>
            <p>{{ toolTip.toolTip3 }}</p>
            <p>{{ toolTip.toolTip4 }}</p>
          </a-collapse-panel>
        </a-collapse>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="8" :xl="8" :style="{ marginBottom: '24px' }">
        <img src="../../assets/supWarn1.png" alt="" />
        <span class="month-add">{{ cardData.a1 }}</span>
        <span class="total-number">{{ cardData.b1 }}</span>
        <!-- <a class="a-href" @click="goMore1">查看更多></a> -->
      </a-col>
      <a-col class="col-card" :sm="24" :md="8" :xl="8" :style="{ marginBottom: '24px' }">
        <img src="../../assets/supWarn2.png" alt="" />
        <span class="month-add">{{ cardData.a2 }}</span>
        <span class="total-number">{{ cardData.b2 }}</span>
        <!-- <a class="a-href" @click="goMore2">查看更多></a> -->
      </a-col>
      <a-col class="col-card" :sm="24" :md="8" :xl="8" :style="{ marginBottom: '24px' }">
        <img src="../../assets/supWarn3.png" alt="" />
        <span class="month-add">{{ cardData.a3 }}</span>
        <span class="total-number">{{ cardData.b3 }}</span>
        <!-- <a class="a-href">查看更多></a> -->
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '10px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/supWarn4.png" alt="" />
          <!-- <div>
            <a class="title-href" slot="extra" href="#"> 更多 > </a>
          </div> -->
        </div>
      </a-col>
    </a-row>
    <a-row class="firm-table" :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '20px' }">
        <a-card style="height: 380px">
          <a-table
            ref="table"
            size="middle"
            rowKey="id"
            :columns="columns1"
            :dataSource="dataSource1"
            :pagination="false"
            :loading="loading"
            :scroll="{ y: 300 }"
            class="j-table-force-nowrap table-content"
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
            <span slot="action1" slot-scope="text, record">
              <a @click="view">{{ text }}</a>
            </span>
            <span slot="action" slot-scope="text, record">
              <!-- <a @click="openAppeal(record)">申诉</a>
              <a-divider type="vertical" /> -->
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
              </a-popconfirm>
            </span>
          </a-table>
        </a-card>
      </a-col>
      <!-- <a-col :sm="24" :md="24" :xl="8">
        <a-card style="height: 380px">
          <div class="chart-right">
            <div class="chart-title">
              <img src="../../assets/title-point.png" alt="" />
              <span>告警反馈处理进度</span>
            </div>
            <div class="progress-main">
              <div class="progress-bar">
                <p>
                  <span>本月反馈进度（本月已反馈/本月新增告警企）</span
                  ><a-progress :strokeColor="'#62A8ED'" :percent="30" />
                </p>
                <p>
                  <span>本月审核进度（本月已审核/本月新增反馈数）</span
                  ><a-progress :strokeColor="'#FDA67E'" :percent="70" />
                </p>
              </div>
              <div class="progress-pie">
                <div class="progress-img">
                  <div class="data-value">9</div>
                  <div class="text">累计待反馈告警企业记录数</div>
                </div>
                <div class="progress-img">
                  <div class="data-value">10</div>
                  <div class="text">累计待审核反馈数</div>
                </div>
                <div class="progress-img">
                  <div class="data-value">20</div>
                  <div class="text">本月新增告警企业记录数</div>
                </div>
                <div class="progress-img">
                  <div class="data-value">40</div>
                  <div class="text">本月已审核反馈数</div>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col> -->
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="16" :style="{ marginBottom: '10px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/supWarn5.png" alt="" />
          <!-- <div>
            <a class="title-href" slot="extra" href="#"> 更多 > </a>
          </div> -->
        </div>
      </a-col>
    </a-row>
    <a-row class="firm-table" :gutter="24">
      <a-col :sm="24" :md="24" :xl="16" :style="{ marginBottom: '20px' }">
        <a-card style="height: 350px">
          <a-table
            ref="table2"
            size="middle"
            rowKey="id"
            :columns="columns2"
            :dataSource="dataSource2"
            :pagination="false"
            :loading="loading"
            class="j-table-force-nowrap table-content"
            @change="handleTableChange"
             :scroll="{ y: 300 }"
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
        </a-card>
      </a-col>
      <a-col :sm="24" :md="24" :xl="8">
        <a-card style="height: 80px">
          <div class="title-name">
            <img src="../../assets/supWarn6.png" alt="" />
            <!-- <div>
              <a class="title-href" slot="extra" href="#"> 更多 > </a>
            </div> -->
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="16" :style="{ marginBottom: '10px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/supWarn8.png" alt="" />
          <!-- <div>
            <a class="title-href" slot="extra" href="#"> 更多 > </a>
          </div> -->
        </div>
      </a-col>
    </a-row>
    <a-row class="firm-table" :gutter="24">
      <a-col :sm="24" :md="24" :xl="16" :style="{ marginBottom: '20px' }">
        <a-card style="height: 350px">
          <a-table
            ref="table3"
            size="middle"
            rowKey="id"
            :columns="columns3"
            :dataSource="dataSource3"
            :pagination="false"
            :loading="loading"
            class="j-table-force-nowrap table-content"
              :scroll="{ y: 300 }"
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
          </a-table>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="24" :xl="8">
        <a-card style="height: 80px">
          <div class="title-name">
            <img src="../../assets/supWarn7.png" alt="" />
            <div>
              <a class="title-href" slot="extra" href="#"> 更多 > </a>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <warn-detail ref="warnDetail"></warn-detail>
    <appeal-page ref="appealPage"></appeal-page>
  </div>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import WarnDetail from './WarnDetail.vue'
import AppealPage from './Appeal'
export default {
  name: 'WarnPage',
  mixins: [JeecgListMixin, mixinDevice],
  components: { WarnDetail, AppealPage },
  data() {
    return {
      dataSource1: [],
      dataSource2: [],
      dataSource3: [],
      columns1: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'id',
          width:30,
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '告警企业名称',
          width:180,
          align: 'center',
          dataIndex: 'name'
        },
        {
          title: '测点MAC',
          align: 'center',
          width:80,
          dataIndex: 'pointMac'
        },
        {
          title: '告警类型',
          align: 'center',
          dataIndex: 'alarmType',
           width:130,
          customRender: function(t, r, index) {
            if (r.alarmType == 1) {
              return '营业时段停机'
            } else if (r.alarmType == 2) {
              return '没有联动开启'
            } else if (r.alarmType == 3) {
              return '二次电压过低'
            } else if (r.alarmType == 4) {
              return '设备联网异常'
            } else if (r.alarmType == 5) {
              return '烟气排放超标'
            } else if (r.alarmType == 6) {
              return '高压电场异常'
            } else if (r.alarmType == 7) {
              return '设备压差异常'
            } else if (r.alarmType == 8) {
              return '其他原因异常'
            }
          }
        },
        {
          title: '告警次数',
          align: 'center',
          dataIndex: 'amount',
          width:80,
          // scopedSlots: { customRender: 'action1' }
        },
        {
          title: '汇总周期',
          align: 'center',
          dataIndex: 'endTime',
           customRender: function(t, r, index) {
              return `${r.createTime}-${r.endTime}`
          },
          width:150
        },
        // {
        //   title: '	烟气排放超标',
        //   align: 'center',
        //   dataIndex: 'code6'
        // },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          // fixed: 'right',
          width: 130,
          scopedSlots: { customRender: 'action' }
        }
      ],
      columns2: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '告警编号',
          align: 'center',
          dataIndex: 'code2'
        },
        {
          title: '申诉标题',
          align: 'center',
          dataIndex: 'code1'
        },
        {
          title: '单位名称',
          align: 'center',
          dataIndex: 'code3'
        },
        {
          title: '申诉时间',
          align: 'center',
          dataIndex: 'code4'
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'code5'
        }
      ],
      columns3: [
        {
          title: '编号',
          align: 'center',
          dataIndex: 'code',
          scopedSlots: { customRender: 'tags' }
        },
        {
          title: '报备标题',
          align: 'center',
          dataIndex: 'code2'
        },
        {
          title: '单位名称',
          align: 'center',
          dataIndex: 'code1'
        },
        {
          title: '申诉时间',
          align: 'center',
          dataIndex: 'code3'
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'code4'
        }
      ],
      activeKey: [''],
      toolTip: {
        toolTip1:
          '告警企业：以自然日为单位，按照告警类型进行数据汇总，只要每个餐饮企业的某个告警类型的统计值大于零，就会生成一条告警企业记录。',
        toolTip2:
          '告警反馈：对每一条告警企业记录，要由对应的餐饮企业进行申诉反馈，即：解释为什么产生告警。这些数据也为环保执法提供依据。',
        toolTip3:
          '报备申请：指餐饮企业提前为其接下来的行为进行报备，例如：停业装修等，在停业装修期间由于断电等原因测电设备将不再上报数据，此时可以不生成告警信息。',
        toolTip4: '在线审核：指环保组织管理人员针对餐饮企业提交告警反馈、报备申请等进行在线审核，要么通过，要么不通过。'
      },
      cardData: {},
      url: {
        list:'/',
        cardList: '/supervision/getAlarmCount',
        listOne: '/supervision/getAlarmList', //告警企业列表
        delete:'/supervision/alarmDelete',
        listTwo:'/supervision/getAlarmLists1',//待审核告警反馈(申诉)
        listThree:'/supervision/getAlarmLists2'//待审核报备申请
      }
    }
  },
  created() {
    this.getCardData()
    this.getList1()
    this.getList2()
    this.getList3()
  },
  computed: {},
  methods: {
    //获取卡片数据
    getCardData() {
      getAction(this.url.cardList).then(res => {
        this.cardData = res.result
      })
    },
    getList1() {
      getAction(this.url.listOne).then(res => {
        this.dataSource1 = res.result.records
      })
    },
     getList2() {
      getAction(this.url.listTwo).then(res => {
        this.dataSource2 = res.result.records
      })
    },
    getList3() {
      getAction(this.url.listThree).then(res => {
        this.dataSource3 = res.result.records
      })
    },
    goMore1() {
      this.$router.push({
        name: 'supervision-WarnList1'
      })
    },
    goMore2() {
      this.$router.push({
        name: 'supervision-WarnList2'
      })
    },
    view() {
      this.$refs.warnDetail.open()
    },
    openAppeal(data) {
      //打开申诉
      this.$refs.appealPage.model.companyId=data.companyId
      this.$refs.appealPage.open()
    },
    handleTableChange(){
      
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
.col-card {
  position: relative;
  img {
    width: 100%;
  }
  span {
    font-size: 24px;
    font-family: SourceHanSansCN-Medium, SourceHanSansCN;
    font-weight: 500;
    color: #62a8ed;
    line-height: 54px;
  }
  .month-add {
    position: absolute;
    left: 28%;
    bottom: 49%;
    width: 58px;
    height: 36px;
  }
  .total-number {
    position: absolute;
    left: 28%;
    bottom: 32%;
    width: 77px;
    height: 36px;
  }
  .a-href {
    position: absolute;
    left: 11%;
    bottom: 20%;
    width: 100px;
    height: 12px;
    font-size: 12px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #62a8ed;
    line-height: 18px;
  }
}
.title-name {
  display: flex;
  position: relative;
  .title-href {
    width: 100px;
    height: 18px;
    font-size: 18px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #666666;
    line-height: 27px;
  }
  a {
    position: absolute;
    right: 2%;
  }
}
.chart-right {
  display: flex;
  flex-direction: column;
  position: relative;
  .progress-main {
    .progress-bar {
      margin-top: 4%;
      p {
        display: flex;
        margin-bottom: 12%;
        span {
          width: 460px;
          height: 14px;
          font-size: 14px;
          font-family: SourceHanSansCN-Normal, SourceHanSansCN;
          font-weight: 400;
          color: #666666;
          line-height: 21px;
        }
      }
    }
    height: 270px;
    .progress-pie {
      display: flex;
      text-align: center;
      .progress-img {
        flex: 1;
        .text {
          width: 90px;
          height: 32px;
          font-size: 14px;
          font-weight: 400;
          color: #666666;
          line-height: 16px;
          margin: 0 auto;
        }
        .data-value {
          margin: 0 auto;
          width: 83px;
          height: 83px;
          font-size: 36px;
          font-weight: 400;
          color: #62a8ed;
          line-height: 83px;
          background-image: url('../../assets/dataNum.png');
        }
      }
    }
  }
}
</style>
