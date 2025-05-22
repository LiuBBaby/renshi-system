import request from '@/utils/request'

// 查询出差申请列表
export function listBusinessTrip(query) {
  return request({
    url: '/trip/business/list',
    method: 'get',
    params: query
  })
}

// 查询出差申请详细
export function getBusinessTrip(id) {
  return request({
    url: '/trip/business/' + id,
    method: 'get'
  })
}

// 新增出差申请
export function addBusinessTrip(data) {
  return request({
    url: '/trip/business',
    method: 'post',
    data: data
  })
}

// 修改出差申请
export function updateBusinessTrip(data) {
  return request({
    url: '/trip/business',
    method: 'put',
    data: data
  })
}

// 删除出差申请
export function delBusinessTrip(id) {
  return request({
    url: '/trip/business/' + id,
    method: 'delete'
  })
}

// 导出出差申请
export function exportBusinessTrip(query) {
  return request({
    url: '/trip/business/export',
    method: 'get',
    params: query
  })
}
