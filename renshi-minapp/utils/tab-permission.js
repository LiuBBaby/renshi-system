import store from '@/store'
import { checkPermi, isAdmin } from '@/utils/permission'
import { tabBarPermissionMap } from '@/utils/permission-config'

/**
 * 检查用户是否拥有考勤权限
 * @returns {Boolean}
 */
export function hasAttendancePermi() {
  // 管理员拥有所有权限
  if (isAdmin()) {
    return true;
  }
  // 检查是否有考勤相关权限，只要有任一考勤权限即可
  return checkPermi(['wxapp:attendance:list']) || 
         checkPermi(['wxapp:attendance:monthly']) ||
         checkPermi(['wxapp:attendance:punch']);
}

/**
 * 检查用户是否有申请权限（请假或出差）
 * @returns {Boolean}
 */
export function hasApplyPermi() {
  // 管理员拥有所有权限
  if (isAdmin()) {
    return true;
  }
  // 检查是否有请假或出差申请权限
  return checkPermi(['wxapp:leave:apply']) || checkPermi(['wxapp:business:apply']);
}

/**
 * 检查用户是否拥有审批权限
 * @returns {Boolean}
 */
export function hasApprovalPermi() {
  // 管理员拥有所有权限
  if (isAdmin()) {
    return true;
  }
  return checkPermi(['wxapp:leave:approve']) || checkPermi(['wxapp:business:approve']);
}

/**
 * 获取基础TabBar配置
 * @returns {Array} TabBar项目列表
 */
export function getTabBarList() {
  // 基础TabBar配置 - 完整列表
  return [
    {
      pagePath: "pages/checkin/index",
      iconPath: "static/images/tabbar/checkin.png",
      selectedIconPath: "static/images/tabbar/checkin_selected.png",
      text: "考勤打卡",
      key: "checkin",
      permission: tabBarPermissionMap.checkin
    },
    {
      pagePath: "pages/apply/index",
      iconPath: "static/images/tabbar/apply.png",
      selectedIconPath: "static/images/tabbar/apply_selected.png",
      text: "申请",
      key: "apply",
      permission: tabBarPermissionMap.apply
    },
    {
      pagePath: "pages/attendance/index",
      iconPath: "static/images/tabbar/approval.png", 
      selectedIconPath: "static/images/tabbar/approval_selected.png",
      text: "考勤",
      key: "attendance",
      permission: tabBarPermissionMap.attendance
    },
    {
      pagePath: "pages/mine/index",
      iconPath: "static/images/tabbar/mine.png",
      selectedIconPath: "static/images/tabbar/mine_.png",
      text: "我的",
      key: "mine",
      permission: tabBarPermissionMap.mine
    }
  ];
}

/**
 * 获取有权限的TabBar项目
 * @returns {Array} 过滤后的TabBar列表
 */
export function getAuthorizedTabBarList() {
  // 获取完整的TabBar列表
  const fullTabBarList = getTabBarList();
  
  // 管理员可查看所有TabBar项
  if (isAdmin()) {
    return fullTabBarList;
  }
  
  // 根据权限过滤TabBar
  return fullTabBarList.filter(tab => {
    // 无权限要求的Tab项直接显示
    if (!tab.permission || tab.permission.length === 0) {
      return true;
    }
    
    // 检查是否有权限
    return checkPermi(tab.permission);
  });
}

/**
 * 设置Tab栏
 * 根据用户权限动态设置Tab栏
 */
export function setTabBarByPermission() {
  try {
    // 获取有权限的TabBar配置
    const authorizedTabs = getAuthorizedTabBarList();
    console.log("动态TabBar列表:", authorizedTabs);
    
    // 设置TabBar样式
    uni.setTabBarStyle({
      color: '#8a8a8a',
      selectedColor: '#007aff',
      backgroundColor: '#ffffff',
      borderStyle: 'black'
    });
    
    // 遍历设置TabBar项
    authorizedTabs.forEach((tab, index) => {
      try {
        uni.setTabBarItem({
          index: index,
          text: tab.text,
          iconPath: tab.iconPath,
          selectedIconPath: tab.selectedIconPath
        });
      } catch (e) {
        console.error(`设置第${index}个Tab出错:`, e);
      }
    });
    
    // 如果有权限的Tab数量少于默认Tab数量，隐藏其余Tab
    const fullTabBarList = getTabBarList();
    if (authorizedTabs.length < fullTabBarList.length) {
      for (let i = authorizedTabs.length; i < fullTabBarList.length; i++) {
        uni.hideTabBarItem({
          index: i
        });
      }
    }
  } catch (e) {
    console.error("设置TabBar总体失败:", e);
  }
} 