<template>
  <div class="map-list">
    <div class="box-header">
      <div class="header-search">
        <a-input v-model="realname" style="margin-right: 10px" placeholder="请输入单位/门头/测点MAC" />
        <a-button icon="search" @click="loadData" />
        <a-button class="show" icon="fast-forward" @click="listShowClick" />
      </div>
    </div>
    <div v-if="listShow">
      <div>
        <a-table
          :pagination="ipagination"
          :showHeader="false"
          :columns="columns"
          :dataSource="dataSource"
          bordered
          :customRow="rowClick"
          @change="handleTableChange"
          rowKey="id"
        >
          <template slot="name" slot-scope="text">
            <a><a-icon type="shop" style="float:right" />{{ text }}</a>
          </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getAction, postAction } from '@/api/manage'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'MapList',
  components: {},
  mixins: [JeecgListMixin],
  data() {
    return {
      listShow: true,
      url: {
        list: '/company/newlist'
      },
      columns: [
        {
          title: 'Name',
          dataIndex: 'name',
          scopedSlots: { customRender: 'name' },
          customRender: function(t, r, index) {
            return (
              <span style="color:#62a8ed">
                <a-icon type="shop" style="margin-right: 12px" />
                {r.point_mac}-{r.name}
              </span>
            )
          }
        }
      ],
      dataSource: [],
      realname: ''
    }
  },
  // created() {
  //   this.loadData()
  // },
  computed: {},
  methods: {
    rowClick(record, index) {
      return {
        on: {
          click: event => {
            this.$emit('cardOpen', true), this.$emit('listData', record), this.$emit('dataSourse', this.dataSource)
          }
        }
      }
    },
    //打开关闭查询列表
    listShowClick() {
      this.listShow = !this.listShow
    },
    loadData() {
      // this.ipagination.showQuickJumper=false
      // this.ipagination.showSizeChanger=false
      this.ipagination.simple = true
      //  this.ipagination.showTotal= (total, range) => {
      //     return  " 共" + total + "条"
      //   }
      var queryParams = this.getQueryParams()
      getAction(this.url.list, Object.assign(queryParams, { str: this.realname, isPage: 'true' })).then(res => {
        this.dataSource = res.result.records
        this.ipagination.total = res.result.total
      })
    }
  }
}
</script>
<style lang="less" scoped>
@media screen and (max-width: 400px) {
  .map-list {
    width:80%;
    background-color: #ffffff;
    .box-header {
      color: #444;
      display: block;
      padding: 10px;
      position: relative;
      // border-bottom: 1px solid #f4f4f4;
      .header-search {
        display: flex;
      }
    }
  }
}
@media screen and (min-width: 400px) {
  .map-list {
    background-color: #ffffff;
    .box-header {
      color: #444;
      display: block;
      padding: 10px;
      position: relative;
      // border-bottom: 1px solid #f4f4f4;
      .header-search {
        display: flex;
      }
    }
  }
}
// .map-list {
//   background-color: #ffffff;
//   .box-header {
//     color: #444;
//     display: block;
//     padding: 10px;
//     position: relative;
//     // border-bottom: 1px solid #f4f4f4;
//     .header-search {
//       display: flex;
//     }
//   }
// }
.show {
  margin-left: 5px;
}
& /deep/ .ant-table-thead > tr > th,
.ant-table-tbody > tr > td {
  padding: 8px 8px;
  overflow-wrap: break-word;
}
& /deep/ .ant-table-tbody .ant-table-row td {
  padding-top: 8px;
  padding-bottom: 8px;
}
</style>
