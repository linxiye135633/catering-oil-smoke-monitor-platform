<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '10px' }">
        <img src="../../assets/mapcard1.png" alt="" />
        <span class="month-add">{{ companyCardData.companyNum }}</span>
        <span class="total-number">{{ companyCardData.pointNum }}</span>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '10px' }">
        <img src="../../assets/mapCard2.png" alt="" />
        <span class="percent-value" style="color: #b1aef6">{{ qyPercent }}%</span>
        <span class="month-add">{{ qyData.a2 }}</span>
        <span class="total-number">{{ qyData.a1 }}</span>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '10px' }">
        <img src="../../assets/mapcard3.png" alt="" />
        <span class="percent-value" style="color: #fca77a">{{ cdPercent }}%</span>
        <span class="month-add">{{ cdData.a2 }}</span>
        <span class="total-number">{{ cdData.a1 }}</span>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '10px' }">
        <img src="../../assets/mapCard4.png" alt="" />
        <span class="percent-value" style="color: #f0628f">{{ pkOline }}%</span>
        <span class="month-add">{{ pkData.onlineTime }}</span>
        <span class="total-number">{{ pkData.offline }}</span>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '24px' }">
        <map-module></map-module>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import MapModule from './mapShowModules/MapModule'
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'MapOverview',
  components: {
    MapModule
  },
  data() {
    return {
      companyCardData: {
        companyNum: '',
        pointNum: ''
      },
      cdData: {
        a1: '',
        a2: ''
      },
      qyData: {
        a1: '',
        a2: ''
      },
      pkData: {
        offline: 0,
        onlineTime: 0,
        total: 0
      },
      url: {
        companyList: '/company/count',
        pointList: '/point/count',
        pkList: '/home/pointRateFlow',
        cdList: '/base/statPointExcessive', //测点超标
        qyList: '/base/statCompanyExcessive' //企业超标
      }
    }
  },
  created() {
    this.getCardData()
  },
  computed: {
    pkOline() {
      let value = (this.pkData.onlineTime / this.pkData.total) * 100
      return `${Number(value.toString().match(/^\d+(?:\.\d{0,2})?/))}`
    },
    qyPercent() {
      let value = (this.qyData.a2 / this.qyData.a1) * 100
      return `${Number(value.toString().match(/^\d+(?:\.\d{0,2})?/))}`
    },
    cdPercent() {
      let value = (this.cdData.a2 / this.cdData.a1) * 100
      return `${Number(value.toString().match(/^\d+(?:\.\d{0,2})?/))}`
    }
  },
  methods: {
     getCardData() {
       getAction(this.url.companyList)
        .then(res => {
          this.companyCardData.companyNum = res
        })
        .catch(err => {
          err
        })
       getAction(this.url.pointList)
        .then(res => {
          this.companyCardData.pointNum = res
        })
        .catch(err => {
          err
        })
       getAction(this.url.qyList)
        .then(res => {
          this.qyData = res.result
        })
        .catch(err => {
          err
        })
       getAction(this.url.cdList)
        .then(res => {
          this.cdData = res.result
        })
        .catch(err => {
          err
        })
       getAction(this.url.pkList, { hours: '-3' })
        .then(res => {
          this.pkData = res.result
        })
        .catch(err => {
          err
        })
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
  .percent-value {
    position: absolute;
    top: 9%;
    left: 50%;
  }
  .month-add {
    position: absolute;
    left: 33%;
    bottom: 44%;
    width: 58px;
    height: 36px;
  }
  .total-number {
    position: absolute;
    left: 33%;
    bottom: 23%;
    width: 77px;
    height: 36px;
  }
}
</style>
