import request from '@/utils/request'

// 获取未打卡员工列表
export function getNotCheckedInEmployees(query) {
  return request({
    url: '/system/index/notCheckedInEmployees',
    method: 'get',
    params: query
  })
}
