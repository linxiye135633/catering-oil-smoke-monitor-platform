<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="指标代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            placeholder="如 R-ALARM-COUNT"
            :disabled="disableSubmit"
            v-decorator="['ruleCode', { rules: [{ required: true, message: '请输入指标代码!' }] }]"
          />
        </a-form-item>
        <a-form-item label="指标名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            placeholder="如 超标告警次数扣分"
            v-decorator="['ruleName', { rules: [{ required: true, message: '请输入指标名称!' }] }]"
          />
        </a-form-item>
        <a-form-item label="分值类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['scoreType', { rules: [{ required: true, message: '请选择分值类型!' }] }]">
            <a-select-option value="1">加分</a-select-option>
            <a-select-option value="2">减分</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="单次分值" :labelCol="labelCol" :wrapperCol="wrapperCol" extra="减分项请填负值，如 -2">
          <a-input-number
            :precision="2"
            style="width: 100%"
            v-decorator="['scoreValue', { rules: [{ required: true, message: '请输入分值!' }] }]"
          />
        </a-form-item>
        <a-form-item label="权重" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number :precision="2" :min="0.01" style="width: 100%" v-decorator="['weight', { initialValue: 1 }]" />
        </a-form-item>
        <a-form-item label="评价周期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['cycleType', { rules: [{ required: true, message: '请选择周期!' }] }]">
            <a-select-option value="1">月度</a-select-option>
            <a-select-option value="2">季度</a-select-option>
            <a-select-option value="3">年度</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="取数来源" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['dataSource', { initialValue: '1' }]">
            <a-select-option value="1">平台统计</a-select-option>
            <a-select-option value="2">自查整改(C组)</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="次数上限" :labelCol="labelCol" :wrapperCol="wrapperCol" extra="留空表示不限次数">
          <a-input-number :min="1" style="width: 100%" v-decorator="['maxTimes']" />
        </a-form-item>
        <a-form-item label="规则说明" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea :rows="2" v-decorator="['remark']" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
/**
 * CreditRuleModal 规则新增/编辑弹窗（#E-46/#E-47，沈福临）
 * 沿用1.0 JModal弹窗范式：rules校验+编辑回显（disableSubmit=编辑时指标代码只读）；
 * 提交走 /v2/credit/rules/add|edit，后端25001重复码在此友好提示（TC-E-013）。
 */
import { httpAction } from '@/api/manage'

export default {
  name: 'CreditRuleModal',
  data () {
    return {
      title: '操作',
      visible: false,
      disableSubmit: false,
      confirmLoading: false,
      model: {},
      labelCol: { xs: { span: 24 }, sm: { span: 5 } },
      wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
      url: {
        add: '/v2/credit/rules/add',
        edit: '/v2/credit/rules/edit'
      }
    }
  },
  beforeCreate () {
    this.form = this.$form.createForm(this)
  },
  methods: {
    add () {
      this.disableSubmit = true
      this.edit({})
    },
    edit (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.disableSubmit = this.model.id != null
      this.$nextTick(() => {
        this.form.setFieldsValue(
          Object.assign(
            { weight: 1, dataSource: '1' },
            this.model.id != null ? this.pick(this.model) : {}
          )
        )
      })
    },
    pick (m) {
      const keys = ['ruleCode', 'ruleName', 'scoreType', 'scoreValue', 'weight', 'cycleType', 'dataSource', 'maxTimes', 'remark']
      const out = {}
      keys.forEach(k => { if (m[k] !== undefined && m[k] !== null) out[k] = m[k] })
      return out
    },
    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          const isAdd = this.model.id == null
          if (!isAdd) {
            values.id = this.model.id
            values.ruleCode = this.model.ruleCode // 编辑时代码不可改
          }
          httpAction(isAdd ? this.url.add : this.url.edit, values, isAdd ? 'post' : 'put').then(res => {
            if (res.success) {
              this.$message.success(res.message)
              this.close()
              this.$emit('ok')
            } else {
              const msg = res.code === '25001' ? '指标代码已存在，请更换' : res.message
              this.$message.warning(msg)
            }
          }).finally(() => { this.confirmLoading = false })
        }
      })
    },
    handleCancel () { this.close() },
    close () {
      this.visible = false
      this.confirmLoading = false
    }
  }
}
</script>
