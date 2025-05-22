import request from '@/utils/request'

// 查询考勤规则字典列表
export function listAttendanceConfig(query) {
  return request({
    url: '/attendanceConfig/attendanceConfig/list',
    method: 'get',
    params: query
  })
}

// 查询考勤规则字典详细
export function getAttendanceConfig(id) {
  return request({
    url: '/attendanceConfig/attendanceConfig/' + id,
    method: 'get'
  })
}

// 新增考勤规则字典
export function addAttendanceConfig(data) {
  return request({
    url: '/attendanceConfig/attendanceConfig',
    method: 'post',
    data: data
  })
}

// 修改考勤规则字典
export function updateAttendanceConfig(data) {
  return request({
    url: '/attendanceConfig/attendanceConfig',
    method: 'put',
    data: data
  })
}

// 删除考勤规则字典
export function delAttendanceConfig(id) {
  return request({
    url: '/attendanceConfig/attendanceConfig/' + id,
    method: 'delete'
  })
}
