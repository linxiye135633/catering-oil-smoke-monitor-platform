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
  name: 'CardMoudle',
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
.card-style {
  display: flex;
  padding: 0;
  width: 400px;
  height: 90px;
  img {
    flex: 1 1 90px;
  }
  .card-text {
    display: flex;
    flex-direction: column;
    flex: 1 1 310px;
    background: #ffffff;
    font-size: 14px;
    padding: 4px 6px 4px 8px;
    box-sizing: border-box;
    .text-strong {
      font-weight: 700;
    }
    .text-more {  
      color: #72afd2;
      margin-left: 80%
    }
  }
}
</style>
