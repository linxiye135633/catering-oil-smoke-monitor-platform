<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    switchFullscreen
    @cancel="handleCancel"
    @ok="handleOk"
    cancelText="关闭"
  >
    <j-form-container>
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail" layout="vertical">
        <div class="pointList">
          <h3>基本信息</h3>
          <a-row :gutter="16">
            <a-col :span="3">
              <h4>企业编码</h4>
              <p>{{ companyInfo.code }}</p>
            </a-col>
            <a-col :span="4">
              <h4>企业名称</h4>
              <p>{{ companyInfo.name }}</p>
            </a-col>
            <a-col :span="3">
              <h4>联系人</h4>
              <p>{{ companyInfo.contact }}</p>
            </a-col>
            <a-col :span="4">
              <h4>联系人手机号</h4>
              <p>{{ companyInfo.contactMobile }}</p>
            </a-col>
            <a-col :span="4">
              <h4>所在区域</h4>
              <p>{{ companyInfo.areaCode }}</p>
            </a-col>
            <a-col :span="4">
              <h4>企业地址</h4>
              <p>{{ companyInfo.address }}</p>
            </a-col>
            <a-col :span="2">
              <h4>状态</h4>
              <p>
                已接入
              </p>
              <!-- <p v-if="companyInfo.status == '01'">已接入</p>
              <p v-else>未接入</p> -->
            </a-col>
          </a-row>
        </div>
        <div class="pointList">
          <h3>测点信息</h3>
          <a-row :gutter="8">
            <a-col :span="6">
              <a-form-model-item label="测点类型:">
                <a-select v-model="model.pointType" placeholder="请选择测点类型" prop="pointType">
                  <a-select-option value="01" selected>油烟监测</a-select-option>
                </a-select>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="测点MAC:">
                <a-input v-model="model.pointMac" placeholder="请输入测点MAC" prop="pointMac" />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="标准灶头数（个）:">
                <a-input-number
                  v-model="model.stoveNumber"
                  placeholder="请输入标准灶头数（个）"
                  style="width: 100%"
                  prop="stoveNumber"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="SIM卡号:">
                <a-input v-model="model.simCode" placeholder="请输入SIM卡号" prop="simCode" />
              </a-form-model-item>
            </a-col>
          </a-row>
          <a-row :gutter="8">
            <a-col :span="6">
              <a-form-model-item label="测点状态:">
                <a-input v-model="statusName" :disabled="true" prop="status" />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="接入日期:">
                <j-date
                  placeholder="请选择SIM卡接入日期"
                  v-model="model.createTime"
                  style="width: 100%"
                  :disabled="true"
                  prop="connDate"
                  format="YYYY-MM-DD HH:mm:ss"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="排口名称:">
                <a-input v-model="model.portName" placeholder="请输入排口名称" prop="portName" />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="SIM卡截止日期:">
                <a-date-picker
                  placeholder="请选择SIM卡截止日期"
                  v-model="model.simCloseDate"
                  style="width: 100%"
                  prop="simCloseDate"
                  show-time
                  format="YYYY-MM-DD HH:mm:ss"
                  @change="onTimeChange"
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
                <a-select v-model="model.techRoadmap" placeholder="请选择净化器技术路线" prop="techRoadmap">
                  <a-select-option value="01" selected> 静电式 </a-select-option>
                  <a-select-option value="02" selected> 机械式 </a-select-option>
                  <a-select-option value="03" selected> 湿式 </a-select-option>
                  <a-select-option value="04" selected> 复合式 </a-select-option>
                  <a-select-option value="05" selected> 其他 </a-select-option>
                </a-select>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="排风机设计风量（立方米/秒）:">
                <a-input-number
                  v-model="model.airVolume"
                  :step="0.01"
                  placeholder="请输入排风机设计风量（立方米/秒）"
                  style="width: 100%"
                  prop="airVolume"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="电场模块数量:">
                <a-input-number
                  v-model="model.modelNumber"
                  placeholder="请输入电场模块数量:"
                  style="width: 100%"
                  prop="modelNumber"
                />
              </a-form-model-item>
            </a-col>
          </a-row>
        </div>
      </a-form-model>
    </j-form-container>
  </j-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'

export default {
  name: 'Editoint',
  props: {},
  data() {
    return {
      title: '编辑测点',
      width: 1200,
      visible: false,
      validatorRules: {
        point_mac: [{ required: true, message: '请输入测点MAC!' }],
        sim_code: [{ required: true, message: '请输入SIM卡号!' }],
        port_name: [{ required: true, message: '请输入排口名称!' }],
      },
      url: {
        edit: '/point/edit',
        getCompanyInfo: '/company/queryById',
      },
      model: {},
      companyInfo: {},
      statusName:''
    }
  },
  created() {},
  methods: {
    edit(record) {
      this.model = Object.assign({}, record)
        if(this.model.status=='01') {
        this.statusName='已接入'
      }else {
        this.statusName='未接入'
      }
      this.companyInfo = {}
      getAction(this.url.getCompanyInfo, { id: this.model.companyId }).then((res) => {
        if (res.success) {
          this.companyInfo = res.result
          console.log(this.companyInfo)
        } else {
          this.$message.warning(res.message)
        }
      })
      this.visible = true
    },
    onTimeChange(value, dataString) {
      this.model.simCloseDate = dataString
    },
    open() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      that.$refs.form.validate((valid) => {
        if (valid) {
          httpAction(that.url.edit, that.model, 'put').then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.visible = false
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          })
        }
      })
    },
  },
}
</script>
<style scoped>
.pointList {
  margin-bottom: 20px;
}
</style>
