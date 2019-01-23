import axios from 'axios'
import store from '../store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // api的base_url
  method: 'get',
  headers: {
    'Content-Type': 'application/json'
  },
  responseType: 'arraybuffer',
  timeout: 600 * 1000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(config => {
  if (store.getters.token) {
    config.headers['X-Token'] = getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  return config
}, error => {
  console.log(error) // for debug
  Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
  response => {
    console.log(response)
    const headers = response.headers
    console.log(headers)
    if (headers['ajax-download'] !== 'undefined' && headers['ajax-download'] === 'true') {
      const blob = new Blob([response.data], {
        type: headers['content-type']
      })
      const fileName = headers['ajax-download-file']
      console.log(fileName)
      if (fileName === 'undefined') {
        return Promise.reject('文件下载异常,下载文件名获取不到')
      }
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = fileName
      link.click()
      return Promise.resolve()
    }
    // 转化成json对象
    const enc = new TextDecoder('utf-8')
    const res = JSON.parse(enc.decode(new Uint8Array(response.data)))
    return Promise.reject(res.message)
  },
  error => {
    console.log('err' + error)// for debug
    return Promise.reject(error)
  }
)

export default service
