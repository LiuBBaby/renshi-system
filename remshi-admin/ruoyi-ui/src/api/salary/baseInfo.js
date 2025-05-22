import request from '@/utils/request'

// 查询基本工资信息列表
export function listSalaryBaseInfo(query) {
  return request({
    url: '/salary/base/list',
    method: 'get',
    params: query
  })
}

// 查询基本工资信息详细
export function getSalaryBaseInfo(id) {
  if (id === undefined || id === null || id === '') {
    // 如果没有ID参数或ID为空，则获取列表，避免直接调用/salary/base/
    return listSalaryBaseInfo({});
  }
  return request({
    url: '/salary/base/' + id,
    method: 'get'
  })
}

// 新增基本工资信息
export function addSalaryBaseInfo(data) {
  return request({
    url: '/salary/base',
    method: 'post',
    data: data
  })
}

// 修改基本工资信息
export function updateSalaryBaseInfo(data) {
  return request({
    url: '/salary/base',
    method: 'put',
    data: data
  })
}

// 删除基本工资信息
export function delSalaryBaseInfo(id) {
  return request({
    url: '/salary/base/' + id,
    method: 'delete'
  })
}

// 根据员工ID获取基本工资信息
export function getSalaryBaseInfoByEmpId(empId) {
  return request({
    url: '/salary/base/' + empId,
    method: 'get'
  })
} 