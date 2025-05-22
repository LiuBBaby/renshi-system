import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardData() {
  return request({
    url: '/system/index/dashboardData',
    method: 'get'
  })
}

// 获取考勤统计数据
export function getAttendanceStatistics(beginDate, endDate) {
  return request({
    url: '/system/index/attendanceStatistics',
    method: 'get',
    params: {
      beginDate,
      endDate
    }
  })
} 