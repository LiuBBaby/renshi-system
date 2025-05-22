import request from '@/utils/request'

// 获取今日考勤信息
export function getTodayAttendance() {
  return request({
    url: '/attendance/today',
    method: 'get'
  })
}

// 考勤打卡
export function checkin(data) {
  return request({
    url: '/attendance/checkin',
    method: 'post',
    data: data
  })
}

// 获取考勤配置信息
export function getAttendanceConfig() {
  return request({
    url: '/attendance/config',
    method: 'get'
  })
}

// 检查请假或出差状态
export function checkLeaveOrBusinessTrip() {
  return request({
    url: '/attendance/checkLeaveOrBusinessTrip',
    method: 'get'
  })
} 