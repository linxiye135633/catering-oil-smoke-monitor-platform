<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="24" :xl="24" :style="{ marginBottom: '5px' }">
        <a-collapse v-model="activeKey">
          <a-collapse-panel key="1" header="指标解释">
            <p>{{ toolTip.toolTip1 }}</p>
            <p>{{ toolTip.toolTip2 }}</p>
            <p>{{ toolTip.toolTip3 }}</p>
            <p>{{ toolTip.toolTip4 }}</p>
            <p>{{ toolTip.toolTip5 }}</p>
            <p>{{ toolTip.toolTip6 }}</p>
            <p>{{ toolTip.toolTip7 }}</p>
            <p>{{ toolTip.toolTip8 }}</p>
          </a-collapse-panel>
        </a-collapse>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh1.png" alt="" />
        <span class="month-add">{{ cardData.a1 }}/{{ cardData.b1 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh2.png" alt="" />
        <span class="month-add">{{ cardData.a2 }}/{{ cardData.b2 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh3.png" alt="" />
        <span class="month-add">{{ cardData.a3 }}/{{ cardData.b3 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh4.png" alt="" />
        <span class="month-add">{{ cardData.a4 }}/{{ cardData.b4 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh5.png" alt="" />
        <span class="month-add">{{ cardData.a5 }}/{{ cardData.b5 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh6.png" alt="" />
        <span class="month-add">{{ cardData.a6 }}/{{ cardData.b6 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh7.png" alt="" />
        <span class="month-add">{{ cardData.a7 }}/{{ cardData.b7 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
      <a-col class="col-card" :sm="24" :md="12" :xl="6" :style="{ marginBottom: '5px' }">
        <img src="../../assets/adh8.png" alt="" />
        <span class="month-add">{{ cardData.a8 }}/{{ cardData.b8 }}</span>
        <a-checkbox class="a-href" @change="onChange"> </a-checkbox>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :sm="24" :md="24" :xl="24" :style="{ marginBottom: '10px', paddingLeft: '20px' }">
        <div class="title-name">
          <img src="../../assets/adh9.png" alt="" />
          <div>
            <!-- <a class="title-href" slot="extra" href="#"> 更多 > </a> -->
          </div>
        </div>
      </a-col>
    </a-row>
    <a-card>
      <div class="table-page-search-wrapper" style="padding-left: 35px">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-input placeholder="餐饮企业名称" v-model="queryParam.str"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-range-picker format="YYYY-MM-DD HH:mm:ss" allowClear v-model="pieValue" @change="onDateChange">
              </a-range-picker>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <a-checkbox-group @change="onChange">
                  <a-checkbox value="01"> 已标记标签 </a-checkbox>
                  <a-checkbox value="02"> 已解除标签 </a-checkbox>
                </a-checkbox-group>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="7" :md="8" :sm="24">
              <a-form-item>
                <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                </span>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-row class="firm-table" :gutter="24">
        <a-col :sm="24" :md="24" :xl="24">
          <div>
            <a-table
              ref="table"
              size="middle"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource"
              :pagination="false"
              :loading="loading"
              class="j-table-force-nowrap table-content"
              @change="handleTableChange"
              :scroll="{ y: 380 }"
            >
              <template slot="htmlSlot" slot-scope="text">
                <div v-html="text"></div>
              </template>
              <span slot="tags" slot-scope="tags">
                <a-tag color="geekblue">
                  {{ tags }}
                </a-tag>
              </span>
            </a-table>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { httpAction, getAction } from '@/api/manage'
export default {
  name: 'AdhPage',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  data() {
    return {
      columns: [
        {
          title: '企业编码',
          align: 'center',
          dataIndex: 'id',
          scopedSlots: { customRender: 'tags' },
        },
        {
          title: '餐饮企业名称',
          align: 'center',
          dataIndex: 'name',
          width:300
        },
        {
          title: '营业时段停机',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=1) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 1) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '没有联动开启',
          align: 'center',
         dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=2 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 2) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '二次电压过低',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=3 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 3) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '设备联网异常',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=4 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 4) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '烟气排放超标',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=5 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 5) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '高压电场异常',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=6 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 6) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '设备压差异常',
          align: 'center',
          dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=7 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 7) {
              return<div class="red-circle"></div>
            }
          },
        },
        {
          title: '其他原因异常',
          align: 'center',
           dataIndex: 'alarmType',
          customRender: function (t, r, index) {
            if (r.alarmType !=8 ) {
              return <div class="white-circle"></div>
            } else if(r.alarmType == 8) {
              return<div class="red-circle"></div>
            }
          },
        },
      ],
      activeKey: [''],
      pieValue: [],
      endTime: '',
      startTime: '',
      toolTip: {
        toolTip1:
          '营业时段停机标签：当日营业时段停机次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip2:
          '没有联动开启标签：当日净化器和风机没有联动开启次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip3:
          '二次电压过低标签：当日二次电压达不到标准电压次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip4:
          '设备联网异常标签：当日测点未向服务器上报数据次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip5:
          '烟气排放超标标签：当日油烟排放超标次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip6:
          '高压电场异常标签：当日高压电场出现异常情况次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip7:
          '设备压差异常标签：当日净化器进出气压压差达不到标准压差次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
        toolTip8:
          '其他原因异常标签：当日其他原因异常此时次数大于等于指定阈值（默认60次，一次相当于一次数据上报），则标记此标签。',
      },
      cardData:{},
      url: {
         list:'/supervision/getListLabel',
        cardList:'/supervision/getLabel'
      },
    }
  },
  created() {
    this.getCardData()
  },
  computed: {},
  methods: {
    //获取卡片数据
    getCardData() {
      getAction(this.url.cardList).then((res) => {
        this.cardData = res.result
      })
    },
    goMore() {
      this.$router.push({
        // name: 'supervision-modules-List',
      })
    },
    onDateChange(value, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
    },
    onChange(checkedValues) {
      console.log('checked = ', checkedValues)
      console.log('value = ', this.value)
    },
    searchQuery() {},
    handleTableChange() {},
  },
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
/deep/.ant-table-thead tr th {
  background-color: #62a8ed;
  color: #e0edfa;
}
/deep/ .ant-card {
  border-radius: 16px;
}
.col-card {
  position: relative;
  img {
    width: 100%;
  }
  span {
    font-size: 24px;
    font-family: SourceHanSansCN-Medium, SourceHanSansCN;
    font-weight: 500;
    color: #62a8ed;
    line-height: 54px;
  }
  .month-add {
    position: absolute;
    left: 16%;
    bottom: 49%;
    width: 58px;
    height: 36px;
  }
  .a-href {
    position: absolute;
    left: 16%;
    bottom: 20%;
    width: 100px;
    height: 12px;
    font-size: 12px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #62a8ed;
    line-height: 18px;
  }
}
.title-name {
  display: flex;
  position: relative;
  .title-href {
    width: 100px;
    height: 18px;
    font-size: 18px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #666666;
    line-height: 27px;
  }
  a {
    position: absolute;
    right: 2%;
  }
}
.blue-circle {
  width: 15px;
  height: 15px;
  border-radius: 15px;
  background: #62a8ed;
  margin: 0 auto
}
.red-circle {
  width: 15px;
  height: 15px;
  border-radius: 15px;
  background: red;
  margin: 0 auto

}
.white-circle {
  width: 15px;
  height: 15px;
  border-radius: 15px;
  border:1px solid #62a8ed;
  margin: 0 auto
}
</style>
