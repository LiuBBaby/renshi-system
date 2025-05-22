package com.ruoyi.examine.controller;

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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.examine.domain.examine;
import com.ruoyi.examine.service.IexamineService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 请假审批Controller
 * 
 * @author 苏天霖
 * @date 2025-03-31
 */
@RestController
@RequestMapping("/examine/examine")
public class examineController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(examineController.class);
    
    @Autowired
    private IexamineService examineService;
    
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询请假审批列表
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:list')")
    @GetMapping("/list")
    public TableDataInfo list(examine examine)
    {
        try {
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            log.info("请假审批列表 - 当前用户部门ID: {}", userDeptId);
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId); // 添加当前部门
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            log.info("请假审批列表 - 查询部门IDs: {}", deptIds);
            
            // 设置查询条件，只查询指定部门的申请
            startPage();
            List<examine> list = examineService.selectexamineByDeptIds(examine, deptIds);
            return getDataTable(list);
        } catch (Exception e) {
            log.error("获取请假审批列表失败", e);
            return getDataTable(new ArrayList<>());
        }
    }

    /**
     * 导出请假审批列表
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:export')")
    @Log(title = "请假审批", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, examine examine)
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
            List<examine> list = examineService.selectexamineByDeptIds(examine, deptIds);
            ExcelUtil<examine> util = new ExcelUtil<examine>(examine.class);
            util.exportExcel(response, list, "请假审批数据");
        } catch (Exception e) {
            log.error("导出请假审批列表失败", e);
        }
    }

    /**
     * 获取请假审批详细信息
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(examineService.selectexamineById(id));
    }

    /**
     * 新增请假审批
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:add')")
    @Log(title = "请假审批", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody examine examine)
    {
        return toAjax(examineService.insertexamine(examine));
    }

    /**
     * 修改请假审批
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:edit')")
    @Log(title = "请假审批", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody examine examine)
    {
        return toAjax(examineService.updateexamine(examine));
    }

    /**
     * 删除请假审批
     */
    @PreAuthorize("@ss.hasPermi('examine:examine:remove')")
    @Log(title = "请假审批", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(examineService.deleteexamineByIds(ids));
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
