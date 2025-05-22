import request from '@/utils/request'

// 查询招聘需求列表
export function listRecruitment(query) {
  return request({
    url: '/recruitment/recruitment/list',
    method: 'get',
    params: query
  })
}

// 查询招聘需求详细
export function getRecruitment(id) {
  return request({
    url: '/recruitment/recruitment/' + id,
    method: 'get'
  })
}

// 新增招聘需求
export function addRecruitment(data) {
  return request({
    url: '/recruitment/recruitment',
    method: 'post',
    data: data
  })
}

// 修改招聘需求
export function updateRecruitment(data) {
  return request({
    url: '/recruitment/recruitment',
    method: 'put',
    data: data
  })
}

// 删除招聘需求
export function delRecruitment(id) {
  return request({
    url: '/recruitment/recruitment/' + id,
    method: 'delete'
  })
}
