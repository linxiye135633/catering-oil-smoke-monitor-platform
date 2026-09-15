<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/cor-card1.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.one }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ cardData.a1 }}</span>
        <span class="total-number">{{ cardData.b1 }}</span>
        <!-- <a class="a-href" @click="goMore">查看更多></a> -->
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/cor-card2.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.two }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ cardData.a2 }}</span>
        <span class="total-number">{{ cardData.b2 }}</span>
        <!-- <a class="a-href" @click="goMore">查看更多></a> -->
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/cor-card3.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.three }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ cardData.a3 }}</span>
        <span class="total-number">{{ cardData.b3 }}</span>
        <!-- <a class="a-href" @click="goMore">查看更多></a> -->
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <img src="../../assets/cor-card4.png" alt="" />
        <a-tooltip>
          <template slot="title">{{ toolTip.four }}</template>
          <a-icon class="card-question" type="question-circle" />
        </a-tooltip>
        <span class="month-add">{{ cardData.a4 }}</span>
        <span class="total-number">{{ cardData.b4 }}</span>
        <!-- <a class="a-href" @click="goMore">查看更多></a> -->
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/cor-card5.png" alt="" />
          <!-- <a class="title-href" slot="extra" href="#"> <a-icon type="plus-circle" /> 风控管理 </a> -->
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <wind-chart  :date="dateStr"></wind-chart>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="10" :xl="10" :style="{ marginBottom: '24px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/cor-card6.png" alt="" />
          <!-- <a class="title-href" slot="extra" href="#" @click="goMore"> 更多 > </a> -->
        </div>
        <area-chart :date="dateStr"></area-chart>
      </a-col>
      <a-col :sm="24" :md="14" :xl="14" :style="{ marginBottom: '24px' }">
        <div class="title-name">
          <img src="../../assets/cor-card7.png" alt="" />
          <!-- <a class="title-href" slot="extra" href="#" @click="goMore"> 更多 > </a> -->
        </div>
        <firm-table></firm-table>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/cor-card8.png" alt="" />
          <!-- <a class="title-href" slot="extra" href="#" @click="goMore"> 更多> </a> -->
        </div>
        <discharged-table></discharged-table>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import WindChart from './modules/WindChart'
import AreaChart from './modules/AreaChart'
import FirmTable from './modules/FirmTable'
import DischargedTable from './modules/DischargedTable'
export default {
  name: 'CorPorate',
  components: {
    WindChart,
    AreaChart,
    FirmTable,
    DischargedTable
  },
  data() {
    return {
      activeKey: [''],
      toolTip: {
        one: '指餐饮企业在近30天内，被标记的所有告警标签达到“20天次”以上，则为高风险企业。',
        two: '指餐饮企业在近30天内，被标记的所有告警标签不超过“20天次”，则为中风险企业。',
        three: '指餐饮企业在近30天内，被标记的所有告警标签不超过“10天次”，则为低风险企业。',
        four: '指餐饮企业在近30天内，从未被标记任何告警标签，则为无风险企业。'
      },
      cardData: {},
      url: {
        cardList: '/supervision/getRiskCount'
      },
      dateStr:''
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
      getAction(this.url.cardList).then(res => {
        this.cardData = res.result
      })
    },
    goMore() {
      this.$router.push({
        name: 'supervision-modules-List'
      })
    },
    getDate() {
       var nowDate = new Date()
    var year = nowDate.getFullYear()
    var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
    var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
    this.dateStr = year + '-' + month + '-' + day
    }
  }
}
</script>
<style lang="less" scoped>
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
    left: 50%;
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
.mr5 {
  margin-right: 5%;
}
.mr9 {
  margin-right: 9%;
}
</style>
