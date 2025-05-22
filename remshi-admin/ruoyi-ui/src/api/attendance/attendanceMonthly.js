import request from '@/utils/request'

// 查询月度考勤列表
export function listAttendanceMonthly(query) {
  return request({
    url: '/attendance/attendance/monthly/list',
    method: 'get',
    params: query
  })
}

// 查询月度考勤详细
export function getAttendanceMonthly(id) {
  return request({
    url: '/attendance/attendance/monthly/' + id,
    method: 'get'
  })
}

// 新增月度考勤
export function addAttendanceMonthly(data) {
  return request({
    url: '/attendance/attendance/monthly',
    method: 'post',
    data: data
  })
}

// 修改月度考勤
export function updateAttendanceMonthly(data) {
  return request({
    url: '/attendance/attendance/monthly',
    method: 'put',
    data: data
  })
}

// 删除月度考勤
export function delAttendanceMonthly(id) {
  return request({
    url: '/attendance/attendance/monthly/' + id,
    method: 'delete'
  })
}

// 生成月度考勤统计
export function generateMonthly(data) {
  return request({
    url: '/attendance/attendance/monthly/generate',
    method: 'post',
    data: data
  })
}

// 导出月度考勤
export function exportMonthlyAttendance(query) {
  return request({
    url: '/attendance/attendance/monthly/export',
    method: 'post',
    params: query
  })
}
