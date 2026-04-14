import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export const bookApi = {
  list: (keyword) => {
    return request.get('/books', { params: { keyword } })
  },
  getById: (id) => {
    return request.get(`/books/${id}`)
  },
  add: (data) => {
    return request.post('/books', data)
  },
  update: (id, data) => {
    return request.put(`/books/${id}`, data)
  },
  delete: (id) => {
    return request.delete(`/books/${id}`)
  }
}

export const borrowApi = {
  list: (keyword, status) => {
    return request.get('/borrows', { params: { keyword, status } })
  },
  getById: (id) => {
    return request.get(`/borrows/${id}`)
  },
  getOverdue: () => {
    return request.get('/borrows/overdue')
  },
  borrow: (data) => {
    return request.post('/borrows/borrow', data)
  },
  returnBook: (id) => {
    return request.post(`/borrows/return/${id}`)
  }
}
