import Vue from 'vue'
import Router from 'vue-router'
import RichGridExample from '@/rich-grid-example/RichGridExample.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      components: {
          default: RichGridExample
      },
      name: "Rich Grid with Pure JavaScript"
    }
  ]
})
