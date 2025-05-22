package com.ruoyi.business.controller;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.business.domain.BusinessTripInfo;
import com.ruoyi.business.service.IBusinessTripInfoService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 出差申请Controller
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@RestController
@RequestMapping("/trip/business") // 调整基础路径以符合模块划分
public class BusinessTripInfoController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(BusinessTripInfoController.class);
    
    @Autowired
    private IBusinessTripInfoService businessTripInfoService;
    
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询出差申请列表
     */
    @PreAuthorize("@ss.hasPermi('trip:business:list')") // 调整权限标识
    @GetMapping("/list")
    public TableDataInfo list(BusinessTripInfo businessTripInfo)
    {
        try {
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            log.info("出差审批列表 - 当前用户部门ID: {}", userDeptId);
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId); // 添加当前部门
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            log.info("出差审批列表 - 查询部门IDs: {}", deptIds);
            
            // 设置查询条件，只查询指定部门的申请
            startPage();
            List<BusinessTripInfo> list = businessTripInfoService.selectBusinessTripInfoByDeptIds(businessTripInfo, deptIds);
            return getDataTable(list);
        } catch (Exception e) {
            log.error("获取出差审批列表失败", e);
            return getDataTable(new ArrayList<>());
        }
    }
    
    /**
     * 查询当前用户出差申请列表
     */
    @PreAuthorize("@ss.hasPermi('trip:business:myList')") // 新增个人查询权限
    @GetMapping("/myList")
    public TableDataInfo myList()
    {
        startPage();
        String empId = SecurityUtils.getUsername(); // 假设用户名即员工ID，或通过 SecurityUtils 获取员工ID
        List<BusinessTripInfo> list = businessTripInfoService.selectBusinessTripInfoByEmpId(empId);
        return getDataTable(list);
    }

    /**
     * 导出出差申请列表
     */
    @PreAuthorize("@ss.hasPermi('trip:business:export')") // 调整权限标识
    @Log(title = "出差申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusinessTripInfo businessTripInfo)
    {
        try {
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId);
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            
            // 设置查询条件，只导出指定部门的申请
            List<BusinessTripInfo> list = businessTripInfoService.selectBusinessTripInfoByDeptIds(businessTripInfo, deptIds);
            ExcelUtil<BusinessTripInfo> util = new ExcelUtil<BusinessTripInfo>(BusinessTripInfo.class);
            util.exportExcel(response, list, "出差申请数据");
        } catch (Exception e) {
            log.error("导出出差审批列表失败", e);
        }
    }

    /**
     * 获取出差申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('trip:business:query')") // 调整权限标识
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(businessTripInfoService.selectBusinessTripInfoById(id));
    }

    /**
     * 新增出差申请
     */
    @PreAuthorize("@ss.hasPermi('trip:business:add')") // 调整权限标识
    @Log(title = "出差申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusinessTripInfo businessTripInfo)
    {
        // Controller层通常不需要填充申请人信息，Service层已处理
        return toAjax(businessTripInfoService.insertBusinessTripInfo(businessTripInfo));
    }

    /**
     * 修改出差申请
     */
    @PreAuthorize("@ss.hasPermi('trip:business:edit')") // 调整权限标识
    @Log(title = "出差申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusinessTripInfo businessTripInfo)
    {
        return toAjax(businessTripInfoService.updateBusinessTripInfo(businessTripInfo));
    }

    /**
     * 审批出差申请
     */
    @PreAuthorize("@ss.hasPermi('trip:business:approve')") // 新增审批权限
    @Log(title = "出差申请审批", businessType = BusinessType.UPDATE) // 修改日志标题和类型
    @PutMapping("/approve/{id}/{result}")
    public AjaxResult approve(@PathVariable("id") Integer id, @PathVariable("result") Integer result)
    {
        String approver = SecurityUtils.getUsername(); // 获取当前审批人用户名
        return toAjax(businessTripInfoService.approveBusinessTripInfo(id, result, approver));
    }

    /**
     * 删除出差申请
     */
    @PreAuthorize("@ss.hasPermi('trip:business:remove')") // 调整权限标识
    @Log(title = "出差申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(businessTripInfoService.deleteBusinessTripInfoByIds(ids));
    }
    
    /**
     * 递归查找所有子部门
     * 
     * @param parentId 父部门ID
     * @param allDepts 所有部门列表
     * @param resultIds 结果集合
     */
    private void findAllChildDepts(Long parentId, List<SysDept> allDepts, List<Long> resultIds) {
        for (SysDept dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                resultIds.add(dept.getDeptId());
                // 递归查找子部门的子部门
                findAllChildDepts(dept.getDeptId(), allDepts, resultIds);
            }
        }
    }
}