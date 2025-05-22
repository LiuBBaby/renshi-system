import request from '@/utils/request'

// 获取部门树结构
export function getDeptTree() {
  return request({
    url: '/api/dept/tree',
    method: 'get'
  })
}

// 获取部门及其下级部门列表
export function getDeptChildren() {
  return request({
    url: `/api/dept/children`,
    method: 'get'
  })
} 