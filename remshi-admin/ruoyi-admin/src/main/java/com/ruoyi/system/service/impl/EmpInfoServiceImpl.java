package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.ruoyi.attendance.mapper.AttendanceInfoMapper;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.examine.mapper.examineMapper;
import com.ruoyi.interview.mapper.InterviewInfoMapper;
import com.ruoyi.recruitment.mapper.RecruitmentInfoMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.EmpInfoMapper;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.domain.SysUserRole;

/**
 * 员工信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@Service
public class EmpInfoServiceImpl implements IEmpInfoService 
{
    private static final Logger log = LoggerFactory.getLogger(EmpInfoServiceImpl.class);
    
    @Autowired
    private EmpInfoMapper empInfoMapper;
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private AttendanceInfoMapper attendanceInfoMapper;
    
    @Autowired
    private examineMapper examineMapper;
    
    @Autowired
    private RecruitmentInfoMapper recruitmentInfoMapper;
    
    @Autowired
    private InterviewInfoMapper interviewInfoMapper;
    
    @Autowired
    private SysDeptMapper deptMapper;
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    /**
     * 查询员工信息
     * 
     * @param empInfoId 员工信息主键
     * @return 员工信息
     */
    @Override
    public EmpInfo selectEmpInfoByEmpInfoId(String empInfoId)
    {
        return empInfoMapper.selectEmpInfoByEmpInfoId(empInfoId);
    }

    /**
     * 查询员工信息列表
     * 
     * @param empInfo 员工信息
     * @return 员工信息
     */
    @Override
    public List<EmpInfo> selectEmpInfoList(EmpInfo empInfo)
    {
        // 获取当前用户的部门ID
        Long loginUserDeptId = SecurityUtils.getLoginUser().getDeptId();
        
        // 如果不是超级管理员，则只查询自己部门及下级部门的员工
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            if (empInfo.getParams() == null) {
                empInfo.setParams(new HashMap<>());
            }
            
            // 收集所有部门ID（当前部门、子部门及所有后代部门）
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(loginUserDeptId); // 添加当前部门ID
            
            // 获取所有部门
            List<SysDept> allDepts = deptMapper.selectDeptList(new SysDept());
            
            // 递归收集所有子部门ID
            collectChildDeptIds(allDepts, loginUserDeptId, deptIds);
            
            if (!deptIds.isEmpty()) {
                // 构建IN查询，查询这些部门的所有员工
                empInfo.getParams().put("dataScope", " AND emp_info.dept_id IN (" + StringUtils.join(deptIds, ",") + ")");
            }
        }
        
        return empInfoMapper.selectEmpInfoList(empInfo);
    }
    
    /**
     * 递归收集部门的所有子部门ID
     * 
     * @param allDepts 所有部门列表
     * @param parentId 父部门ID
     * @param deptIds 收集的部门ID列表
     */
    private void collectChildDeptIds(List<SysDept> allDepts, Long parentId, List<Long> deptIds) {
        for (SysDept dept : allDepts) {
            // 如果当前部门的父ID等于传入的parentId，说明是下级部门
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                deptIds.add(dept.getDeptId());
                // 递归查找当前部门的子部门
                collectChildDeptIds(allDepts, dept.getDeptId(), deptIds);
            }
        }
    }

    /**
     * 新增员工信息
     * 
     * @param empInfo 员工信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertEmpInfo(EmpInfo empInfo)
    {
        // 自动生成员工ID
        if (StringUtils.isEmpty(empInfo.getEmpInfoId())) {
            String empId = generateEmpId(empInfo.getDeptId());
            empInfo.setEmpInfoId(empId);
            log.info("自动生成员工工号: {}", empId);
        }
        
        // 检查userId是否已存在
        if (empInfo.getUserId() != null) {
            // 如果员工信息已包含userId，检查该用户是否存在
            SysUser existingUser = userMapper.selectUserById(empInfo.getUserId());
            if (existingUser != null) {
                // 用户已存在，无需创建新用户，直接使用现有用户
                log.info("使用现有系统用户，userId={}", existingUser.getUserId());
            } else {
                // 指定的userId不存在，需要注册新用户
                SysUser sysUser = registerEmpin(empInfo);
                if (sysUser != null && sysUser.getUserId() != null) {
                    empInfo.setUserId(sysUser.getUserId());
                }
            }
        } else {
            // 检查是否存在同名用户
            if (StringUtils.isNotEmpty(empInfo.getUsername())) {
                SysUser checkUser = userMapper.selectUserByUserName(empInfo.getUsername());
                if (checkUser != null) {
                    // 用户名已存在，使用现有用户
                    empInfo.setUserId(checkUser.getUserId());
                    log.info("使用同名系统用户，userId={}", checkUser.getUserId());
                } else {
                    // 用户不存在，注册新用户
                    SysUser sysUser = registerEmpin(empInfo);
                    if (sysUser != null && sysUser.getUserId() != null) {
                        empInfo.setUserId(sysUser.getUserId());
                    }
                }
            } else {
                // 没有用户名信息，无法创建用户
                log.warn("新增员工时缺少用户名信息，无法创建系统用户");
            }
        }
        
        empInfo.setCreateTime(DateUtils.getNowDate());
        
        // 添加员工信息
        int rows = empInfoMapper.insertEmpInfo(empInfo);
        
        // 关联普通员工角色
        if (empInfo.getUserId() != null) {
            // 获取普通员工角色ID
            Long roleId = getRegularEmployeeRoleId();
            if (roleId != null) {
                // 检查用户是否已有该角色
                boolean hasRole = false;
                List<SysRole> existingRoles = roleMapper.selectRolePermissionByUserId(empInfo.getUserId());
                if (existingRoles != null && !existingRoles.isEmpty()) {
                    for (SysRole role : existingRoles) {
                        if (role.getRoleId().equals(roleId)) {
                            hasRole = true;
                            break;
                        }
                    }
                }
                
                // 如果没有该角色，则添加
                if (!hasRole) {
                    // 创建角色关联对象
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(empInfo.getUserId());
                    userRole.setRoleId(roleId);
                    
                    // 批量添加用户角色关联
                    List<SysUserRole> userRoleList = new ArrayList<>();
                    userRoleList.add(userRole);
                    userRoleMapper.batchUserRole(userRoleList);
                }
            }
        }
        
        return rows;
    }
    
    /**
     * 注册系统用户
     * @param empInfo 员工信息
     * @return 注册的系统用户
     */
    public SysUser registerEmpin(EmpInfo empInfo) {
        String username = empInfo.getUsername(), password = empInfo.getPasssword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        if (StringUtils.isEmpty(username)) {
            throw new ServiceException("用户名不能为空");
        } else if (StringUtils.isEmpty(password)) {
            throw new ServiceException("用户密码不能为空");

        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");

        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");

        } else if (!userService.checkUserNameUnique(sysUser)) {
            throw new ServiceException("保存用户'" + username + "'失败，注册账号已存在");

        } else {
            sysUser.setPhonenumber(empInfo.getEmpInfoIphone());
            sysUser.setNickName(empInfo.getEmpInfoName());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            sysUser.setDeptId(empInfo.getDeptId());
            boolean result = userService.registerUser(sysUser);
            if (result) {
                // 获取注册用户的ID
                sysUser = userMapper.selectUserByUserName(username);
                return sysUser;
            }
        }
        return null;
    }

    /**
     * 获取普通员工角色ID
     * @return 普通员工角色ID
     */
    private Long getRegularEmployeeRoleId() {
        // 创建角色查询条件
        SysRole queryRole = new SysRole();
        queryRole.setRoleName("普通员工");
        
        // 查询角色列表
        List<SysRole> roles = roleMapper.selectRoleList(queryRole);
        
        // 如果找到了匹配的角色，返回角色ID
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0).getRoleId();
        }
        
        // 如果没找到，返回默认值101
        return 101L;
    }

    /**
     * 生成员工工号（8位：2年份+3部门+3序列）
     * 规则：自动识别当前年份的后两位 + 部门号（固定3位，不足前面补0）+ 序列号（从1开始，固定3位，不足前面补0）
     * 
     * @param deptId 部门ID
     * @return 生成的员工工号
     */
    private String generateEmpId(Long deptId) {
        if (deptId == null) {
            throw new ServiceException("生成员工工号失败：部门ID不能为空");
        }
        
        // 获取当前年份后两位
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String yearStr = String.valueOf(year).substring(2);  // 获取年份后两位
        
        // 获取部门编码（确保3位，不足前面补0）
        String deptCode = String.format("%03d", deptId);  
        if (deptCode.length() > 3) {
            // 如果部门ID超过3位，则取后3位
            deptCode = deptCode.substring(deptCode.length() - 3);
        }
        
        // 查询该部门下的员工数量，用于确定序列号
        EmpInfo queryEmp = new EmpInfo();
        queryEmp.setDeptId(deptId);
        List<EmpInfo> deptEmps = empInfoMapper.selectEmpInfoList(queryEmp);
        
        // 获取序列号（员工数量+1，确保3位，不足前面补0）
        int sequence = (deptEmps != null) ? deptEmps.size() + 1 : 1;
        String sequenceCode = String.format("%03d", sequence);
        
        // 组合成完整的员工工号
        return yearStr + deptCode + sequenceCode;
    }

    /**
     * 修改员工信息
     * 
     * @param empInfo 员工信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateEmpInfo(EmpInfo empInfo)
    {
        int rows = empInfoMapper.updateEmpInfo(empInfo);
        
        // 同步更新系统用户信息
        if (empInfo.getUserId() != null) {

            SysUser sysUser = userMapper.selectUserById(empInfo.getUserId());
            if (sysUser != null) {
                // 更新用户基本信息
                boolean needUpdateUser = false;
                
                // 更新用户名
                if (StringUtils.isNotEmpty(empInfo.getUsername()) && !empInfo.getUsername().equals(sysUser.getUserName())) {
                    // 检查用户名是否唯一
                    SysUser checkUser = new SysUser();
                    checkUser.setUserName(empInfo.getUsername());
                    if (userService.checkUserNameUnique(checkUser)) {
                        sysUser.setUserName(empInfo.getUsername());
                        needUpdateUser = true;
                    }
                }
                
                // 更新密码
                if (StringUtils.isNotEmpty(empInfo.getPasssword()) && !SecurityUtils.matchesPassword(empInfo.getPasssword(), sysUser.getPassword())) {
                    sysUser.setPassword(SecurityUtils.encryptPassword(empInfo.getPasssword()));
                    needUpdateUser = true;
                }
                
                // 更新手机号
                if (StringUtils.isNotEmpty(empInfo.getEmpInfoIphone()) && !empInfo.getEmpInfoIphone().equals(sysUser.getPhonenumber())) {
                    sysUser.setPhonenumber(empInfo.getEmpInfoIphone());
                    needUpdateUser = true;
                }
                
                // 更新昵称
                if (StringUtils.isNotEmpty(empInfo.getEmpInfoName()) && !empInfo.getEmpInfoName().equals(sysUser.getNickName())) {
                    sysUser.setNickName(empInfo.getEmpInfoName());
                    needUpdateUser = true;
                }
                
                // 更新部门
                if (empInfo.getDeptId() != null && !empInfo.getDeptId().equals(sysUser.getDeptId())) {
                    sysUser.setDeptId(empInfo.getDeptId());
                    needUpdateUser = true;
                }
                
                // 执行更新
                if (needUpdateUser) {
                    userMapper.updateUser(sysUser);
                }
            }
        }
        
        return rows;
    }

    /**
     * 批量删除员工信息
     * 
     * @param empInfoIds 需要删除的员工信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEmpInfoByEmpInfoIds(String[] empInfoIds)
    {
        // 处理每个员工ID
        for (String empInfoId : empInfoIds) {
            EmpInfo empInfo = empInfoMapper.selectEmpInfoByEmpInfoId(empInfoId);
            if (empInfo != null && empInfo.getUserId() != null) {
                // 不再删除用户，而是将用户状态修改为停用
                SysUser user = userMapper.selectUserById(empInfo.getUserId());
                if (user != null) {
                    user.setStatus("1"); // 设置状态为停用
                    userMapper.updateUser(user);
                }
            }
            
            // 删除关联的考勤记录
            attendanceInfoMapper.deleteAttendanceInfoByEmpId(empInfoId);
            // 删除关联的请假审批记录
            examineMapper.deleteexamineByEmpId(empInfoId);
            // 删除关联的招聘需求记录
            recruitmentInfoMapper.deleteRecruitmentInfoByEmpId(empInfoId);
            // 删除关联的面试计划记录
            interviewInfoMapper.deleteInterviewInfoByEmpId(empInfoId);
        }
        return empInfoMapper.deleteEmpInfoByEmpInfoIds(empInfoIds);
    }

    /**
     * 删除员工信息信息
     * 
     * @param empInfoId 员工信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEmpInfoByEmpInfoId(String empInfoId)
    {
        // 查询员工信息
        EmpInfo empInfo = empInfoMapper.selectEmpInfoByEmpInfoId(empInfoId);
        if (empInfo != null && empInfo.getUserId() != null) {
            // 不再删除用户，而是将用户状态修改为停用
            SysUser user = userMapper.selectUserById(empInfo.getUserId());
            if (user != null) {
                user.setStatus("1"); // 设置状态为停用
                userMapper.updateUser(user);
            }
        }
        
        // 删除关联的考勤记录
        attendanceInfoMapper.deleteAttendanceInfoByEmpId(empInfoId);
        // 删除关联的请假审批记录
        examineMapper.deleteexamineByEmpId(empInfoId);
        // 删除关联的招聘需求记录
        recruitmentInfoMapper.deleteRecruitmentInfoByEmpId(empInfoId);
        // 删除关联的面试计划记录
        interviewInfoMapper.deleteInterviewInfoByEmpId(empInfoId);
        
        return empInfoMapper.deleteEmpInfoByEmpInfoId(empInfoId);
    }

    /**
     * 根据用户ID查询员工信息
     * 
     * @param userId 用户ID
     * @return 员工信息
     */
    @Override
    public EmpInfo selectEmpInfoByUserId(Long userId)
    {
        return empInfoMapper.selectEmpInfoByUserId(userId);
    }
    
    /**
     * 获取在职员工数量
     * 
     * @return 在职员工数量
     */
    public long getOnJobEmployeeCount() {
        EmpInfo queryEmp = new EmpInfo();
        queryEmp.setStatus(0L); // 假设0表示在职状态
        List<EmpInfo> empList = empInfoMapper.selectEmpInfoList(queryEmp);
        return empList != null ? empList.size() : 0;
    }

    /**
     * 通过部门ID数组查询员工列表
     *
     * @param deptIds 部门ID数组
     * @return 员工列表
     */
    @Override
    public List<EmpInfo> selectEmpInfoByDeptIds(List<Long> deptIds)
    {
        return empInfoMapper.selectEmpInfoByDeptIds(deptIds);
    }
}
