<template>
  <j-modal :title="title" :width="width" :visible="visible" switchFullscreen @cancel="handleCancel" cancelText="关闭">
    <h2>已录入平台的餐饮企业（单位）档案信息</h2>
    <a-alert
      message=" - 说明：基本信息、扩展信息、默认账号、GIS地图位置、测点信息、移动端状态监测二维码、符合某些特征的企业标签（每日计算）、风险级别（每日计算）、营业时段信息。"
      type="info"
    />
    <a-row :gutter="24" style="padding-top: 20px">
      <a-col :span="16" style="height: 300px;">
        <div class="first-left">
          <h3>基本信息</h3>
          <h4>[接入状态: 带入信息] 带入信息</h4>
          <a-row :gutter="8">
            <a-col :span="4">企业编码:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">单位地址:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="4">默认联系人:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">所属服务商:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="4">创建日期:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">所属组织:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
        </div>
        <div class="first-left">
          <h3>扩展信息</h3>
          <a-row :gutter="8">
            <a-col :span="4">社会信用代码:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">单位类型:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="4">营业面积:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">经营类别:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="4">餐位数:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">废气治理模式:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="4">标准折算灶头数:</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="4">排污许可证编码:</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
        </div>
      </a-col>
      <a-col :span="8" style="height: 300px;">
        <div class="first-left">
          <h3>位置信息</h3>
          <a-input v-model="center" />
          <!-- center 带入的 经纬度 -->
          <baidu-map class="map" :center="center" :zoom="zoom"> </baidu-map>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24" style="padding-top: 20px">
      <a-col :span="16" style="height: 142.2px;">
        <div class="first-left" style="height: 100%">
          <h3>登录账号</h3>
          <a-row :gutter="8">
            <a-col :span="8">登陆账号</a-col>
            <a-col :span="8">名称</a-col>
            <a-col :span="8">状态</a-col>
          </a-row>
          <a-row :gutter="8" style="margin-top: 20px">
            <a-col :span="8">带入信息</a-col>
            <a-col :span="8">带入信息</a-col>
            <a-col :span="8">带入信息</a-col>
          </a-row>
        </div>
      </a-col>
      <a-col :span="8" style="height: 142.2px;">
        <div class="first-left">
          <h3>微信扫码（实时查看测点运行状态）</h3>
          <img src="./pic.png" style="width: 90px; height: 90px" />
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24" style="padding-top: 20px">
      <a-col :span="16" style="height: 100px;">
        <div class="first-left" style="height: 100%">
          <h3>企业标签</h3>
          <p>暂无标签</p>
        </div>
      </a-col>
      <a-col :span="8" style="height: 100px;">
        <div class="first-left">
          <h3>风险级别</h3>
          <p>无风险企业 <strong>最近风控评分：</strong> 分 <strong>最近风控时间：</strong></p>
        </div>
      </a-col>
    </a-row>
    <a-row style="padding-top: 20px">
      <div class="first-left">
      <h3>营业时段</h3>
      <p>信息带入</p>
      </div>
    </a-row>
    <a-row :gutter="24" style="padding-top: 20px">
      <a-col :span="24" style="height: 150px;">
        <div class="first-left">
          <h3>测点信息</h3>
          <h4>[接入状态: 带入信息] 带入信息</h4>
          <a-row :gutter="8">
            <a-col :span="2">测点名称:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">净化器技术路线:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">排风机设计风量:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">SIM卡号:	</a-col>
            <a-col :span="4">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="2">创建日期:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">净化器电场模块:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">标准灶头数:</a-col>
            <a-col :span="4">带入信息</a-col>
            <a-col :span="2">SIM卡截止日期:</a-col>
            <a-col :span="4">带入信息</a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="2">接入日期:</a-col>
            <a-col :span="4">带入信息</a-col>
          </a-row>
        </div>
      </a-col>
    </a-row>
  </j-modal>
</template>

<script>
export default {
  name: 'CompanyView',
  data() {
    return {
      title: '企业信息详情',
      width: 1400,
      visible: false,
      model: {
        name: '',
      },
      center: { lng: 116, lat: 34 },
      zoom: 15,
    }
  },
  methods: {
    open() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
  },
}
</script>
<style scoped>
.first-left {
  padding: 10px;
  border: 1px dashed #e9e9e9;
  border-radius: 6px;
  background-color: #fafafa;
  margin-bottom: 10px;
}
.map {
  width: 100%;
  height: 206px;
}
</style>
