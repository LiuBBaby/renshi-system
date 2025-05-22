import request from '@/utils/request'

/**
 * 获取考勤列表数据
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getAttendanceList(params) {
  return request({
    url: '/attendance/list',
    method: 'get',
    params
  })
}

/**
 * 获取员工某月考勤评价数据
 * @param {String} empId 员工工号
 * @param {Number} year 年份
 * @param {Number} month 月份
 * @returns {Promise}
 */
export function getAttendanceEvaluate(empId, year, month) {
  return request({
    url: '/attendance/evaluate',
    method: 'get',
    params: {
      empId,
      year,
      month
    }
  })
}

/**
 * 提交考勤评价
 * @param {Object} data 评价数据
 * @returns {Promise}
 */
export function submitAttendanceEvaluate(data) {
  return request({
    url: '/attendance/evaluate',
    method: 'post',
    data
  })
}

/**
 * 获取月度考勤汇总数据
 * @param {Number} year 年份
 * @param {Number} month 月份
 * @param {String} deptId 部门ID(可选)
 * @returns {Promise}
 */
export function getMonthlyAttendance(year, month, deptId) {
  return request({
    url: '/attendance/monthly',
    method: 'get',
    params: {
      year,
      month,
      deptId
    }
  })
}

/**
 * 获取本周打卡详情
 * @returns {Promise}
 */
export function getWeekAttendance() {
  return request({
    url: '/attendance/week',
    method: 'get'
  })
}

/**
 * 获取考勤评价列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getAttendanceEvaluationList(params) {
  return request({
    url: '/attendance/evaluation',
    method: 'get',
    params
  })
}

/**
 * 获取部门考勤详细列表（不汇总）
 * @param {Number} deptId 部门ID
 * @param {Number} year 年份
 * @param {Number} month 月份
 * @returns {Promise}
 */
export function getDeptMonthlyDetail(deptId, year, month) {
  return request({
    url: '/attendance/dept/monthly',
    method: 'get',
    params: {
      deptId,
      year,
      month
    }
  })
}

/**
 * 提交考勤评价
 * @param {Object} data 评价数据
 * @returns {Promise}
 */
export function evaluateAttendance(data) {
  return request({
    url: '/attendance/evaluate',
    method: 'post',
    data
  })
} 