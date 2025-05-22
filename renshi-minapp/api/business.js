import request from '@/utils/request'

// 获取出差记录列表
export function getBusinessList(params) {
  return request({
    url: '/api/business/list',
    method: 'get',
    params: params
  })
}

// 提交出差申请
export function applyBusiness(data) {
  return request({
    url: '/api/business/apply',
    method: 'post',
    data: data
  })
}

// 获取出差详情
export function getBusinessDetail(params) {
  return request({
    url: '/api/business/detail',
    method: 'get',
    params: params
  })
}

// 审批出差申请
export function approveBusiness(data) {
  return request({
    url: '/api/business/approve',
    method: 'post',
    data: data
  })
}

// 撤销出差申请
export function cancelBusiness(data) {
  return request({
    url: '/api/business/cancel',
    method: 'post',
    data: data
  })
}