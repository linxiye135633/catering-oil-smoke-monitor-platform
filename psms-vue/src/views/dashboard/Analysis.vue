<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/catering_ enterprises.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.one }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ cateringList.monthNum }}</span>
        <span class="total-number">{{ cateringList.total }}</span>
        <a class="a-href" @click="goMore(1)">查看更多></a>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/double-breasted.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.two }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ pointList.monthNum }}</span>
        <span class="total-number">{{ pointList.total }}</span>
        <a class="a-href" @click="goMore(2)">查看更多></a>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/reported-data.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.three }}</template>
          <a-icon class="mid-card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ appearLsit.monthNum }}</span>
        <span class="total-number">{{ appearLsit.total }}</span>
        <a class="a-href" @click="goMore(3)">查看更多></a>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/internet-card.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.four }}</template>
          <a-icon class="short-card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ onlineList.onlineTime }}</span>
        <span class="total-number">{{ onlineList.offline }}</span>
        <a class="a-href" @click="goMore(3)">查看更多></a>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/develop-title.png" alt="" />
          <a class="title-href" slot="extra" href="#" @click="goMore(4)"> <a-icon type="plus-circle" /> 即时接入 </a>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px' }">
        <development-chart></development-chart>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/point-equiptment.png" alt="" />
          <div>
            <a style="float:right;color:#666;font-size:18px;width:150px" slot="extra" href="#" @click="goMore(4)">
              <a-icon type="plus-circle" /> 新增
            </a>
            <a style="float:right;color:#666;font-size:18px" slot="extra" href="#" @click="goMore(2)">
              更多 <a-icon type="step-forward" />
            </a>
          </div>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px' }">
        <station-equipment></station-equipment>
      </a-col>
    </a-row>
    <a-row :gutter="12">
      <a-col :sm="12" :md="12" :xl="12" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/canyin-title.png" alt="" />
          <div>
            <a style="float:right;color:#666;font-size:18px;width:150px" slot="extra" href="#" @click="goMore(4)">
              <a-icon type="plus-circle" /> 新增
            </a>
            <a style="float:right;color:#666;font-size:18px" slot="extra" href="#" @click="goMore(1)">
              更多 <a-icon type="step-forward" />
            </a>
          </div>
        </div>
      </a-col>
      <a-col :sm="12" :md="12" :xl="12" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/fenlei-title.png" alt="" />
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px' }">
        <catering-enterprise></catering-enterprise>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/data-liuliang.png" alt="" /> <span class="month-date">{{ dateStr }}</span>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px' }">
        <data-trafic></data-trafic>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ paddingLeft: '10px' }">
        <a-card style="padding:0">
          <div class="title-name">
            <img src="../../assets/btitle1.png" alt="" />
            <div>
              <a style="float:left;font-size:18px;color:#666" slot="extra" href="#" @click="goMore(5)">
                更多 <a-icon type="step-forward" />
              </a>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ paddingLeft: '10px' }">
        <a-card style="padding:0">
          <div class="title-name">
            <img src="../../assets/btitle2.png" alt="" />
            <div>
              <a style="float:left;font-size:18px;color:#666" slot="extra" href="#" @click="goMore(6)">
                更多 <a-icon type="step-forward" />
              </a>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ paddingLeft: '10px' }">
        <a-card style="padding:0">
          <div class="title-name">
            <img src="../../assets/btitle3.png" alt="" />
            <div>
              <a style="float:left;font-size:18px;color:#666" slot="extra" href="#" @click="goMore(7)">
                更多 <a-icon type="step-forward" />
              </a>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ paddingLeft: '10px' }">
        <a-card style="padding:0">
          <div class="title-name">
            <img src="../../assets/btitle4.png" alt="" />
            <div>
              <a style="float:left;font-size:18px;color:#666" slot="extra" href="#" @click="goMore(8)">
                更多 <a-icon type="step-forward" />
              </a>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import CardMoudle from './modules/CardMoudle'
import DevelopmentChart from './modules/DevelopmentChart'
import StationEquipment from './modules/StationEquipment'
import CateringEnterprise from './modules/CateringEnterprise'
import DataTrafic from './modules/DataTrafic'
import { httpAction, getAction } from '@/api/manage'
export default {
  name: 'HomePage',
  components: {
    CardMoudle,
    DevelopmentChart,
    StationEquipment,
    CateringEnterprise,
    DataTrafic
  },
  data() {
    return {
      activeKey: [''],
      toolTip: {
        one: '指已录入平台的所有未注销的餐饮单位总数，包含已录入且未接入测点设备的企业。',
        two: '指已录入平台的所有未注销的排口测点总数，包含已录入且未接入的测点设备。',
        three: '指一段时间内所有测点上报到服务器的数据包总数。',
        four: '指在24小时内至少向服务器上报过一条数据包的所有测点数与排口测点总数的占比。'
      },
      cardData: {
        cateringAdd: '32', //餐饮企业本月新增
        cateringTotal: '32', //餐饮企业累计数量
        pointAdd: '32', //排扣测点数
        pointTotal: '32', //排扣累计数量
        appearAdd: '0', //上报数据流量
        appearTotal: '0', //累计上报
        interNetOnline: '0', //在线数量
        offLine: '0' //离线数量
      },
      //餐饮企业数
      cateringList: {},
      //排口测点数
      pointList: {},
      //上报数据流量
      appearLsit: {},
      //测点联网率
      onlineList: {},
      url: {
        cateringList: '/home/statCompany',
        pointList: '/home/pointCompany',
        appearLsit: '/home/lampblackRateFlow',
        onlineList: '/home/pointRateFlow'
      },
      dateStr: ''
    }
  },
  created() {
    this.getCardData()
    this.getDate()
  },
  computed: {},
  methods: {
    //获取卡片数据
    getCardData() {
      getAction(this.url.cateringList).then(res => {
        this.cateringList = res.result
      })
      getAction(this.url.pointList).then(res => {
        this.pointList = res.result
      })
      getAction(this.url.appearLsit).then(res => {
        this.appearLsit = res.result
      })
      getAction(this.url.onlineList, { hours: '-24' }).then(res => {
        this.onlineList = res.result
      })
    },
    goMore(data) {
      if (data == 1) {
        this.$router.push({
          //餐饮企业
          name: 'InstantAccess-CompanyList'
        })
      } else if (data == 2) {
        this.$router.push({
          //测点设备
          name: 'InstantAccess-PointList'
        })
      } else if (data == 3) {
        this.$router.push({
          //数据查询
          name: 'OnlineMonitor-DataList'
        })
      } else if (data == 4) {
        this.$router.push({
          //测点接入
          name: 'InstantAccess-PointRegist'
        })
      } else if (data == 5) {
        //运维管理 list 1
        this.$router.push({
          name: 'aiops-Management'
        })
      } else if (data == 6) {
        //运维报告
        this.$router.push({
          name: 'aiops-OpsReport'
        })
      } else if (data == 7) {
        //告警反馈
        this.$router.push({
          name: 'supervision-Warning'
        })
      } else if (data == 8) {
        //运维管理 list 1
        this.$router.push({
          name: 'aiops-Management'
        })
      }
    },
    getDate() {
      var nowDate = new Date()
      var year = nowDate.getFullYear()
      var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
      this.dateStr = year + '-' + month
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 16px;
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
    left: 33%;
    bottom: 49%;
    width: 58px;
    height: 36px;
  }
  .total-number {
    position: absolute;
    left: 33%;
    bottom: 32%;
    width: 77px;
    height: 36px;
  }
  .a-href {
    position: absolute;
    left: 15.5%;
    bottom: 15%;
    width: 100px;
    height: 12px;
    font-size: 12px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #62a8ed;
    line-height: 18px;
  }
  .anticon-question-circle {
    position: absolute;
    color: #62a8ed;
    bottom: 75%;
  }
  .card-question {
    left: 62%;
  }
  .mid-card-question {
    left: 44%;
  }
  .short-card-question {
    left: 39%;
  }
}
.title-name {
  display: flex;
  position: relative;
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
.mr5 {
  margin-right: 5%;
}
.mr9 {
  margin-right: 9%;
}
.month-date {
  width: 244px;
  height: 18px;
  font-size: 18px;
  font-weight: 400;
  color: #333333;
  line-height: 27px;
}
</style>
