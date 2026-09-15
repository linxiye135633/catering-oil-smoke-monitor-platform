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
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail" layout="vertical" >
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
                  format='YYYY-MM-DD HH:mm:ss'
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
import { httpAction } from '@/api/manage'

export default {
  name: 'AddNewPoint',
  props: {
      ID: {
        type: String,
        default: ''
      },
      Name: {
        type: String,
        default: ''
      }
  },
  data() {
    return {
      title: '添加新测点',
      width: 1200,
      visible: false,
      validatorRules: {
        point_mac: [{ required: true, message: '请输入测点MAC!' }],
        sim_code: [{ required: true, message: '请输入SIM卡号!' }],
        port_name: [{ required: true, message: '请输入排口名称!' }],
      },
      url: {
        add: '/point/add',
      },
      model: {},
    }
  },
  methods: {
    onTimeChange(value,dataString) {
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
          that.model.companyId = this.ID
          that.model.enterpriseName = '[' + this.model.pointMac + ']' + this.Name
          httpAction(that.url.add, that.model, 'post').then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              // that.$parent.getClickPoint()
              that.$emit('refresh')
              that.handleCancel()
              //that.$emit('ok') 触发父组件调用查询测点接口
              
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