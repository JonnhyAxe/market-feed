import Vue from 'vue'
import Router from 'vue-router'
import RichGridExample from '@/rich-grid-example/RichGridExample.vue'
import LiveFeedExample2 from '@/rxVue/LiveFeed2.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/richGrid',
      components: {
          default: RichGridExample
      },
      name: "Rich Grid with Pure JavaScript"
    },
    {
      path: '/',
      components: {
          default: LiveFeedExample2
      },
      name: "Live Feed Example"
    }
  ]
})
