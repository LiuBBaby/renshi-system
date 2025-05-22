import request from '@/utils/request'

// 查询考勤管理列表
export function listAttendance(query) {
  return request({
    url: '/attendance/attendance/list',
    method: 'get',
    params: query
  })
}

// 查询考勤管理详细
export function getAttendance(attendanceInfoId) {
  return request({
    url: '/attendance/attendance/' + attendanceInfoId,
    method: 'get'
  })
}

// 新增考勤管理
export function addAttendance(data) {
  return request({
    url: '/attendance/attendance',
    method: 'post',
    data: data
  })
}

// 修改考勤管理
export function updateAttendance(data) {
  return request({
    url: '/attendance/attendance',
    method: 'put',
    data: data
  })
}

// 删除考勤管理
export function delAttendance(attendanceInfoId) {
  return request({
    url: '/attendance/attendance/' + attendanceInfoId,
    method: 'delete'
  })
}

// 查询月度考勤列表
export function listMonthlyAttendance(query) {
  return request({
    url: '/attendance/attendance/monthly/list',
    method: 'get',
    params: query
  })
}

// 导出月度考勤数据
export function exportMonthlyAttendance(query) {
  return request({
    url: '/attendance/attendance/monthly/export',
    method: 'post',
    params: query
  })
}

// 生成月度考勤统计
export function generateMonthlyAttendance(data) {
  return request({
    url: '/attendance/attendance/monthly/generate',
    method: 'post',
    data: data
  })
}
