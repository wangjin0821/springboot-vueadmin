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

export function getSaleStatusList() {
  return request({
    url: 'product/getSaleStatusList',
    method: 'get'
  })
}

export function getCategoryList() {
  return request({
    url: 'product/getCategoryList',
    method: 'get'
  })
}

export function getProductPic(params) {
  return request({
    url: 'product/getProductPic',
    method: 'get',
    params
  })
}

export function saveProductPic(params) {
  return request({
    url: 'product/saveProductPic',
    method: 'post',
    data: params
  })
}

export function syncProductList() {
  return request({
    url: 'product/syncProductList',
    method: 'get'
  })
}

export function parseProductLocalImage() {
  return request({
    url: 'product/parseProductLocalImage',
    method: 'get'
  })
}
