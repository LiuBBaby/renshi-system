/**
 * 日期时间工具函数
 */

/**
 * 格式化日期
 * @param {Date|string|number} date 日期对象、日期字符串或时间戳
 * @param {string} format 格式模板，默认为'YYYY-MM-DD'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  
  // 尝试转换为Date对象
  let dateObj;
  if (date instanceof Date) {
    dateObj = date;
  } else if (typeof date === 'string') {
    // 处理日期字符串
    if (date.indexOf('T') > -1) {
      // ISO格式日期字符串
      dateObj = new Date(date);
    } else if (date.indexOf('-') > -1) {
      // YYYY-MM-DD格式
      const parts = date.split('-');
      dateObj = new Date(parts[0], parts[1] - 1, parts[2]);
    } else {
      // 其他格式尝试直接解析
      dateObj = new Date(date);
    }
  } else if (typeof date === 'number') {
    // 时间戳
    dateObj = new Date(date);
  } else {
    return '';
  }
  
  // 检查日期对象是否有效
  if (isNaN(dateObj.getTime())) {
    return '';
  }
  
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const seconds = String(dateObj.getSeconds()).padStart(2, '0');
  
  // 替换格式字符
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 获取当前日期的字符串表示
 * @param {string} format 格式模板，默认为'YYYY-MM-DD'
 * @returns {string} 当前日期字符串
 */
export function getCurrentDate(format = 'YYYY-MM-DD') {
  return formatDate(new Date(), format);
}

/**
 * 计算两个日期之间的天数差
 * @param {Date|string|number} startDate 开始日期
 * @param {Date|string|number} endDate 结束日期
 * @returns {number} 相差的天数
 */
export function getDaysBetween(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  
  // 重置时间部分，只比较日期
  start.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);
  
  // 计算天数差
  const diffTime = Math.abs(end - start);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  return diffDays;
}

/**
 * 获取指定日期所在周的周一和周日
 * @param {Date|string|number} date 日期
 * @returns {Object} 包含周一和周日的对象
 */
export function getWeekRange(date) {
  const currentDate = new Date(date);
  const day = currentDate.getDay() || 7; // 将周日的0改为7
  
  // 计算周一日期
  const monday = new Date(currentDate);
  monday.setDate(currentDate.getDate() - (day - 1));
  monday.setHours(0, 0, 0, 0);
  
  // 计算周日日期
  const sunday = new Date(currentDate);
  sunday.setDate(currentDate.getDate() + (7 - day));
  sunday.setHours(23, 59, 59, 999);
  
  return {
    monday,
    sunday,
    mondayStr: formatDate(monday),
    sundayStr: formatDate(sunday)
  };
}

/**
 * 获取指定日期所在月的第一天和最后一天
 * @param {Date|string|number} date 日期
 * @returns {Object} 包含月初和月末的对象
 */
export function getMonthRange(date) {
  const currentDate = new Date(date);
  
  // 月初
  const firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
  
  // 月末
  const lastDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
  lastDay.setHours(23, 59, 59, 999);
  
  return {
    firstDay,
    lastDay,
    firstDayStr: formatDate(firstDay),
    lastDayStr: formatDate(lastDay)
  };
} 