<template>
  <div>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ padding: '30px' }">
        <a-modal :visible="visible" @ok="handleOk" @cancel="handleCancel" width="80%">
          <a-row :gutter="24">
            <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-collapse v-model="activeKey">
                <a-collapse-panel key="1" header="报备申诉">
                  <p>{{ toolTip }}</p>
                </a-collapse-panel>
              </a-collapse>
            </a-col>
            <!-- <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-steps :current="current" size="small" @change="stepChange">
                <a-step title="录入基本信息" />
                <a-step title="上传附件" />
              </a-steps>
            </a-col> -->
            <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
              <a-card v-if="current == 0" title="基本信息" style="width: 100%">
                <a-form-model layout="vertical">
                  <div>
                    <a-row :gutter="24">
                      <a-col :span="8">
                        <a-form-model-item label="标题:（必填）:">
                          <a-input v-model="model.title" placeholder="请输入标题" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="8">
                        <a-form-model-item label="维护时段:（必选）">
                          <a-range-picker
                            format="YYYY-MM-DD HH:mm:ss"
                            allowClear
                            v-model="pieValue"
                            @change="onDateChange"
                            style="width: 100%"
                          >
                          </a-range-picker>
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="8">
                        <a-form-model-item label="联系人员:（必填）:">
                          <a-input v-model="model.linkman" placeholder="请输入餐饮排放单位联系人" />
                        </a-form-model-item>
                      </a-col>
                    </a-row>
                    <a-row :gutter="24">
                      <a-col :span="8">
                        <a-form-model-item label="反馈类型:（必选）:">
                          <a-select v-model="model.auditType" placeholder="请选择反馈类型">
                            <a-select-option value="2"> 报备 </a-select-option>
                            <a-select-option value="1"> 申诉 </a-select-option>
                          </a-select>
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="8">
                        <a-form-model-item label="联系地址:（必填）">
                          <a-input v-model="model.address" placeholder="请输入餐饮排放单位地址" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="8">
                        <a-form-model-item label="联系电话:（必填）:">
                          <a-input v-model="model.name" placeholder="请输入餐饮排放单位联系人电话" />
                        </a-form-model-item>
                      </a-col>
                    </a-row>
                    <a-row :gutter="24">
                      <a-col :span="24">
                        <a-form-model-item label="反馈描述:（必填）">
                          <a-textarea placeholder="请输入反馈描述信息" v-model="model.auditText" />
                        </a-form-model-item>
                      </a-col>
                    </a-row>
                  </div>
                </a-form-model>
              </a-card>
              <!-- <a-card v-if="current == 1" title="上传运维附件" style="width: 100%">
                <a-form-model layout="vertical">
                  <div>
                    <a-row :gutter="24">
                      <a-col :span="8">
                        <a-upload
                          name="file"
                          :showUploadList="false"
                          :multiple="false"
                          :headers="tokenHeader"
                          :action="importExcelUrl"
                          @change="handleImportExcel"
                        >
                          <a-button type="primary" icon="import">导入</a-button>
                        </a-upload>
                      </a-col>
                    </a-row>
                  </div>
                </a-form-model>
              </a-card> -->
            </a-col>
          </a-row>
        </a-modal>
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
  name: 'AppealPage',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  props: {},
  data() {
    return {
      visible: false,
      activeKey: ['1'],
      pieValue: [],
      endTime: '',
      startTime: '',
      // toolTip: ' 说明：第一步：录入基本信息，第二步：上传运维附件',
      toolTip: ' 说明：录入基本信息',
      current: 0,
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
      model: {
        title: '', //机构名称
        linkman: '', //联系人
        auditType: '', //反馈类型
        auditText: '', //反馈描述
        address: '', //联系地址
        telephone: '', //联系电话
        companyId: ''
      },
      url: {
        list: '/supervision/alarmAdd'
        // importExcelUrl: 'sys/checkRule/importExcel',
      }
    }
  },
  computed: {
    // importExcelUrl: function () {
    //   return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    // }
  },
  methods: {
    handleOk(e) {
      //保存
      let params = Object.assign(this.model)
      postAction(this.url.list, params)
        .then(res => {
          this.$message.success('提交成功')
        })
        .catch(err => {
          err
        })
      this.visible = false
    },
    open() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    stepChange(current) {
      this.current = current
    }
  },
  created() {}
}
</script>
<style lang="less" scoped>
// /deep/ .ant-card {
//   border-radius: 10px;
// }
.col-card {
  padding: 20px;
}
</style>
