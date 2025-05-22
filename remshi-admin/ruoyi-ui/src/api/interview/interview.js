import request from '@/utils/request'

// 查询面试计划列表
export function listInterview(query) {
  return request({
    url: '/interview/interview/list',
    method: 'get',
    params: query
  })
}

// 查询面试计划详细
export function getInterview(id) {
  return request({
    url: '/interview/interview/' + id,
    method: 'get'
  })
}

// 新增面试计划
export function addInterview(data) {
  return request({
    url: '/interview/interview',
    method: 'post',
    data: data
  })
}

// 修改面试计划
export function updateInterview(data) {
  return request({
    url: '/interview/interview',
    method: 'put',
    data: data
  })
}

// 删除面试计划
export function delInterview(id) {
  return request({
    url: '/interview/interview/' + id,
    method: 'delete'
  })
}
