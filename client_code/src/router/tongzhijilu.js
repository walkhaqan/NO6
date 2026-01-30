import { Main } from '@/views'

const routes = {
  path: '/index',
  name: 'index',
  component: Main,
  meta: { title: '首页', icon: 'el-icon-s-home' },
  children: [
    {
      path: '/index/tongzhijiluList',
      name: '通知记录管理',
      component: () => import('@/views/pages/tongzhijilu/list'),
      meta: { title: '通知记录管理', icon: 'el-icon-message' }
    },
    {
      path: '/index/tongzhijiluDetail/:id',
      name: '通知记录详情',
      component: () => import('@/views/pages/tongzhijilu/formModel'),
      meta: { title: '通知记录详情', icon: 'el-icon-document' },
      hidden: true
    }
  ]
}

export default routes