<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-card title="修改密码">
          <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-row :gutter="24">
              <a-col :span="8">
                <a-form-model-item label="原始密码:">
                  <a-input v-model="form.name" placeholder="请输入原始密码" />
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="新密码:">
                  <a-input v-model="form.name" placeholder="请输入新密码" />
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="确认密码:">
                  <a-input v-model="form.name" placeholder="请确认密码" />
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
              <a-button type="primary" @click="onSubmit"> 保存 </a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage'
import { setTimeout } from 'timers'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  name: 'ChangePassWord',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  props: {},
  data() {
    return {
      visible: false,
      url: {
        list: '/Institution/add',
      },
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
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
    }
  },
  computed: {},
  methods: {
     onSubmit() {
      console.log('submit!', this.form);
    },
  },
  created() {},
}
</script>
<style lang="less" scoped>
// /deep/ .ant-card {
//   border-radius: 10px;
// }
.col-card {
  padding: 20px;
  .col-name {
    color: #333;
    font-size: 14px;
    font-weight: 700;
    margin-right: 10px;
  }
  .col-value {
    color: #7f7f7f;
    font-weight: 400;
    font-size: 14px;
  }
}
</style>