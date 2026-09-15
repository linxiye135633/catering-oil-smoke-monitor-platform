<template>
  <div class="develop-chart-style test-content" hoverable>
    <a-card :bordered="false">
      <div class="search-header">
        <a-select
          show-search
          placeholder="请选择一个接入单位"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangeCompany"
          allowClear
          style="width: 40%; margin-right: 20px"
        >
          <a-select-option v-for="(item, index) in searchData" :key="index" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-select
          v-if="companyId != undefined"
          show-search
          placeholder="请选择一个测点"
          option-filter-prop="children"
          :filter-option="filterOption"
          @change="handleChangePoint"
          allowClear
          style="width: 16%; margin-right: 20px"
        >
          <a-select-option v-for="(item, index) in addressData" :key="index + item.id" :value="item.name">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-input
          style="width: 16%; margin-right: 20px"
          v-if="companyId == undefined"
          placeholder="请选择一个测点"
          v-model="pointId"
          allowClear
        ></a-input>
        <a-button style="margin-left: 20px" icon="search" @click="searchDatas" />
      </div>
      <a-row v-if="normal" :gutter="24" class="warn-title">
        <a-card :bordered="false">
          <h2>提示</h2>
          <h1>请选择一个单位或测点！</h1>
        </a-card>
      </a-row>
      <a-row v-if="cardShow" :gutter="24" class="warn-title">
        <a-col class="title-col" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <img src="../../assets/wt1.png" alt="" />
          <div class="title-text">排放超标告警</div>
        </a-col>
        <a-col class="title-col" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <img src="../../assets/wt2.png" alt="" />
          <div class="title-text">疑是故障告警</div>
        </a-col>
        <a-col class="title-col" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <img src="../../assets/wt3.png" alt="" />
          <div class="title-text">设备停机事件</div>
        </a-col>
      </a-row>
      <a-row v-if="cardShow" :gutter="24" class="warn-box">
        <a-col class="warn" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <div v-for="(item, index) in warnData1" :key="index">
            <div class="card card1">
              <div class="card-title">排放超标警告</div>
              <div v-if="item.text !== '解除超标告警'" class="card-text">
                <h1 style="color: red">{{ item.text }}</h1>
                <p>浓度:{{ item.data }} mg/m3</p>
                <p>标准:{{ item.norm }}</p>
                <p>时间:{{ item.time }}</p>
              </div>
              <div v-else class="card-text">
                <h1 style="color: red">{{ item.text }}</h1>
                <p>时间:{{ item.time }}</p>
              </div>
            </div>
          </div>
        </a-col>
        <a-col class="warn" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <div v-for="(item, index) in warnData2" :key="index">
            <div class="card card1">
              <div class="card-title">疑是故障警告</div>
              <div class="card-text">
                <h1 style="color: red">{{ item.text }}</h1>
                <p>查询时间:{{ item.time }}</p>
              </div>
            </div>
          </div>
        </a-col>
        <a-col class="warn" :sm="24" :md="24" :xl="8" :style="{ marginBottom: '10px' }">
          <div v-for="(item, index) in warnData3" :key="index">
            <div class="card card3">
              <div class="card-title">设备停机事件</div>
              <div class="card-text">
                <h1 style="color: red">{{ item.text }}</h1>
                <p>最后一次上传数据时间:{{ item.time }}</p>
                <p>查询时间:{{ item.timeNow }}</p>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage'
export default {
  name: 'AlarmList',
  components: {},
  data() {
    return {
      warningMessage: '暂未获取到告警信息',
      searchData: [],
      addressData: [],
      companyId: undefined,
      pointId: undefined,
      url: {
        pointList: '/base/getPointByCompanyId',
        list: '/company/list',
        warnList: '/base/getWarnEvent'
      },
      warnData1: [],
      warnData2: [],
      warnData3: [],
      cardShow: false,
      normal: true
    }
  },
  created() {},
  computed: {},
  methods: {
    handleChangeCompany(value) {
      this.companyId = value
      this.getPointList()
    },
    handleChangePoint(value) {
      console.log('v', value)
      this.pointId = value
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    //获取测点
    getPointList() {
      this.addressData = []
      getAction(this.url.pointList, { companyid: this.companyId }).then(res => {
        res.result.records.forEach(item => {
          this.addressData.push({ id: item.id, name: item.pointMac })
        })
      })
    },
    searchDatas() {
      this.getListData()
    },
    //获取数据
    getListData() {
      this.cardShow = true
      this.normal = false
      let params = { id: this.companyId, pointId: this.pointId }
      getAction(this.url.warnList, params).then(res => {
        this.warnData1 = res.result.a1
        this.warnData2 = res.result.a2
        this.warnData3 = res.result.a3
      })
    },
    //获取所有餐饮企业
    loadData() {
      getAction(this.url.list).then(res => {
        res.result.forEach(item => {
          this.searchData.push({ id: item.id, name: item.name })
        })
      })
    }
  },
  created() {
    this.loadData()
  }
}
</script>
<style lang="less" scoped>
.warn-box {
  .warn {
    .card {
      position: relative;
      max-width: 500px;
      height: 270px;
      background-size: 100% 100%;
      .card-title {
        position: absolute;
        top: 11%;
        left: 75px;
        height: 16px;
        width: 96px;
        font-size: 16px;
        font-weight: 400;
        color: #333333;
      }
      .card-text {
        max-width: 500px;
        height: 130px;
        position: absolute;
        top: 31%;
        left: 75px;
        p {
          max-width: 500px;
          height: 16px;
          font-size: 12px;
          font-weight: 400;
          color: #333333;
          line-height: 16px;
          margin-bottom: 15px;
        }
      }
    }
    .card1 {
      background: url('../../assets/warn1.png') no-repeat;
    }
    .card2 {
      background: url('../../assets/warn2.png') no-repeat;
    }
    .card3 {
      background: url('../../assets/warn3.png') no-repeat;
    }
  }
}
.warn-title {
  .title-col {
    position: relative;
    height: 80px;
    img {
      position: absolute;
      left: 60px;
      top: 20px;
    }
    .title-text {
      position: absolute;
      left: 80px;
      top: 35px;
      width: 189px;
      font-size: 22px;
      font-weight: 500;
      color: #ffffff;
    }
  }
}
</style>
