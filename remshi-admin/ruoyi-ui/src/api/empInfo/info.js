import request from '@/utils/request'

// 查询员工信息列表
export function listInfo(query) {
  return request({
    url: '/empInfo/info/list',
    method: 'get',
    params: query
  })
}

// 查询员工信息详细
export function getInfo(empInfoId) {
  return request({
    url: '/empInfo/info/' + empInfoId,
    method: 'get'
  })
}

// 新增员工信息
export function addInfo(data) {
  return request({
    url: '/empInfo/info',
    method: 'post',
    data: data
  })
}

// 修改员工信息
export function updateInfo(data) {
  return request({
    url: '/empInfo/info',
    method: 'put',
    data: data
  })
}

// 删除员工信息
export function delInfo(empInfoId) {
  return request({
    url: '/empInfo/info/' + empInfoId,
    method: 'delete'
  })
}

// 查询岗位列表
export function listPosts() {
  return request({
    url: '/empInfo/posts',
    method: 'get'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/empInfo/info/deptTree',
    method: 'get'
  })
}
