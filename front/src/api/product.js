import request from '@/utils/request'
import download from '@/utils/download'

export function getList(params) {
  return request({
    url: '/product/list',
    method: 'post',
    data: params
  })
}

export function deleted(params) {
  return request({
    url: '/product/delete',
    method: 'post',
    params
  })
}

export function update(params) {
  return request({
    url: 'product/update',
    method: 'post',
    data: params
  })
}

export function add(params) {
  return request({
    url: 'product/add',
    method: 'post',
    data: params
  })
}

export function downLoadMix(params) {
  return download({
    url: 'product/export',
    params
  })
}
