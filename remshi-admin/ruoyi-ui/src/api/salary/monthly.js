import request from '@/utils/request'

// 查询月度工资记录列表
export function listSalaryMonthly(query) {
  return request({
    url: '/salary/monthly/list',
    method: 'get',
    params: query
  })
}

// 查询月度工资记录详细
export function getSalaryMonthly(id) {
  return request({
    url: '/salary/monthly/' + id,
    method: 'get'
  })
}

// 新增月度工资记录
export function addSalaryMonthly(data) {
  return request({
    url: '/salary/monthly',
    method: 'post',
    data: data
  })
}

// 修改月度工资记录
export function updateSalaryMonthly(data) {
  return request({
    url: '/salary/monthly',
    method: 'put',
    data: data
  })
}

// 删除月度工资记录
export function delSalaryMonthly(id) {
  return request({
    url: '/salary/monthly/' + id,
    method: 'delete'
  })
}

// 获取员工的月度工资记录
export function getSalaryMonthlyByEmpId(empId, year, month) {
  return request({
    url: `/salary/monthly/employee/${empId}`,
    method: 'get',
    params: { year, month }
  })
}

// 批量生成月度工资
export function generateMonthlySalary(year, month) {
  return request({
    url: '/salary/monthly/generate',
    method: 'post',
    data: { year, month }
  })
}

// 导出月度工资
export function exportSalaryMonthly(query) {
  return request({
    url: '/salary/monthly/export',
    method: 'get',
    params: query
  })
} 