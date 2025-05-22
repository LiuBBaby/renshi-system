package com.ruoyi.business.controller;

import java.util.List;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.business.domain.BusinessTripInfo;
import com.ruoyi.business.service.IBusinessTripInfoService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;

/**
 * 微信小程序出差相关接口
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/business")
public class WxBusinessTripController extends BaseController
{
    @Autowired
    private IBusinessTripInfoService businessTripInfoService;

    @Autowired
    private IEmpInfoService empInfoService;

    /**
     * 获取出差记录列表
     */
    @GetMapping("/list")
    public AjaxResult getBusinessTripList(
            @RequestParam(required = false) Integer status,
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
            String roleKey = "user"; // 默认为普通用户
            
            // 判断用户角色类型
            if (roles != null && !roles.isEmpty()) {
                for (SysRole role : roles) {
                    // 按优先级获取角色，优先级：admin > manager > group > user
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
            
            logger.info("获取出差记录, 用户ID: {}, 部门ID: {}, 角色: {}, 状态: {}, 开始日期: {}, 结束日期: {}",
                    userId, deptId, roleKey, status, startDate, endDate);

            // 构建查询条件
            BusinessTripInfo queryParam = new BusinessTripInfo();
            
            // 通过userId获取员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 设置出差记录筛选逻辑
            if (isAdmin) {
                // 管理员可以查看所有记录，不需要添加限制条件
                queryParam.getParams().put("roleKey", "admin");
            } else if ("manager".equals(roleKey)) {
                // 项目经理：查看自己的出差列表 + 下属组长的出差列表 + 组长已审批的出差列表
                queryParam.getParams().put("roleKey", "manager");
                queryParam.getParams().put("userName", loginUser.getUser().getNickName());
                queryParam.getParams().put("deptId", deptId);
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
            } else if ("group".equals(roleKey)) {
                // 组长：查看自己的出差列表 + 自己能审批的员工出差列表（包括下级部门的员工）
                queryParam.getParams().put("roleKey", "group");
                queryParam.getParams().put("userName", loginUser.getUser().getNickName());
                queryParam.getParams().put("deptId", deptId); // 组长所在部门ID
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
                
                // 获取当前部门及其所有下级部门的列表（简化后只需要传递组长的部门ID即可）
                logger.info("组长部门ID: {}", deptId);
            } else {
                // 普通用户：只能查看自己的出差列表
                queryParam.getParams().put("roleKey", "user");
                queryParam.getParams().put("empId", empInfo.getEmpInfoId());
                queryParam.setTripInfoId(empInfo.getEmpInfoId());
            }

            // 如果传入了状态参数，则按状态筛选
            if (status != null) {
                queryParam.setTripInfoResult(Integer.valueOf(status));
            }

            // 获取出差记录列表
            List<BusinessTripInfo> tripList = businessTripInfoService.selectBusinessTripInfoList(queryParam);

            // 记录查询结果数量
            logger.info("查询到出差记录数: {}", tripList.size());

            return AjaxResult.success(tripList);
        } catch (Exception e) {
            logger.error("获取出差记录列表失败", e);
            return AjaxResult.error("获取出差记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 提交出差申请
     */
    @PostMapping("/apply")
    public AjaxResult applyBusinessTrip(@RequestBody BusinessTripInfo businessTripInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();

            logger.info("提交出差申请, 用户ID: {}, 接收到的数据: {}", userId, businessTripInfo);

            // 参数校验
            if (businessTripInfo.getTripInfoDestination() == null || businessTripInfo.getTripInfoDestination().trim().isEmpty()) {
                logger.error("出差目的地不能为空");
                return AjaxResult.error("出差目的地不能为空");
            }
            
            if (businessTripInfo.getTripInfoDateBegin() == null) {
                logger.error("出差开始日期不能为空");
                return AjaxResult.error("出差开始日期不能为空");
            }
            
            if (businessTripInfo.getTripInfoDateEnd() == null) {
                logger.error("出差结束日期不能为空");
                return AjaxResult.error("出差结束日期不能为空");
            }
            
            if (businessTripInfo.getTripInfoReason() == null || businessTripInfo.getTripInfoReason().trim().isEmpty()) {
                logger.error("出差原因不能为空");
                return AjaxResult.error("出差原因不能为空");
            }

            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }

            // 设置员工信息
            businessTripInfo.setTripInfoId(empInfo.getEmpInfoId());
            businessTripInfo.setTripInfoName(empInfo.getEmpInfoName());
            businessTripInfo.setTripInfoDept(empInfo.getDeptName()); // 使用部门名称
            businessTripInfo.setTripInfoResult(0); // 0表示一级审批中
            businessTripInfo.setUserId(userId); // 设置关联用户ID

            logger.info("准备保存出差申请, 处理后的数据: {}", businessTripInfo);

            // 插入出差记录
            int rows = businessTripInfoService.insertBusinessTripInfo(businessTripInfo);
            if (rows > 0) {
                return AjaxResult.success("出差申请提交成功");
            } else {
                return AjaxResult.error("出差申请提交失败");
            }
        } catch (Exception e) {
            logger.error("提交出差申请失败", e);
            return AjaxResult.error("提交出差申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取出差详情
     */
    @GetMapping("/detail")
    public AjaxResult getBusinessTripDetail(@RequestParam Long id)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            logger.info("获取出差详情, 出差 ID: {}, 用户ID: {}", id, userId);
            
            // 根据ID查询出差记录
            BusinessTripInfo businessTripInfo = businessTripInfoService.selectBusinessTripInfoById(id.intValue());
            if (businessTripInfo == null) {
                logger.error("未找到ID为{}的出差记录", id);
                return AjaxResult.error("未找到对应的出差记录");
            }
            
            return AjaxResult.success(businessTripInfo);
        } catch (Exception e) {
            logger.error("获取出差详情失败", e);
            return AjaxResult.error("获取出差详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 审批出差申请
     */
    @PostMapping("/approve")
    public AjaxResult approveBusinessTrip(@RequestBody BusinessTripInfo approvalInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            String userName = loginUser.getUser().getNickName();
            
            logger.info("审批出差申请, 出差ID: {}, 审批结果: {}, 用户ID: {}", 
                      approvalInfo.getId(), approvalInfo.getTripInfoResult(), userId);
            
            // 根据ID查询出差记录
            BusinessTripInfo tripInfo = businessTripInfoService.selectBusinessTripInfoById(approvalInfo.getId());
            if (tripInfo == null) {
                logger.error("未找到ID为{}的出差记录", approvalInfo.getId());
                return AjaxResult.error("未找到对应的出差记录");
            }
            
            // 检查出差记录状态，只有一级或二级审批中状态的记录才能审批
            if (tripInfo.getTripInfoResult() != 0 && tripInfo.getTripInfoResult() != 1) {
                logger.error("出差记录状态不允许审批, 当前状态: {}", tripInfo.getTripInfoResult());
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
            if (tripInfo.getTripInfoResult() == 0) { // 一级审批中
                if (!"group".equals(roleKey) && !"admin".equals(roleKey) && !"manager".equals(roleKey)) {
                    return AjaxResult.error("您没有权限进行一级审批");
                }
            } else if (tripInfo.getTripInfoResult() == 1) { // 二级审批中
                if (!"manager".equals(roleKey) && !"admin".equals(roleKey)) {
                    return AjaxResult.error("您没有权限进行二级审批");
                }
            }
            
            // 更新审批信息
            BusinessTripInfo updateInfo = new BusinessTripInfo();
            updateInfo.setId(approvalInfo.getId());
            updateInfo.setTripInfoResult(approvalInfo.getTripInfoResult()); // 审批结果
            updateInfo.setRemark(approvalInfo.getRemark()); // 审批备注
            updateInfo.setTripInfoPeople(userName); // 审批人
            
            // 判断是一级还是二级审批
            if (tripInfo.getTripInfoResult() == 0) { // 一级审批中
                if (approvalInfo.getTripInfoResult() == 1) { // 一级通过，进入二级
                    updateInfo.setFirstApprovalTime(new java.util.Date()); // 设置一级审批时间
                } else { // 一级拒绝
                    updateInfo.setApprovalTime(new java.util.Date()); // 设置最终审批时间
                }
            } else if (tripInfo.getTripInfoResult() == 1) { // 二级审批中
                updateInfo.setApprovalTime(new java.util.Date()); // 设置最终审批时间
            }
            
            // 更新记录
            int rows = businessTripInfoService.updateBusinessTripInfo(updateInfo);
            if (rows > 0) {
                return AjaxResult.success("出差审批操作成功");
            } else {
                return AjaxResult.error("出差审批操作失败");
            }
        } catch (Exception e) {
            logger.error("审批出差申请失败", e);
            return AjaxResult.error("审批出差申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 撤销出差申请
     */
    @PostMapping("/cancel")
    public AjaxResult cancelBusinessTrip(@RequestBody BusinessTripInfo tripInfo)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            Integer id = tripInfo.getId();
            logger.info("撤销出差申请, 出差 ID: {}, 用户ID: {}", id, userId);
            
            // 根据ID查询出差记录
            BusinessTripInfo businessTripInfo = businessTripInfoService.selectBusinessTripInfoById(id);
            if (businessTripInfo == null) {
                logger.error("未找到ID为{}的出差记录", id);
                return AjaxResult.error("未找到对应的出差记录");
            }
            
            // 检查是否为申请人本人操作
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null || !empInfo.getEmpInfoId().equals(businessTripInfo.getTripInfoId())) {
                logger.error("非申请人本人，无法撤销申请. 申请人ID: {}, 当前用户员工ID: {}", 
                          businessTripInfo.getTripInfoId(), empInfo != null ? empInfo.getEmpInfoId() : "未找到");
                return AjaxResult.error("只有申请人本人才能撤销申请");
            }
            
            // 检查出差记录状态，只有待审批或审批中的记录才能撤销
            if (businessTripInfo.getTripInfoResult() > 1) {
                logger.error("出差记录状态不允许撤销, 当前状态: {}", businessTripInfo.getTripInfoResult());
                return AjaxResult.error("当前记录状态不允许撤销操作");
            }
            
            // 更新为撤销状态
            BusinessTripInfo updateInfo = new BusinessTripInfo();
            updateInfo.setId(id);
            updateInfo.setTripInfoResult(4); // 设置状态为已撤销
            updateInfo.setRemark("申请人撤销"); // 备注
            
            // 更新记录
            int rows = businessTripInfoService.updateBusinessTripInfo(updateInfo);
            if (rows > 0) {
                return AjaxResult.success("出差撤销操作成功");
            } else {
                return AjaxResult.error("出差撤销操作失败");
            }
        } catch (Exception e) {
            logger.error("撤销出差申请失败", e);
            return AjaxResult.error("撤销出差申请失败: " + e.getMessage());
        }
    }
}
