import router from './router'
import store from './store'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css'// Progress 进度条样式
import { Message } from 'element-ui'
import { getToken } from '@/utils/auth' // 验权
// import MenuUtils from '@/utils/MenuUtils'

// const data = store.getters.menuList
// if (data) {
// console.log(data)
// 这里是防止用户手动刷新页面，整个app要重新加载,动态新增的路由，会消失，所以我们重新add一次
// const routes = []
// MenuUtils(routes, data)
// router.addRoutes(routes)
// router.options.routes.push(routes[0])
// }

const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken() && store.getters.menuList) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!store.getters.name) {
        store.dispatch('GetInfo').then(res => { // 拉取用户信息
          next({ path: '/' })
        }).catch(() => {
          store.dispatch('FedLogOut').then(() => {
            Message.error('验证失败,请重新登录')
            next({ path: '/login' })
          })
        })
      } else {
        next()
      }
      // next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})
