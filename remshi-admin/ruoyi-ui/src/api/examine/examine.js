import request from '@/utils/request'

// 查询请假审批列表
export function listExamine(query) {
  return request({
    url: '/examine/examine/list',
    method: 'get',
    params: query
  })
}

// 查询请假审批详细
export function getExamine(id) {
  return request({
    url: '/examine/examine/' + id,
    method: 'get'
  })
}

// 新增请假审批
export function addExamine(data) {
  return request({
    url: '/examine/examine',
    method: 'post',
    data: data
  })
}

// 修改请假审批
export function updateExamine(data) {
  return request({
    url: '/examine/examine',
    method: 'put',
    data: data
  })
}

// 删除请假审批
export function delExamine(id) {
  return request({
    url: '/examine/examine/' + id,
    method: 'delete'
  })
}
