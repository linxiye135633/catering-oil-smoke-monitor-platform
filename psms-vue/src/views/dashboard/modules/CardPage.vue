<template>
  <div>
    <div class="card-style" hoverable>
      <slot name="cardImg"></slot>
      <div class="card-text">
        <slot name="cardName"></slot>
        <slot name="cardText"></slot>
        <a v-if="moreShow" class="text-more">更多 <a-icon type="right-circle" /></a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CardPage',
   props: {
      moreShow: {
        type: Boolean,
        default: true
      },
     
    },
  components: {},
  data() {
    return {}
  },
  created() {},
  computed: {},
  methods: {}
}
</script>
<style lang="less" scoped>
span {
    font-size: 36px;
    font-family: SourceHanSansCN-Medium, SourceHanSansCN;
    font-weight: 500;
    color: #62a8ed;
    line-height: 54px;
  }
  .month-add {
    position: absolute;
    left: 140px;
    top: 67px;
    width: 58px;
    height: 36px;
  }
  .total-number {
    position: absolute;
    left: 140px;
    top: 100px;
    width: 77px;
    height: 36px;
  }
  .a-href {
    position: absolute;
    left: 64px;
    bottom: 35px;
    width: 100px;
    height: 12px;
    font-size: 12px;
    font-family: SourceHanSansCN-Normal, SourceHanSansCN;
    font-weight: 400;
    color: #62a8ed;
    line-height: 18px;
  }
  .card-question {
    position: absolute;
    left: 260px;
    top: 36px;
    color: #62a8ed;
  }
</style>
