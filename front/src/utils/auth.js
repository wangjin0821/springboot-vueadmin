import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const MenuListKey = 'menuList'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getMenuList() {
  return JSON.parse(window.sessionStorage.getItem(MenuListKey))
}

export function setMenuList(menuList) {
  return window.sessionStorage.setItem(MenuListKey, JSON.stringify(menuList))
}
