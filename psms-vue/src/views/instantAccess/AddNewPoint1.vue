<template>
  <j-modal :title="title" :width="width" :visible="visible" switchFullscreen @cancel="handleCancel" cancelText="关闭">
    <a-form-model layout="vertical">
      <div class="pointList">
        <h3>基本信息</h3>
        <a-row :gutter="16">
          <a-col :span="3">
            <h4>企业编码</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="4">
            <h4>企业名称</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="3">
            <h4>联系人</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="4">
            <h4>联系人手机号</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="4">
            <h4>所在区域</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="4">
            <h4>企业地址</h4>
            <p>带入信息</p>
          </a-col>
          <a-col :span="2">
            <h4>状态</h4>
            <p>带入信息</p>
          </a-col>
        </a-row>
      </div>
      <div class="pointList">
        <h3>测点信息</h3>
        <a-row :gutter="8">
          <a-col :span="6">
            <a-form-model-item label="测点类型:">
              <a-select v-model="model.name" placeholder="请选择测点类型">
                <a-select-option value="1" selected> 油烟监测 </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="测点MAC:">
              <a-input v-model="model.name" placeholder="请输入测点MAC" />
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="标准灶头数（个）:">
              <a-input v-model="model.name" placeholder="请输入标准灶头数（个）" />
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="SIM卡号:">
              <a-input v-model="model.name" placeholder="请输入SIM卡号" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="8">
          <a-col :span="6">
            <a-form-model-item label="排口名称:">
              <a-input v-model="model.name" placeholder="请输入排口名称" />
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="SIM卡截止日期:">
              <j-date placeholder="请选择SIM卡截止日期" v-model="model.name" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </div>
      <div class="pointList">
        <h3>设备信息</h3>
        <a-row :gutter="8">
          <a-col :span="6">
            <a-form-model-item label="净化器技术路线:">
              <a-select v-model="model.name" placeholder="请选择净化器技术路线">
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
              <a-input v-model="model.name" placeholder="请输入排风机设计风量（立方米/秒）" />
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="电场模块数量:">
              <a-input v-model="model.name" placeholder="请输入电场模块数量:" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </div>
    </a-form-model>
  </j-modal>
</template>

<script>
export default {
  name: 'AddNewPoint',
  data() {
    return {
      title: '添加新测点',
      width: 1200,
      visible: false,
      model: {
        name: '',
      },
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
.pointList {
  margin-bottom: 20px;
}
</style>