<template>
  <a-card :bordered="false">
    <h2>操作提示</h2>
    <a-alert
      message=" - 说明：第一步：录入基本信息，第二步：录入扩展信息，第三步：录入测点信息，第四步：录入营业时段"
      type="info"
    />
    <div class="container">
      <a-steps v-if="pageType == 'edit'" :current="current" @change="stepChange">
        <a-step v-for="item in steps" :key="item.title" :title="item.title" />
      </a-steps>
      <a-steps v-else :current="current">
        <a-step v-for="item in steps" :key="item.title" :title="item.title" />
      </a-steps>
      <div class="steps-content">
        <a-form-model ref="ruleForm" :model="enteringForm" :rules="rules" layout="vertical">
          <a-row :gutter="16" v-if="current == 0" style="padding: 0 20px">
            <a-col :span="10">
              <a-form-model-item label="请选择服务厂商:(必填)" prop="manufacturerCode">
                <a-select v-model="enteringForm.manufacturerCode" placeholder="请选择服务厂商">
                  <a-select-option v-for="(item, index) in factData" :key="index" :value="item.code">
                    {{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
              <a-form-model-item label="请选择行政机构:(选填)">
                <a-select v-model="enteringForm.institutionCode" placeholder="请选择行政机构">
                  <a-select-option v-for="(item, index) in institutionData" :key="index" :value="item.code">
                    {{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-model-item>
              <a-form-model-item v-if="pageType === 'enroll'" label="默认登录账号:(必填)" prop="adminCode">
                <a-input v-model="enteringForm.adminCode" placeholder="请输入本单位默认的登录账号" />
              </a-form-model-item>
              <a-form-model-item v-if="pageType === 'enroll'" label="登录密码:(必填)" prop="adminPwd">
                <a-input v-model="enteringForm.adminPwd" placeholder="请输入密码" />
              </a-form-model-item>
              <a-form-model-item label="单位编码:(可选)">
                <a-input v-model="enteringForm.code" placeholder="请填写所属单位编码" />
              </a-form-model-item>
              <a-form-model-item label="单位名称:(必填)" prop="name">
                <a-input v-model="enteringForm.name" placeholder="请填写所属单位名称" />
              </a-form-model-item>
              <a-form-model-item label="门头名称:(选填)">
                <a-input v-model="enteringForm.doorName" placeholder="请填写所属单位店铺门头名称" />
              </a-form-model-item>
              <a-form-model-item label="单位地址:(必填)" prop="areaCode">
                <a-cascader
                  :options="areaData"
                  @change="onChange"
                  placeholder="请选择单位所在区域"
                  style="margin-bottom: 8px"
                  v-model="enteringForm.areaCode"
                />
              </a-form-model-item>
              <a-input
                v-model="enteringForm.streetCode"
                placeholder="请填写单位所在街道（非必填）"
                style="margin-bottom: 8px"
              />
              <a-input v-model="enteringForm.address" placeholder="填写单位详细地址（请具体到门牌号）" />
              <a-form-model-item label="默认联系人:(必填)" prop="contact">
                <a-input v-model="enteringForm.contact" placeholder="请填写联系人姓名" />
              </a-form-model-item>
              <a-form-model-item label="联系人手机号:(必填)" prop="contactMobile">
                <a-input v-model="enteringForm.contactMobile" placeholder="请填写11位手机号码" />
              </a-form-model-item>
            </a-col>
            <a-col :span="14">
              <a-form-model-item label="地理位置(请在地图上点击,选择位置):">
                <a-input v-model="model.coordinates" placeholder="点击地图获取经纬度" />
              </a-form-model-item>
              <baidu-map
                class="map"
                ak="jpdX5xQ4hioihYepstKk1IDBRb2HNGL1"
                :center="getCenter"
                :zoom="zoom"
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
          <div v-if="current == 1" class="step2-box">
            <a-row :gutter="8">
              <a-col :span="6">
                <a-form-model-item label="统一社会信用代码:">
                  <a-input
                    v-model="enteringForm.creditCode"
                    placeholder="请输入营业执照上的统一社会信用代码"
                  /> </a-form-model-item
              ></a-col>
              <a-col :span="6">
                <a-form-model-item label="经营类别:">
                  <a-select v-model="enteringForm.businessCategory" placeholder="请选择经营类别">
                    <a-select-option value="A类"> A类：无灶头或废气排放小 </a-select-option>
                    <a-select-option value="B类"> B类：经营一般中餐西餐等 </a-select-option>
                    <a-select-option value="C类"> C类：烧烤等污染强度较高 </a-select-option>
                    <a-select-option value="D类"> D类：食堂 </a-select-option>
                    <a-select-option value="E类"> E类：中央厨房 </a-select-option>
                  </a-select>
                </a-form-model-item></a-col
              >
              <a-col :span="6">
                <a-form-model-item label="单位类型:">
                  <a-select v-model="enteringForm.unitCategory" placeholder="请选择单位类型">
                    <a-select-option value="1"> 餐饮经营单位 </a-select-option>
                    <a-select-option value="2"> 单位食堂 </a-select-option>
                    <a-select-option value="3"> 中央厨房 </a-select-option>
                    <a-select-option value="4"> 集体用餐配送单位 </a-select-option>
                  </a-select>
                </a-form-model-item></a-col
              >
              <a-col :span="6">
                <a-form-model-item label="废气治理模式:">
                  <a-select v-model="enteringForm.governanceModel" placeholder="请选择废气治理模式">
                    <a-select-option value="1"> 自主开展 </a-select-option>
                    <a-select-option value="2"> 第三方治理 </a-select-option>
                  </a-select>
                </a-form-model-item></a-col
              >
            </a-row>
            <a-row :gutter="8">
              <a-col :span="6">
                <a-form-model-item label="营业面积（平方米）:">
                  <a-input-number
                    style="width: 100%"
                    v-model="enteringForm.businessArea"
                    placeholder="请输入营业面积"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="餐位数:">
                  <a-input v-model="enteringForm.mealNumber" placeholder="请输入餐位数" />
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="标准折算灶头数:">
                  <a-input v-model="enteringForm.stoveNumber" placeholder="请输入标准折算灶头数" />
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="排污许可证编码:">
                  <a-input v-model="enteringForm.permitCode" placeholder="请输入排污许可证编码（可选）" />
                </a-form-model-item>
              </a-col>
            </a-row>
          </div>
          <div v-if="current == 2" class="step2-box">
            <div class="pointList">
              <h3>
                点选测点 <a-button type="primary" style="float: right" @click="handleAddNewPoint">添加新测点</a-button>
              </h3>
              <a-radio-group v-model="pointId" @change="getPointForm">
                <a-radio v-for="(item, index) in clickPointList" :value="item.id" :key="index">{{
                  item.enterpriseName
                }}</a-radio>
              </a-radio-group>
            </div>
            <div class="pointList">
              <h3>测点信息</h3>
              <a-row :gutter="8">
                <a-col :span="6">
                  <a-form-model-item label="测点类型:">
                    <a-select disabled v-model="ceDianList.pointType" placeholder="请选择测点类型">
                      <a-select-option value="1" selected> 油烟监测 </a-select-option>
                    </a-select>
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="测点MAC:">
                    <a-input disabled v-model="ceDianList.pointMac" placeholder="请输入测点MAC" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="标准灶头数（个）:">
                    <a-input disabled v-model="ceDianList.stoveNumber" placeholder="请输入标准灶头数（个）" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="SIM卡号:">
                    <a-input disabled v-model="ceDianList.simCode" placeholder="请输入SIM卡号" />
                  </a-form-model-item>
                </a-col>
              </a-row>
              <a-row :gutter="8">
                <a-col :span="6">
                  <a-form-model-item label="测点状态:">
                    <a-input disabled v-model="ceDianList.status" placeholder="请输入测点状态" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="接入时间:">
                    <a-input disabled v-model="ceDianList.connDate" placeholder="请输入接入时间" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="排口名称:">
                    <a-input disabled v-model="ceDianList.portName" placeholder="请输入排口名称" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="SIM卡截止日期:">
                    <j-date
                      disabled
                      placeholder="请选择SIM卡截止日期"
                      v-model="ceDianList.simCloseDate"
                      style="width: 100%"
                    />
                  </a-form-model-item>
                </a-col>
              </a-row>
            </div>
            <div class="pointList">
              <h3>设备信息</h3>
              <a-row :gutter="8">
                <a-col :span="6">
                  <a-form-model-item label="净化器技术路线:">
                    <a-select disabled v-model="ceDianList.techRoadmap" placeholder="请选择净化器技术路线">
                      <a-select-option value="静电式" selected> 静电式 </a-select-option>
                      <a-select-option value="机械式" selected> 机械式 </a-select-option>
                      <a-select-option value="湿式" selected> 湿式 </a-select-option>
                      <a-select-option value="复合式" selected> 复合式 </a-select-option>
                      <a-select-option value="其他" selected> 其他 </a-select-option>
                    </a-select>
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="排风机设计风量（立方米/秒）:">
                    <a-input disabled v-model="ceDianList.airVolume" placeholder="请输入排风机设计风量（立方米/秒）" />
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="电场模块数量:">
                    <a-input disabled v-model="ceDianList.modelNumber" placeholder="请输入电场模块数量:" />
                  </a-form-model-item>
                </a-col>
              </a-row>
            </div>
          </div>
          <div v-if="current == 3">
            <div class="pointList">
              <h3>营业时段</h3>
              <a-row>
                <a-col :span="6">
                  <a-form-model-item label="起始时间（每日）:">
                    <div>
                      <a-input v-model="enteringForm.startHour" placeholder="小时" style="width: 40%"></a-input> :
                      <a-input v-model="enteringForm.startMinute" placeholder="分钟" style="width: 40%"></a-input>
                    </div>
                  </a-form-model-item>
                </a-col>
                <a-col :span="6">
                  <a-form-model-item label="结束时间（每日）:">
                    <a-input v-model="enteringForm.endHour" placeholder="小时" style="width: 40%"></a-input> :
                    <a-input v-model="enteringForm.endMinute" placeholder="分钟" style="width: 40%"></a-input>
                  </a-form-model-item>
                </a-col>
              </a-row>
            </div>
          </div>
          <div class="steps-action">
            <a-button v-if="current == 0" @click="saveAdd"> 下一步 </a-button>
            <a-button v-if="current == 1" @click="editAdd"> 下一步 </a-button>
            <a-button v-if="current == 2" type="primary" @click="saveAdd2">下一步 </a-button>

            <a-button v-if="current == steps.length - 1" type="primary" @click="submitForm"> 提交 </a-button>
            <a-button v-if="current > 0" style="margin-left: 8px" @click="prev"> 上一步 </a-button>
          </div>
        </a-form-model>
      </div>
    </div>
    <add-new-point
      :ID="enteringForm.id"
      :Name="enteringForm.name"
      @refresh="getClickPoint"
      ref="modalForm"
    ></add-new-point>
  </a-card>
</template>

<script>
import areaData from './areaData' //省市区三级联动数据
import AddNewPoint from './AddNewPoint'
import { getAction, postAction, putAction } from '@/api/manage'
import { setTimeout } from 'timers'
import store from '@/store/'
import { BaiduMap, BmControl, BmView, BmAutoComplete, BmLocalSearch, BmMarker, BmGeolocation } from 'vue-baidu-map'
export default {
  name: 'CompanyAdd',
  components: {
    AddNewPoint,
    BaiduMap,
    BmControl,
    BmView,
    BmAutoComplete,
    BmLocalSearch,
    BmMarker,
    BmGeolocation,
  },
  data() {
    return {
      addId: '',
      pointId: '',
      pageType: '', //当前页面状态
      current: 0,
      enteringForm: {
        id: '',
        manufacturerCode: '', //服务厂商
        institutionCode: '', //行政机构
        adminCode: '', //默认登陆账号
        adminPwd: '', //登陆密码
        code: '', //单位编码
        name: '', //单位名称
        doorName: '', //门头名称
        areaCode: [], //单位行政区域
        streetCode: '', //单位所属街道
        address: '', //单位地址
        contact: '', //默认联系人
        contactMobile: '', //联系人手机号
        lng: null,
        lat: null,

        creditCode: '', //统一社会信用代码
        businessCategory: '', //经营类别
        unitCategory: '', //单位类型
        governanceModel: '', //废气治理模式
        businessArea: '', //营业面积（平方米）
        mealNumber: '', //餐位数
        stoveNumber: '', //标准折算灶头数
        permitCode: '', //排污许可证编码
        startHour: '', //起始时间（小时）
        startMinute: '', //起始时间（分钟）
        endHour: '', //借宿时间（小时）
        endMinute: '', //结束时间（分钟）
      }, //录入表单
      ceDianList: {
        pointType: '', //测点类型
        pointMac: '', //测点MAC
        stoveNumber: '', //标准灶头数（个）
        simCode: '', //SIM卡号
        status: '', //测点状态
        connDate: '', //接入时间
        portName: '', //排口名称
        simCloseDate: '', //SIM卡截止日期
        techRoadmap: '', //净化器技术路线
        airVolume: '', //排风机设计风量（立方米/秒）
        modelNumber: '', //电场模块数量
      },
      steps: [
        {
          title: '录入基本信息',
        },
        {
          title: '录入扩展信息',
        },
        {
          title: '录入测点信息',
        },
        {
          title: '录入营业时段',
        },
      ],
      model: {
        name: '',
        coordinates: '',
      },
      //省市区数据
      areaData: areaData,
      selectdAreaData: [],
      //地图数据
      map: {},
      BMap: {},
      center: { lng: 116.404, lat: 39.915 },
      zoom: 15,
      keyword: '',
      url: {
        add: '/company/add',
        edit: '/company/edit',
        list: '/company/queryById',
        pointEdit: '/point/edit',
        clickPointList: '/base/getPointByCompanyId',
        clickPoint: '/point/queryById',
        factList: '/Manufacturer/list',
        institutionList: 'Institution/list',
      },
      rules: {
        manufacturerCode: [{ required: true, message: '请选择服务厂商', trigger: 'change' }],
        adminCode: [{ required: true, message: '请输入默认登陆账号', trigger: 'blur' }],
        adminPwd: [{ required: true, message: '请输入登陆密码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
        areaCode: [{ required: true, message: '请选择单位地址', trigger: 'change' }],
        contact: [{ required: true, message: '请输入默认联系人姓名', trigger: 'blur' }],
        contactMobile: [
          { required: true, message: '请输入联系人手机号', trigger: 'blur', pattern: /^1[3|4|5|7|8][0-9]\d{8}$/ },
        ],
      },
      clickPointList: [],
      factData: [],
      institutionData: [],
    }
  },
  watch: {
    current: {
      immediate: true,
      handler(val) {
        if (val == 2) {
          this.getClickPoint()
        }
      },
    },
  },
  computed: {
    getCenter() {
      if(this.enteringForm.lng) {
        return {lng:this.enteringForm.lng,lat:this.enteringForm.lat}
      }else {
        return `${store.getters.userInfo.realname}`
      }
    }
  },
  methods: {
    onChange(value) {
      this.enteringForm.areaCode = value
    },
    //地图部分
    handler({ BMap, map }) {
      this.map = map
      this.BMap = BMap
    },
    getClickCoordinates(e) {
      this.model.coordinates = e.point.lng + ',' + e.point.lat
      this.enteringForm.lng = e.point.lng
      this.enteringForm.lat = e.point.lat
    },
    //第三页 添加新监测点
    handleAddNewPoint() {
      this.$refs.modalForm.open()
    },
    // 第四页
    onTimeStartChange(time, timeString) {
      console.log(time, timeString)
    },
    onTimeEndChange(time, timeString) {
      console.log(time, timeString)
    },
    //步骤条部分
    next() {
      this.current++
    },
    prev() {
      this.pageType = 'edit'
      this.current--
    },
    //新增保存
    saveAdd() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (this.pageType === 'edit') {
            //编辑保存
            let params = Object.assign(
              this.enteringForm,
              { areaCode: this.enteringForm.areaCode.toString() },
              // { id: this.addId != '' ? this.addId : this.enteringForm.id }
              { id: this.enteringForm.id }
            )
            putAction(this.url.edit, params)
              .then((res) => {
                this.$message.success('保存成功')
                this.current++
              })
              .catch((err) => {
                err
              })
          } else {
            let params = Object.assign(this.enteringForm, { areaCode: this.enteringForm.areaCode.toString() })
            postAction(this.url.add, params)
              .then((res) => {
                this.$message.success('保存成功')
                this.current++
                this.$nextTick(() => {
                  this.addId = res.result
                  getAction(this.url.list, { id: res.result }).then((res) => {})
                })
              })
              .catch((err) => {
                err
              })
          }
        } else {
          return false
        }
      })
    },
    // 测点保存
    saveAdd2() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let params = Object.assign(this.ceDianList, { id: this.addId != '' ? this.addId : this.enteringForm.id })
          putAction(this.url.pointEdit, params)
            .then((res) => {
              this.$message.success('保存成功')
              this.current++
            })
            .catch((err) => {
              err
            })
        } else {
          return false
        }
      })
    },
    editAdd() {
      let params = Object.assign(
        this.enteringForm,
        { areaCode: this.enteringForm.areaCode.toString() },
        { id: this.addId != '' ? this.addId : this.enteringForm.id }
      )
      putAction(this.url.edit, params)
        .then((res) => {
          this.$message.success('保存成功')
          this.current++
        })
        .catch((err) => {
          err
        })
    },
    //渲染编辑表单
    getForm() {
      if (this.pageType === 'edit') {
        let lng = this.$route.query.listData.lng
        let lat = this.$route.query.listData.lat
        this.center.lng = lng
        this.center.lat = lat
        this.model.coordinates = lng + ',' + lat
        this.enteringForm = Object.assign(this.$route.query.listData, {
          areaCode: this.$route.query.listData.areaCode.split(','),
          lng,
          lat,
        })
      }
    },
    stepChange(current) {
      this.current = current
    },
    submitForm() {
      this.editAdd()
      this.$router.push('/InstantAccess/CompanyList')
    },
    //查询有多少点选测点
    getClickPoint() {
      getAction(this.url.clickPointList, { companyid: this.enteringForm.id })
        .then((res) => {
          this.$nextTick(() => {
            this.clickPointList = res.result.records
          })
        })
        .catch((err) => {
          err
        })
    },
    //查询点位数据---不可编辑
    getPointForm() {
      getAction(this.url.clickPoint, { id: this.pointId })
        .then((res) => {
          this.ceDianList = {}
          this.ceDianList = res.result
        })
        .catch((err) => {
          err
        })
    },
    //获取服务厂商
    getFact() {
      getAction(this.url.factList)
        .then((res) => {
          this.factData = res.result
        })
        .catch((err) => {
          err
        })
    },
    //获取行政机构
    getInstitution() {
      getAction(this.url.institutionList)
        .then((res) => {
          this.institutionData = res.result
        })
        .catch((err) => {
          err
        })
    },
  },
  created() {
    this.pageType = this.$route.query.pageType
    this.getForm()
    this.getFact()
    this.getInstitution()
  },
}
</script>
<style scoped>
.container {
  padding-top: 30px;
}
.steps-content {
  margin-top: 16px;
  border: 1px dashed #e9e9e9;
  border-radius: 6px;
  background-color: #fafafa;
  min-height: 200px;
  padding: 20px;
}
/* 下一步按钮样式 */
.steps-action {
  text-align: right;
}
/* 第一页 */
.left-box {
  display: inline-block;
  width: 40%;
  min-width: 200px;
}
.right-box {
  display: inline-block;
  width: 50%;
}
.map {
  height: 752px;
  width: 100%;
}
/* 第三页 */
.pointList {
  padding: 10px 20px;
  background: #fff;
  border-radius: 10px;
  margin-bottom: 20px;
}
/* 缩小本页表单间距 */
.ant-form-item {
  margin-bottom: 8px !important;
}
</style>
<style>
/* 去掉百度地图左下角logo */
.anchorBL {
  display: none;
}
</style>
