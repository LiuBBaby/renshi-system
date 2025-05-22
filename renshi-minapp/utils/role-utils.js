/**
 * 角色工具函数
 */

// 角色优先级, 数值越小优先级越高
const ROLE_PRIORITIES = {
  'admin': 1,
  'manager': 2,
  'group': 3,
  'user': 4
};

/**
 * 获取主要角色
 * @param {Array} roles 角色数组
 * @returns {String} 主要角色
 */
export function getMainRole(roles) {
  if (!roles || roles.length === 0) {
    return 'user'; // 默认为普通用户
  }

  // 按照优先级排序
  let mainRole = 'user';
  let highestPriority = 999;

  for (let i = 0; i < roles.length; i++) {
    const role = roles[i];
    const roleKey = typeof role === 'string' ? role : (role.roleKey || '');
    
    if (ROLE_PRIORITIES[roleKey] && ROLE_PRIORITIES[roleKey] < highestPriority) {
      highestPriority = ROLE_PRIORITIES[roleKey];
      mainRole = roleKey;
    }
  }

  return mainRole;
}

/**
 * 保存主要角色
 * @param {Array} roles 角色数组
 */
export function saveUserRole(roles) {
  const mainRole = getMainRole(roles);
  uni.setStorageSync('userRole', mainRole);
  return mainRole;
}

/**
 * 获取当前用户角色
 * @returns {String} 用户角色
 */
export function getCurrentRole() {
  return uni.getStorageSync('userRole') || 'user';
}

/**
 * 检查用户是否有指定角色
 * @param {String} roleKey 角色键
 * @returns {Boolean}
 */
export function hasRole(roleKey) {
  const currentRole = getCurrentRole();
  
  // 管理员有所有权限
  if (currentRole === 'admin') {
    return true;
  }
  
  return currentRole === roleKey;
}

/**
 * 判断是否组长或以上角色
 * @returns {Boolean}
 */
export function isGroupOrAbove() {
  const currentRole = getCurrentRole();
  return currentRole === 'group' || currentRole === 'manager' || currentRole === 'admin';
}

/**
 * 判断是否部门经理或以上角色
 * @returns {Boolean}
 */
export function isManagerOrAbove() {
  const currentRole = getCurrentRole();
  return currentRole === 'manager' || currentRole === 'admin';
}

/**
 * 获取用户EmpID
 * @returns {String} 员工ID
 */
export function getCurrentEmpId() {
  return uni.getStorageSync('empId') || '';
}

/**
 * 判断申请记录是否是当前用户提交的
 * @param {Object} record 申请记录
 * @returns {Boolean}
 */
export function isOwnApplication(record) {
  if (!record) return false;
  
  // 从userInfo对象中获取userId
  const userInfo = uni.getStorageSync('userInfo');
  const userId = userInfo ? userInfo.userId : null;
  
  // 优先使用userId字段判断
  if (userId && record.userId) {
    return parseInt(userId) === parseInt(record.userId);
  }
  
  // 如果没有userId，使用empId判断
  const empId = getCurrentEmpId();
  if (!empId) return false;
  
  // 请假申请和出差申请的字段名不同
  const recordEmpId = record.examineInfoId || record.tripInfoId || '';
  
  return empId === recordEmpId;
}

/**
 * 判断申请是否需要当前用户审批或已由当前用户审批
 * @param {Object} record 申请记录
 * @param {Boolean} showApproved 是否显示已审批的记录
 * @returns {Boolean}
 */
export function needsMyApproval(record, showApproved = false) {
  if (!record) return false;
  
  // 如果是自己的申请，则不需要自己审批
  if (isOwnApplication(record)) {
    return false;
  }
  
  const currentRole = getCurrentRole();
  const status = record.examineInfoResult !== undefined ? record.examineInfoResult : record.tripInfoResult;
  
  // 已撤销的申请不需要审批
  if (status === 4) {
    return false;
  }
  
  // 对于"待我审批"选项，除了显示需要当前角色审批的记录外
  // 如果showApproved为true，也显示已处理过的记录(状态2或3)
  
  // 一级审批(status=0)需要组长或以上角色审批
  if (status === 0 && (currentRole === 'group' || currentRole === 'manager' || currentRole === 'admin')) {
    return true;
  }
  
  // 二级审批(status=1)需要经理或管理员审批
  if (status === 1 && (currentRole === 'manager' || currentRole === 'admin')) {
    return true;
  }
  
  // 如果需要显示已审批记录，检查审批人
  if (showApproved && (status === 2 || status === 3)) {
    // 检查是否有审批人字段且匹配当前用户
    const approver = record.examineInfoPeople || record.tripInfoPeople;
    const userInfo = uni.getStorageSync('userInfo');
    const nickname = userInfo ? userInfo.nickName : '';
    
    if (approver && nickname && approver.includes(nickname)) {
      return true;
    }
  }
  
  return false;
}

/**
 * 获取适合当前角色的自定义状态文本
 * @param {Number} status 状态码
 * @param {Boolean} isOwnRecord 是否是自己的记录
 * @returns {String} 状态文本
 */
export function getCustomStatusText(status, isOwnRecord) {
  const currentRole = getCurrentRole();
  
  // 如果是自己的申请，根据角色显示不同的状态文本
  if (isOwnRecord) {
    if (currentRole === 'user') {
      // 普通员工看到自己的申请
      if (status === 0) return '待组长审批';
      if (status === 1) return '待经理审批';
    } else if (currentRole === 'group') {
      // 组长看到自己的申请，无论状态是0还是1，都显示为"待经理审批"
      // 组长不需要组长审批，直接进入经理审批
      if (status === 0 || status === 1) return '待经理审批';
    } else if (currentRole === 'manager') {
      // 经理看到自己的申请，两种状态都显示为"待审批"
      if (status === 0 || status === 1) return '待审批';
    }
  }
  
  // 其他状态使用通用映射
  const statusMap = {
    0: '待组长审批',
    1: '待经理审批',
    2: '已通过',
    3: '审批驳回',
    4: '已撤销'
  };
  
  return statusMap[status] || '未知状态';
} 