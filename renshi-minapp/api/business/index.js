import request from '@/utils/request'

// 获取出差申请列表
export function getBusinessList() {
  return request({
    url: '/trip/business/myList',
    method: 'get'
  })
}

// 新增出差申请
export function addBusiness(data) {
  return request({
    url: '/trip/business',
    method: 'post',
    data: data
  })
}

// 获取出差申请详情
export function getBusinessDetail(id) {
  return request({
    url: '/trip/business/' + id,
    method: 'get'
  })
} 