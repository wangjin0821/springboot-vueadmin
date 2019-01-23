import request from '@/utils/request'

export function getMenuList() {
  return request({
    url: '/menu/list',
    method: 'get'
  })
}

export function deleteMenu(params) {
  return request({
    url: '/menu/delete',
    method: 'post',
    data: params
  })
}

export function saveMenu(params) {
  return request({
    url: '/menu/save',
    method: 'post',
    data: params
  })
}

export function updateMenu(params) {
  return request({
    url: '/menu/update',
    method: 'post',
    data: params
  })
}

export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'post',
    data: params
  })
}

export function deleteUser(params) {
  return request({
    url: '/user/delete',
    method: 'post',
    data: params
  })
}

export function saveUser(params) {
  return request({
    url: '/user/save',
    method: 'post',
    data: params
  })
}

export function updateUserRole(params) {
  return request({
    url: '/userRoles/save',
    method: 'post',
    data: params
  })
}

export function getUserRoles(params) {
  return request({
    url: '/userRoles/list',
    method: 'post',
    params
  })
}

export function getRoleList() {
  return request({
    url: '/role/tree',
    method: 'get'
  })
}

export function saveRole(params) {
  return request({
    url: 'role/save',
    method: 'post',
    data: params
  })
}

export function deleteRoles(params) {
  return request({
    url: 'role/delete',
    method: 'post',
    data: params
  })
}

export function saveRolePermission(params) {
  return request({
    url: '/rolePermission/save',
    method: 'post',
    data: params
  })
}

export function getListByRoleId(params) {
  return request({
    url: '/rolePermission/getListByRoleId',
    method: 'post',
    params
  })
}
