<template>
  <a-card :bordered="false">
    <h2>即时接入餐饮企业（单位）的监测点设备</h2>
    <a-alert
      message=" - 说明：可在 “搜索框” 中搜索已注册的餐饮企业单位即时接入测点设备，也可点击 “注册” 新餐饮单位链接注册新的餐饮企业（单位）并接入测点设备。"
      type="info"
    />
    <p class="title">即时接入</p>
    <p class="title-second">接入远程测点设备</p>
    <div class="search">
      <div class="search-pic"></div>
      <a-input-search
        class="search-input"
        placeholder="餐饮企业/门头名称关键字"
        enter-button
        size="large"
        @search="onSearch"
        v-model="companyName"
      />
    </div>
    <div class="clear"></div>
    <p class="title-third">输入关键字搜索已注册餐饮企业追加新测点</p>
    <div class="title-third" v-if="radioList.length">
      <a-radio-group v-model="companyId" @change="onChangeRadio">
        <a-radio v-for="(item, index) in radioList" :value="item.companyId" :key="index" :style="radioStyle">
             {{ item.enterpriseName }}
          </a-radio
        >
      </a-radio-group>
    </div>
    <p class="title-third">或者 <a-button type="link" @click="goCompanyAdd"> [ 注册 ] </a-button> 新餐饮企业</p>
    <p class="title-third">
      <a-button type="link" @click="goHomePage"> 返回首页 </a-button> <strong>|</strong>
      <a-button type="link" @click="goCompanyList"> 餐饮企业列表 </a-button> <strong>|</strong>
      <a-button type="link" @click="goPointList"> 测点列表 </a-button>
    </p>
    <a-modal title="提示" :visible="visible" @ok="handleOk" @cancel="handleCancel">
      <p>{{ ModalText }}</p>
    </a-modal>
  </a-card>
</template>

<script>
import { getAction } from '@/api/manage'
import { get } from 'http'
export default {
  name: 'PointRegist',
  components: {},
  data() {
    return {
      companyName: '',
      value: 1,
      radioStyle: {
        display: 'block',
        height: '30px',
        lineHeight: '30px',
      },
      url: {
        queryList: '/base/getPointByCompanyName',
        getCompanyList: '/company/queryById',
      },
      ModalText: '选定此单位?',
      visible: false,
      radioList: [],
      listData: [],
      companyId: '',
    }
  },
  created() {},
  methods: {
    onSearch() {
      let params = {
        name: this.companyName,
      }
      getAction(this.url.queryList, params).then((res) => {
        this.radioList = res.result.records
      })
    },
    goHomePage() {
      this.$router.push({ path: '/Dashboard/analysis' })
    },
    goCompanyList() {
      this.$router.push({ path: '/InstantAccess/companyList' })
    },
    goPointList() {
      this.$router.push({ path: '/InstantAccess/pointList' })
    },
    goCompanyAdd() {
      this.$router.push({
        path: '/InstantAccess/CompanyAdd',
        query: {
          pageType: 'enroll',
        },
      })
    },
    onChangeRadio(e) {
      this.visible = true
      this.getSendData()
    },
    handleOk(e) {
      setTimeout(() => {
        this.visible = false
      }, 2000)
      this.$router.push({
        path: '/InstantAccess/CompanyAdd',
        query: {
          pageType: 'edit',
          listData: this.listData,
        },
      })
    },
    handleCancel(e) {
      this.visible = false
    },
    //处理组要传到注册界面的数据
    getSendData() {
      getAction(this.url.getCompanyList, { id: this.companyId }).then((res) => {
        this.listData = res.result
      })
    },
  },
}
</script>
<style lang="less" scoped>
/* 清除浮动 */
.clear {
  clear: both;
  margin-bottom: 20px;
}
/* 三个标题 */
.title {
  margin-top: 60px;
  font-size: 35px;
  text-align: center;
  margin-bottom: 25px;
  font-weight: 300;
}
.title-second {
  font-size: 16px;
  text-align: center;
  margin-bottom: 5px;
}
.title-third {
  font-size: 16px;
  text-align: center;
  margin-bottom: 15px;
}
/* 搜索框样式 */
.search {
  width: 400px;
  margin: 0 auto;
}
.search-pic {
  position: relative;
  float: left;
  width: 80px;
  height: 80px;
  border: 5px solid rgb(94, 183, 252);
  border-radius: 50%;
  background-image: url(./search.png);
  background-repeat: no-repeat;
  background-size: 100% 100%;
  z-index: 10;
}
.search-input {
  position: relative;
  float: left;
  margin-left: -8px;
  margin-top: 22px;
  width: 320px;
}
& /deep/ .ant-radio-group {
  border: 1px solid #eee;
  text-align: left;
}
</style>
