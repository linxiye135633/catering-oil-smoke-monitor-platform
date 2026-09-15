<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="报表任务名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入报表任务名称"></a-input>
        </a-form-item>
        <a-form-item label="报表任务参数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'msg', validatorRules.msg]" placeholder="请输入报表任务参数"></a-input>
        </a-form-item>
        <a-form-item label="报表任务状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'state', validatorRules.state]" placeholder="请输入报表任务状态"></a-input>
        </a-form-item>
        <a-form-item label="报表输出文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'fileName', validatorRules.fileName]" placeholder="请输入报表输出文件"></a-input>
        </a-form-item>
        <a-form-item label="文件地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'fileUrl', validatorRules.fileUrl]" placeholder="请输入文件地址"></a-input>
        </a-form-item>
        <a-form-item label="账号关联" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'fidRegion', validatorRules.fidRegion]" placeholder="请输入账号关联" style="width: 100%"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: "BaseStatisticalModal",
    components: { 
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
          name: {rules: [
          ]},
          msg: {rules: [
          ]},
          state: {rules: [
          ]},
          fileName: {rules: [
          ]},
          fileUrl: {rules: [
          ]},
          fidRegion: {rules: [
          ]},
        },
        url: {
          add: "/basestatistical/baseStatistical/add",
          edit: "/basestatistical/baseStatistical/edit",
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'name','msg','state','fileName','fileUrl','fidRegion'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'name','msg','state','fileName','fileUrl','fidRegion'))
      },

      
    }
  }
</script>