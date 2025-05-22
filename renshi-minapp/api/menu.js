import request from '@/utils/request'

/**
 * 获取路由
 * @returns {Promise} 返回包含路由数据的Promise
 */
export function getRouters() {
  return request({
    url: '/getRouters',
    method: 'get'
  })
}