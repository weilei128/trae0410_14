import { createRouter, createWebHistory } from 'vue-router'
import BooksView from '../views/BooksView.vue'
import BorrowsView from '../views/BorrowsView.vue'
import RecordsView from '../views/RecordsView.vue'

const routes = [
  {
    path: '/',
    redirect: '/books'
  },
  {
    path: '/books',
    name: 'Books',
    component: BooksView
  },
  {
    path: '/borrows',
    name: 'Borrows',
    component: BorrowsView
  },
  {
    path: '/records',
    name: 'Records',
    component: RecordsView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
