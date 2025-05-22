import request from '@/utils/request'

// 获取请假记录列表
export function getLeaveList(params) {
  return request({
    url: '/api/leave/list',
    method: 'get',
    params: params
  })
}

// 提交请假申请
export function applyLeave(data) {
  return request({
    url: '/api/leave/apply',
    method: 'post',
    data: data
  })
}

// 获取请假详情
export function getLeaveDetail(params) {
  return request({
    url: '/api/leave/detail',
    method: 'get',
    params: params
  })
}

// 审批请假
export function approveLeave(data) {
  return request({
    url: '/api/leave/approve',
    method: 'post',
    data: data
  })
}

// 撤销请假申请
export function cancelLeave(data) {
  return request({
    url: '/api/leave/cancel',
    method: 'post',
    data: data
  })
}