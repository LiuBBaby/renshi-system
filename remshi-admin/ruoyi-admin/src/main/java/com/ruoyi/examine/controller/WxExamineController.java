package com.ruoyi.examine.controller;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.examine.domain.examine;
import com.ruoyi.examine.service.IexamineService;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 微信小程序请假相关接口
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/leave")
public class WxExamineController extends BaseController
{
    @Autowired
    private IexamineService examineService;

    @Autowired
    private IEmpInfoService empInfoService;

    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 获取请假记录列表
     */
    @GetMapping("/list")
    public AjaxResult getLeaveList(
            @RequestParam(required = false) Long status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            Long deptId = loginUser.getDeptId();
            boolean isAdmin = SysUser.isAdmin(userId);
            
            // 获取用户角色信息
            List<SysRole> roles = loginUser.getUser().getRoles();
            boolean isOrdinaryUser = true;
            String roleKey = "user"; // 默认为普通用户
            
            // 判断用户角色类型
            if (roles != null && !roles.isEmpty()) {
                for (SysRole role : roles) {
                    // 按优先级获取角色，优先级：admin > manager > group > user
                    if ("admin".equals(role.getRoleKey())) {
                        roleKey = "admin";
                        isOrdinaryUser = false;
                        break;
                    } else if ("manager".equals(role.getRoleKey())) {
                        roleKey = "manager";
                        isOrdinaryUser = false;
                    } else if ("group".equals(role.getRoleKey()) && !"manager".equals(roleKey)) {
                        roleKey = "group";
                        isOrdinaryUser = false;
                    }
                }
            }
            
            logger.info("获取请假记录, 用户ID: {}, 部门ID: {}, 角色: {}, 状态: {}, 开始日期: {}, 结束日期: {}",
                    userId, deptId, roleKey, status, startDate, endDate);

            // 构建查询条件
            examine queryParam = new examine();
            
            // 通过userId获取员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 设置请假记录筛选逻辑
            if (isAdmin) {
                // 管理员可以查看所有记录，不需要添加限制条件
                queryParam.getParams().put("roleKey", "admin");
            } else if ("manager".equals(roleKey)) {
                // 项目经理：查看自己的请假列表 + 下属组长的请假列表 + 组长已审批的请假列表
                queryParam.getParams().put("roleKey", "manager");
                queryParam.getParams().put("userName", loginUser.getUser().getNickName());
                queryParam.getParams().put("deptId", deptId);
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
            } else if ("group".equals(roleKey)) {
                // 组长：查看自己的请假列表 + 自己能审批的员工请假列表（包括下级部门的员工）
                queryParam.getParams().put("roleKey", "group");
                queryParam.getParams().put("userName", loginUser.getUser().getNickName());
                queryParam.getParams().put("deptId", deptId); // 组长所在部门ID
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
                
                // 获取当前部门及其所有下级部门的列表（简化后只需要传递组长的部门ID即可）
                logger.info("组长部门ID: {}", deptId);
            } else {
                // 普通用户：只能查看自己的请假列表
                queryParam.getParams().put("roleKey", "user");
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
                queryParam.setExamineInfoId(empInfo.getEmpInfoId());
            }

            // 如果传入了状态参数，则按状态筛选
            if (status != null) {
                queryParam.setExamineInfoResult(status);
            }

            // 获取请假记录列表
            List<examine> leaveList = examineService.selectexamineList(queryParam);

            // 记录查询结果数量
            logger.info("查询到请假记录数: {}", leaveList.size());

            return AjaxResult.success(leaveList);
        } catch (Exception e) {
            logger.error("获取请假记录列表失败", e);
            return AjaxResult.error("获取请假记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 提交请假申请
     */
    @PostMapping("/apply")
    public AjaxResult applyLeave(@RequestBody examine leaveInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();

            logger.info("提交请假申请, 用户ID: {}", userId);

            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }

            // 设置员工信息
            leaveInfo.setExamineInfoId(empInfo.getEmpInfoId());
            leaveInfo.setExamineInfoName(empInfo.getEmpInfoName());
            leaveInfo.setExamineInfoDept(empInfo.getDeptName()); // 使用部门名称
            leaveInfo.setExamineInfoResult(0L); // 0表示一级审批中
            leaveInfo.setUserId(userId); // 设置关联用户ID
            
            // 从请假原因中提取请假类型
            String reasonText = leaveInfo.getExamineInfoReason();
            if (reasonText != null && reasonText.contains(":")) {
                String[] parts = reasonText.split(":", 2);
                leaveInfo.setLeaveType(parts[0].trim()); // 设置请假类型
                leaveInfo.setExamineInfoReason(parts[1].trim()); // 更新请假原因
            }

            // 插入请假记录
            int rows = examineService.insertexamine(leaveInfo);
            if (rows > 0) {
                return AjaxResult.success("请假申请提交成功");
            } else {
                return AjaxResult.error("请假申请提交失败");
            }
        } catch (Exception e) {
            logger.error("提交请假申请失败", e);
            return AjaxResult.error("提交请假申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取请假详情
     */
    @GetMapping("/detail")
    public AjaxResult getLeaveDetail(@RequestParam Long id)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            logger.info("获取请假详情, 请假ID: {}, 用户ID: {}", id, userId);
            
            // 根据ID查询请假记录
            examine leaveInfo = examineService.selectexamineById(id);
            if (leaveInfo == null) {
                logger.error("未找到ID为{}的请假记录", id);
                return AjaxResult.error("未找到对应的请假记录");
            }
            
            return AjaxResult.success(leaveInfo);
        } catch (Exception e) {
            logger.error("获取请假详情失败", e);
            return AjaxResult.error("获取请假详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 审批请假申请
     */
    @PostMapping("/approve")
    public AjaxResult approveLeave(@RequestBody examine approvalInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            String userName = loginUser.getUser().getNickName();
            
            logger.info("审批请假申请, 请假ID: {}, 审批结果: {}, 用户ID: {}", 
                      approvalInfo.getId(), approvalInfo.getExamineInfoResult(), userId);
            
            // 根据ID查询请假记录
            examine leaveInfo = examineService.selectexamineById(approvalInfo.getId());
            if (leaveInfo == null) {
                logger.error("未找到ID为{}的请假记录", approvalInfo.getId());
                return AjaxResult.error("未找到对应的请假记录");
            }
            
            // 检查请假记录状态，只有一级或二级审批中状态的记录才能审批
            if (leaveInfo.getExamineInfoResult() != 0 && leaveInfo.getExamineInfoResult() != 1) {
                logger.error("请假记录状态不允许审批, 当前状态: {}", leaveInfo.getExamineInfoResult());
                return AjaxResult.error("当前记录状态不允许审批操作");
            }
            
            // 获取角色信息
            List<SysRole> roles = loginUser.getUser().getRoles();
            String roleKey = "user"; // 默认为普通用户
            
            // 确定当前用户角色
            if (roles != null && !roles.isEmpty()) {
                for (SysRole role : roles) {
                    if ("admin".equals(role.getRoleKey())) {
                        roleKey = "admin";
                        break;
                    } else if ("manager".equals(role.getRoleKey())) {
                        roleKey = "manager";
                    } else if ("group".equals(role.getRoleKey()) && !"manager".equals(roleKey)) {
                        roleKey = "group";
                    }
                }
            }
            
            // 权限检查：确保审批人有权限审批此记录
            if (leaveInfo.getExamineInfoResult() == 0) { // 一级审批中
                if (!"group".equals(roleKey) && !"admin".equals(roleKey) && !"manager".equals(roleKey)) {
                    return AjaxResult.error("您没有权限进行一级审批");
                }
            } else if (leaveInfo.getExamineInfoResult() == 1) { // 二级审批中
                if (!"manager".equals(roleKey) && !"admin".equals(roleKey)) {
                    return AjaxResult.error("您没有权限进行二级审批");
                }
            }
            
            // 更新审批信息
            examine updateInfo = new examine();
            updateInfo.setId(approvalInfo.getId());
            updateInfo.setExamineInfoResult(approvalInfo.getExamineInfoResult()); // 审批结果
            updateInfo.setRemark(approvalInfo.getRemark()); // 审批备注
            updateInfo.setExamineInfoPeople(userName); // 审批人
            
            // 判断是一级还是二级审批
            if (leaveInfo.getExamineInfoResult() == 0) { // 一级审批中
                if (approvalInfo.getExamineInfoResult() == 1) { // 一级通过，进入二级
                    updateInfo.setFirstApprovalTime(new Date()); // 设置一级审批时间
                } else { // 一级拒绝
                    updateInfo.setApprovalTime(new Date()); // 设置最终审批时间
                }
            } else if (leaveInfo.getExamineInfoResult() == 1) { // 二级审批中
                updateInfo.setApprovalTime(new Date()); // 设置最终审批时间
            }
            
            // 更新记录
            int rows = examineService.updateexamine(updateInfo);
            if (rows > 0) {
                return AjaxResult.success("请假审批操作成功");
            } else {
                return AjaxResult.error("请假审批操作失败");
            }
        } catch (Exception e) {
            logger.error("审批请假申请失败", e);
            return AjaxResult.error("审批请假申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 撤销请假申请
     */
    @PostMapping("/cancel")
    public AjaxResult cancelLeave(@RequestBody examine leaveInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            Long id = leaveInfo.getId();
            logger.info("撤销请假申请, 请假ID: {}, 用户ID: {}", id, userId);
            
            // 根据ID查询请假记录
            examine leaveRecord = examineService.selectexamineById(id);
            if (leaveRecord == null) {
                logger.error("未找到ID为{}的请假记录", id);
                return AjaxResult.error("未找到对应的请假记录");
            }
            
            // 检查是否为申请人本人操作
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null || !empInfo.getEmpInfoId().equals(leaveRecord.getExamineInfoId())) {
                logger.error("非申请人本人，无法撤销申请. 申请人ID: {}, 当前用户员工ID: {}", 
                          leaveRecord.getExamineInfoId(), empInfo != null ? empInfo.getEmpInfoId() : "未找到");
                return AjaxResult.error("只有申请人本人才能撤销申请");
            }
            
            // 检查请假记录状态，只有审批中的记录才能撤销
            if (leaveRecord.getExamineInfoResult() > 1) {
                logger.error("请假记录状态不允许撤销, 当前状态: {}", leaveRecord.getExamineInfoResult());
                return AjaxResult.error("当前记录状态不允许撤销操作");
            }
            
            // 更新为撤销状态
            examine updateInfo = new examine();
            updateInfo.setId(id);
            updateInfo.setExamineInfoResult(4L); // 设置状态为已撤销
            updateInfo.setRemark("申请人撤销"); // 备注
            
            // 更新记录
            int rows = examineService.updateexamine(updateInfo);
            if (rows > 0) {
                return AjaxResult.success("请假撤销操作成功");
            } else {
                return AjaxResult.error("请假撤销操作失败");
            }
        } catch (Exception e) {
            logger.error("撤销请假申请失败", e);
            return AjaxResult.error("撤销请假申请失败: " + e.getMessage());
        }
    }

    /**
     * 获取部门及其所有下级部门的ID列表
     * 
     * @param deptId 部门ID
     * @return 部门及其下级部门的ID列表
     */
    private List<Long> getChildDeptIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        // 添加当前部门ID
        deptIds.add(deptId);
        
        try {
            // 获取所有部门
            List<com.ruoyi.common.core.domain.entity.SysDept> allDepts = sysDeptService.selectDeptList(new com.ruoyi.common.core.domain.entity.SysDept());
            
            // 查找所有子部门
            for (com.ruoyi.common.core.domain.entity.SysDept dept : allDepts) {
                // 如果当前部门的父部门ID等于我们要查找的部门ID，说明是直接子部门
                if (dept.getParentId() != null && dept.getParentId().equals(deptId)) {
                    deptIds.add(dept.getDeptId());
                    // 递归添加子部门的子部门
                    deptIds.addAll(getChildDeptIds(dept.getDeptId()));
                }
            }
        } catch (Exception e) {
            logger.error("获取部门及其下级部门失败", e);
        }
        
        return deptIds;
    }
}
