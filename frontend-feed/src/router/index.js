import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import PerfectMatch from '@/components/PerfectMatch'
import RichGridExample from '@/rich-grid-example/RichGridExample.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/hello',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/tmp',
      name: 'PerfectMatch',
      component: PerfectMatch
    },
    {
      path: '/',
      components: {
          default: RichGridExample
      },
      name: "Rich Grid with Pure JavaScript"
    },
    {
      path: '/rx',
      name: 'rxPerfectMatch',
      component: PerfectMatch
    }
  ]
})
