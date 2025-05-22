/**
 * 人事系统全局规则文件
 * 定义系统中使用的常量、枚举值、验证规则和业务规则
 */

// 考勤状态枚举
const ATTENDANCE_STATUS = {
  NORMAL: 0,    // 正常
  LATE: 1,      // 迟到
  EARLY_LEAVE: 2, // 早退
  OVERTIME: 3   // 加班
};

// 员工评价类型
const EMPLOYEE_EVALUATION = {
  EXCELLENT: 0, // 优
  GOOD: 1,      // 良
  AVERAGE: 2,   // 中
  POOR: 3       // 差
};

// 员工状态
const EMPLOYEE_STATUS = {
  NORMAL: 0,    // 正常
  RESIGNED: 1   // 离职
};

// 审批结果状态
const APPROVAL_STATUS = {
  FIRST_LEVEL_PENDING: 0,  // 一级审批中
  SECOND_LEVEL_PENDING: 1, // 二级审批中
  APPROVED: 2,            // 通过
  REJECTED: 3             // 未通过
};

// 面试结果状态
const INTERVIEW_STATUS = {
  PENDING: '0',   // 待面试
  PASSED: '1',    // 通过
  FAILED: '2'     // 未通过
};

// 学历要求
const EDUCATION_LEVEL = {
  COLLEGE: 0,     // 专科
  BACHELOR: 1,    // 本科
  MASTER: 2,      // 研究生
  OTHER: 3        // 其他
};

// 招聘状态
const RECRUITMENT_STATUS = {
  IN_PROGRESS: 0, // 进行中
  COMPLETED: 1    // 已完成
};

// 考勤规则
const ATTENDANCE_RULES = {
  // 考勤时间设置
  START_CHECK_TIME: '06:00',     // 上班考勤开始时间
  WORK_START_TIME: '08:30',      // 规定上班时间
  END_CHECK_TIME: '09:30',       // 上班考勤截止时间
  LEAVE_START_TIME: '16:30',     // 下班考勤开始时间
  WORK_END_TIME: '18:30',        // 规定下班时间
  LEAVE_END_TIME: '23:59',       // 下班考勤截止时间
  
  // 打卡半径(米)
  VALID_RADIUS: 100,
  
  // 考勤判定规则
  isLate: (clockInTime) => {
    // 将字符串时间转为Date对象进行比较
    const workStartTime = new Date(`2025-01-01T${ATTENDANCE_RULES.WORK_START_TIME}:00`);
    const clockIn = new Date(`2025-01-01T${clockInTime}:00`);
    return clockIn > workStartTime;
  },
  
  isEarlyLeave: (clockOutTime) => {
    // 将字符串时间转为Date对象进行比较
    const workEndTime = new Date(`2025-01-01T${ATTENDANCE_RULES.WORK_END_TIME}:00`);
    const clockOut = new Date(`2025-01-01T${clockOutTime}:00`);
    return clockOut < workEndTime;
  },
  
  // 检查是否在打卡有效范围内
  isInValidRange: (userLongitude, userLatitude, locationLongitude, locationLatitude, radius = ATTENDANCE_RULES.VALID_RADIUS) => {
    // 使用简化的距离计算公式 (实际应用中可能需要更精确的地球距离计算)
    const R = 6371000; // 地球半径，单位米
    const dLat = (userLatitude - locationLatitude) * Math.PI / 180;
    const dLon = (userLongitude - locationLongitude) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(locationLatitude * Math.PI / 180) * Math.cos(userLatitude * Math.PI / 180) *
              Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distance = R * c;
    
    return distance <= radius;
  }
};

// 表单验证规则
const VALIDATION_RULES = {
  // 手机号验证
  isValidPhone: (phone) => {
    return /^1[3-9]\d{9}$/.test(phone);
  },
  
  // 必填字段验证
  isRequired: (value) => {
    return value !== null && value !== undefined && value !== '';
  },
  
  // 身份证验证
  isValidIdCard: (idCard) => {
    return /(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(idCard);
  },
  
  // 员工工号验证 (8位数字)
  isValidEmployeeId: (empId) => {
    return /^\d{8}$/.test(empId);
  }
};

// 请假规则
const LEAVE_RULES = {
  // 请假类型
  LEAVE_TYPES: [
    { value: 'sick', label: '病假' },
    { value: 'annual', label: '年假' },
    { value: 'personal', label: '事假' },
    { value: 'marriage', label: '婚假' },
    { value: 'maternity', label: '产假' },
    { value: 'paternity', label: '陪产假' },
    { value: 'bereavement', label: '丧假' }
  ],
  
  // 请假最小单位(小时)
  MIN_LEAVE_UNIT: 0.5,
  
  // 请假有效性检查
  isValidLeaveRequest: (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    // 开始日期不能小于今天
    if (start < today) {
      return { valid: false, message: '请假开始日期不能早于今天' };
    }
    
    // 结束日期不能小于开始日期
    if (end < start) {
      return { valid: false, message: '请假结束日期不能早于开始日期' };
    }
    
    return { valid: true, message: '' };
  }
};

// 业务出差规则
const BUSINESS_TRIP_RULES = {
  // 出差申请有效性检查
  isValidTripRequest: (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    // 开始日期必须至少提前一天申请
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    if (start < tomorrow) {
      return { valid: false, message: '出差开始日期必须至少提前一天申请' };
    }
    
    // 结束日期不能小于开始日期
    if (end < start) {
      return { valid: false, message: '出差结束日期不能早于开始日期' };
    }
    
    return { valid: true, message: '' };
  }
};

// 导出所有规则
module.exports = {
  ATTENDANCE_STATUS,
  EMPLOYEE_EVALUATION,
  EMPLOYEE_STATUS,
  APPROVAL_STATUS,
  INTERVIEW_STATUS,
  EDUCATION_LEVEL,
  RECRUITMENT_STATUS,
  ATTENDANCE_RULES,
  VALIDATION_RULES,
  LEAVE_RULES,
  BUSINESS_TRIP_RULES
};
