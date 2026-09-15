<template>
  <div class="map-box">
    <baidu-map
      ak="jpdX5xQ4hioihYepstKk1IDBRb2HNGL1"
      class="map"
      :zoom="15"
      :scroll-wheel-zoom="true"
      :center="getCenter"
    >
      <bm-marker
        v-for="(item, index) in propData"
        :key="index"
        :position="{ lng: item.lng, lat: item.lat }"
        :dragging="true"
      >
      </bm-marker>
      <!-- <bm-marker :position="marker" :dragging="true"> </bm-marker> -->
    </baidu-map>
    <map-list @dataSourse="dataSourse" @cardOpen="cardOpen" @listData="listData" class="map-list"></map-list>
    <map-card v-if="cardShow" :pointMac="pointMac" @cardOpen="cardOpen" class="map-card"></map-card>
  </div>
</template>

<script>
import MapList from './MapList'
import MapCard from './MapCard'
import { setTimeout } from 'timers'
import BaiduMap from 'vue-baidu-map/components/map/Map.vue'
import BmMarker from 'vue-baidu-map/components/overlays/Marker.vue'
import store from '@/store/'
export default {
  name: 'MapModule',
  components: { MapList, MapCard,BaiduMap,BmMarker },
  data() {
    return {
      cardShow: false,
      listDatas: {},
      pointMac: '',
      marker: { lat: null, lng: null },
      propData: [],
    }
  },
  created() {},
  computed: {
    getCenter() {
      if(this.listDatas.lng) {
        return {lng:this.listDatas.lng,lat:this.listDatas.lat}
      }else {
        return `${store.getters.userInfo.realname}`
      }
    }
  },
  methods: {
    cardOpen(val) {
      this.cardShow = val
    },
    //点击行获取详细信息
    listData(val) {
      setTimeout(() => {
        this.listDatas = val
        this.pointMac = val.point_mac
        this.marker.lng = val.lng
        this.marker.lat = val.lat
      }, 200)
    },
    dataSourse(val) {
      this.propData = val
    },
  },
}
</script>
<style lang="less" scoped>
.map-box {
  width: 100%;
  position: relative;
  .map {
    position: absolute;
    // top: 0;
    width: 100%;
    height: 590px;
  }
  .map-list {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 1;
  }
  .map-card {
    position: absolute;
    right: 0;
    z-index: 1;
  }
}
</style>
