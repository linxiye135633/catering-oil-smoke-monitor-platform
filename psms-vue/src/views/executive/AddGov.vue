<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-modal
          title="行政机构"
          :visible="visible"
          @ok="handleOk"
          @cancel="handleCancel"
          width="80%"
        >
          <a-row :gutter="24">
            <a-col :sm="24" :md="12" :xl="12">
              <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="机构名称(必填)" prop="name">
                  <a-input v-model="form.name" placeholder="请输入机构名称" />
                </a-form-model-item>
                <a-form-model-item label="中文简称(选填)">
                  <a-input v-model="form.shortNameCn" placeholder="请输入中文简称" />
                </a-form-model-item>
                <a-form-model-item label="英文简称(选填)">
                  <a-input v-model="form.shortNameEn" placeholder="请输入英文简称" />
                </a-form-model-item>
                <a-form-model-item label="单位地址(必填)" prop="area">
                  <a-cascader
                    :options="areaData"
                    @change="addressChange"
                    placeholder="请选择单位所在区域"
                    style="margin-bottom: 8px"
                    v-model="form.area"
                  />
                </a-form-model-item>
                <a-form-model-item label="详细地址(必填)" prop="address">
                  <a-input v-model="form.address" placeholder="填写机构详细地址(请具体到门牌号)" />
                </a-form-model-item>
                <a-form-model-item label="默认管理员登陆账号(必填)" prop="adminCode">
                  <a-input v-model="form.adminCode" placeholder="请输入本单位管理员登陆账号" />
                </a-form-model-item>
                <a-form-model-item label="登陆密码(必填)" prop="adminPwd">
                  <a-input v-model="form.adminPwd" placeholder="请输入密码" />
                </a-form-model-item>
                <a-form-model-item label="联系人(必填)" prop="contact">
                  <a-input v-model="form.contact" placeholder="请填写联系人姓名" />
                </a-form-model-item>
                <a-form-model-item label="联系人手机号(必填)" prop="contactTel">
                  <a-input v-model="form.contactTel" placeholder="请填写11位手机号码" />
                </a-form-model-item>
              </a-form-model>
            </a-col>
            <a-col :sm="24" :md="12" :xl="12">
              <a-form-model-item label="地理位置(请在地图上点击,选择位置):">
                <a-input :value="coordinates" placeholder="点击地图获取经纬度" />
              </a-form-model-item>
              <baidu-map
                class="map"
                ak="jpdX5xQ4hioihYepstKk1IDBRb2HNGL1"
                :center="getCenter"
                :zoom="15"
                :scroll-wheel-zoom="true"
                @ready="handler"
                @click="getClickCoordinates"
              >
                <bm-control style="margin: 10px">
                  <bm-auto-complete v-model="keyword" :sug-style="{ zIndex: 99 }">
                    <a-input-search v-model="keyword" placeholder="请输入地名关键字" clearable style="width: 360px">
                    </a-input-search>
                  </bm-auto-complete>
                </bm-control>
                <bm-local-search :keyword="keyword" :auto-viewport="true" :panel="false" />
                <bm-marker :position="getCenter" :dragging="true"> </bm-marker>
              </baidu-map>
            </a-col>
          </a-row>
        </a-modal>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage'
import areaData from '@/views/instantAccess/areaData.js' //省市区三级联动数据
import { setTimeout } from 'timers';
import store from '@/store/'
import { BaiduMap, BmControl, BmView, BmAutoComplete, BmLocalSearch, BmMarker, BmGeolocation } from 'vue-baidu-map'
export default {
  name: 'AddGovPage',
  components: {BaiduMap,
    BmControl,
    BmView,
    BmAutoComplete,
    BmLocalSearch,
    BmMarker,
    BmGeolocation},
  props: {},
  data() {
    return {
      visible: false,
      //省市区数据
      areaData: areaData,
      map: {},
      BMap: {},
      center: { lng: 116.404, lat: 39.915 },
      keyword: '',
      url: {
        addGov: '/Institution/add',
      },
      labelCol: { span: 8 },
      wrapperCol: { span: 14 },
      other: '',
      area: [],
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
      coordinates: ' 116.404, 39.915',
      rules: {
        name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
        area: [{ required: true, message: '请选择机构地址', trigger: 'change' }],
        address: [{ required: true, message: '请输入机构详细地址', trigger: 'blur' }],
        adminCode: [{ required: true, message: '请输入默认登陆账号', trigger: 'blur' }],
        adminPwd: [{ required: true, message: '请输入登陆密码', trigger: 'blur' }],
        contact: [{ required: true, message: '请输入默认联系人姓名', trigger: 'blur' }],
        contactTel: [
          { required: true, message: '请输入联系人手机号', trigger: 'blur', pattern: /^1[3|4|5|7|8][0-9]\d{8}$/ },
        ],
      },
    }
  },
  computed: {
    getCenter() {
      if(this.form.lng) {
        return {lng:this.form.lng,lat:this.form.lat}
      }else {
        return `${store.getters.userInfo.realname}`
      }
    }
  },
  methods: {
    handleOk(e) {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.saveForm()
          this.form = {}
          this.center = { lng: 116.404, lat: 39.915 }
          this.coordinates = ''
          this.handleCancel()
          setTimeout(()=>{
             this.$parent.getGovData()
          },20)
        } else {
          return false
        }
      })
    },
    open() {
      this.visible = true
    },
    handleCancel() {
      this.center = { lng: 116.404, lat: 39.915 }
      this.coordinates = ''
      this.visible = false
    },
    addressChange(value) {
      this.form.area = value
    },
    //地图部分
    handler({ BMap, map }) {
      this.map = map
      this.BMap = BMap
    },
    getClickCoordinates(e) {
      this.coordinates = e.point.lng + ',' + e.point.lat
      this.form.lng = e.point.lng
      this.form.lat = e.point.lat
      this.center.lng = e.point.lng
      this.center.lat = e.point.lat
    },
    //保存
    saveForm() {
      postAction(this.url.addGov, Object.assign(this.form, { area: this.form.area.toString() }))
        .then((res) => {
         if (res.success == true) {
            this.$message.success('保存成功')
          }
        })
        .catch((err) => {
          err
        })
    },
  },
  created() {},
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 10px;
}
.map {
  height: 520px;
  //   width: 100%;
}
</style>
