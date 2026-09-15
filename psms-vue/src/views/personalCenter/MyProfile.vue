<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-card title="我的登录账号">
          <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-row :gutter="24">
              <a-col :xl="8" :lg="8" :md="8" :sm="24">
                <a-form-model-item label="账户姓名:">
                  <!-- <a-input v-model="form.realname"  :disabled="true" placeholder="请输入登陆账号" /> -->
                  {{ form.realname }}
                </a-form-model-item>
              </a-col>
              <a-col  :xl="8" :lg="8" :md="8" :sm="24">
                <a-form-model-item label="账号编码:">
                  <!-- <a-input v-model="form.fidRegion" placeholder="请输入系统角色" /> -->
                  {{ form.fidRegion }}
                </a-form-model-item>
              </a-col>
              <a-col  :xl="8" :lg="8" :md="8" :sm="24">
                <a-form-model-item label="创建时间:">
                  <!-- <a-input v-model="form.createTime" placeholder="请输入我的名字" /> -->
                  {{ form.createTime }}
                </a-form-model-item>
              </a-col>
              <a-col :xl="8" :lg="8" :md="8" :sm="24">
                <a-form-model-item label="手机号:">
                  <!-- <a-input v-model="form.createTime" placeholder="请输入我的名字" /> -->
                  {{ form.phone }}
                </a-form-model-item>
              </a-col>
              <a-col  :xl="8" :lg="8" :md="8" :sm="24">
                <a-form-model-item label="创建账号:">
                  <!-- <a-input v-model="form.createTime" placeholder="请输入我的名字" /> -->
                  {{ form.username }}
                </a-form-model-item>
              </a-col>
            </a-row>
            <!-- <a-row :gutter="24">
              <a-col :span="8">
                <a-form-model-item label="绑定手机:">
                  <a-input v-model="form.name" placeholder="请输入绑定手机" />
                </a-form-model-item>
              </a-col>
            </a-row> -->
            <!-- <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
              <a-button type="primary" @click="onSubmit"> 保存 </a-button>
            </a-form-model-item> -->
          </a-form-model>
        </a-card>
        <!-- <a-card title="所属单位信息">
          <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-row :gutter="24">
              <a-col :span="8">
                <a-form-model-item label="单位名称:">
                  <a-input v-model="form.name" placeholder="请输入单位名称" />
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="单位英文简称:">
                  <a-input v-model="form.name" placeholder="请输入单位英文简称" />
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="单位中文简称:">
                  <a-input v-model="form.name" placeholder="请输入单位中文简称" />
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
              <a-button type="primary" @click="onSubmit"> 保存 </a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card> -->
      </a-col>
    </a-row>
  </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage'
import { setTimeout } from 'timers'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import store from '@/store/'
export default {
  name: 'MyProfile',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  props: {},
  data() {
    return {
      visible: false,
      url: {
        list: '/sys/user/queryById'
      },
      labelCol: { span: 8 },
      wrapperCol: { span: 14 },
      form: {
        realname: '',
        fidRegion: '',
        createTime: '',
        phone: '',
        username: ''
      }
    }
  },
  computed: {},
  methods: {
    getData() {
      getAction(this.url.list, { id: store.getters.userInfo.id })
        .then(res => {
          this.form = res.result
        })
        .catch(err => {
          err
        })
    }
  },
  created() {
    this.getData()
  }
}
</script>
<style lang="less" scoped>

</style>
