<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-modal title="机构人员" :visible="visible" @ok="handleOk" @cancel="handleCancel" width="50%">
          <a-row :gutter="24">
            <a-col :sm="24" :md="24" :xl="24">
              <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="姓名(必填)" prop="name">
                  <a-input v-model="form.name" placeholder="请输入姓名" />
                </a-form-model-item>
                <a-form-model-item label="性别(必填)" prop="sex">
                  <a-input v-model="form.sex" placeholder="请输入性别" />
                </a-form-model-item>
                <a-form-model-item label="手机号(必填)" prop="mobile">
                  <a-input v-model="form.mobile" placeholder="请填写11位手机号码" />
                </a-form-model-item>
                <a-form-model-item label="平台账号">
                  <a-input v-model="form.account" placeholder="请输入平台账号" />
                </a-form-model-item>
                <a-form-model-item label="部门">
                  <a-input v-model="form.department" placeholder="请输入部门" />
                </a-form-model-item>
                <a-form-model-item label="职位">
                  <a-input v-model="form.position" placeholder="请填写职位" />
                </a-form-model-item>
                <a-form-model-item label="所属组织">
                  <a-input v-model="form.orgCode" placeholder="请填写所属组织" />
                </a-form-model-item>
                <a-form-model-item label="所属行政机构">
                  <a-input disabled v-model="govName" placeholder="请输入部门" />
                </a-form-model-item>
                <a-form-model-item label="状态">
                  <a-radio-group v-model="form.status" :options="options" @change="onChange1" />
                </a-form-model-item>
              </a-form-model>
            </a-col>
          </a-row>
        </a-modal>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import { getAction, postAction, putAction } from '@/api/manage'
import { setTimeout } from 'timers'
export default {
  name: 'AddPerPage',
  components: {},
  props: {
    code: {
      type: String,
      default: '',
      required: false,
    },
    govName: {
      type: String,
      default: '',
      required: false,
    },
  },
  data() {
    return {
      visible: false,
      url: {
        addPerson: '/InstitutionPerson/add',
        editPerson: '/InstitutionPerson/edit',
      },
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
      options: [
        { label: '正常', value: '01' },
        { label: '锁定', value: '02' },
      ],
      form: {
        name: '', //姓名
        sex: '', //性别
        account: '', //平台账号
        department: '', //登陆密码
        mobile: '', //手机号
        position: '',
        orgCode: '',
        status: '01',
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        sex: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        account: [{ required: true, message: '请输入平台账号', trigger: 'blur' }],
        mobile: [{ required: true, message: '请输入手机号', trigger: 'blur', pattern: /^1[3|4|5|7|8][0-9]\d{8}$/ }],
      },
      isEdit: false,
    }
  },
  computed: {},
  methods: {
    handleOk(e) {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.saveForm()
          this.form = {}
          this.handleCancel()
          if (this.isEdit == false) {
            setTimeout(() => {
              this.$parent.getPerSonData(this.code)
            }, 20)
          }else {
            setTimeout(() => {
              this.$parent.loadData()
            }, 20)
          }
        } else {
          return false
        }
      })
    },
    open() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
    onChange1(e) {
      this.form.status = e.target.value
    },
    //保存
    saveForm() {
      if (this.isEdit == false) {
        postAction(this.url.addPerson, Object.assign(this.form, { code: this.code, institutionCode: this.govName }))
          .then((res) => {
            if (res.success == true) {
              this.$message.success('保存成功')
            }
          })
          .catch((err) => {
            err
          })
      } else {
        putAction(this.url.editPerson, this.form)
          .then((res) => {
            if (res.success == true) {
              this.$message.success('保存成功')
            }
          })
          .catch((err) => {
            err
          })
      }
    },
  },
  created() {},
}
</script>
<style lang="less" scoped>
/deep/ .ant-card {
  border-radius: 10px;
}
</style>
