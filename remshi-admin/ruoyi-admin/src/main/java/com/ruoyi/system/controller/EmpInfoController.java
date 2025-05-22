package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 员工信息Controller
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@RestController
@RequestMapping("/empInfo/info")
public class EmpInfoController extends BaseController
{
    @Autowired
    private IEmpInfoService empInfoService;
    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询员工信息列表
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmpInfo empInfo)
    {
        startPage();
        List<EmpInfo> list = empInfoService.selectEmpInfoList(empInfo);
        return getDataTable(list);
    }

    /**
     * 导出员工信息列表
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:export')")
    @Log(title = "员工信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmpInfo empInfo)
    {
        List<EmpInfo> list = empInfoService.selectEmpInfoList(empInfo);
        ExcelUtil<EmpInfo> util = new ExcelUtil<EmpInfo>(EmpInfo.class);
        util.exportExcel(response, list, "员工信息数据");
    }

    /**
     * 获取员工信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:query')")
    @GetMapping(value = "/{empInfoId}")
    public AjaxResult getInfo(@PathVariable("empInfoId") String empInfoId)
    {
        return success(empInfoService.selectEmpInfoByEmpInfoId(empInfoId));
    }

    /**
     * 新增员工信息
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:add')")
    @Log(title = "员工信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmpInfo empInfo)
    {
        return toAjax(empInfoService.insertEmpInfo(empInfo));
    }

    /**
     * 修改员工信息
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:edit')")
    @Log(title = "员工信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmpInfo empInfo)
    {
        return toAjax(empInfoService.updateEmpInfo(empInfo));
    }

    /**
     * 删除员工信息
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:remove')")
    @Log(title = "员工信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{empInfoIds}")
    public AjaxResult remove(@PathVariable String[] empInfoIds)
    {
        return toAjax(empInfoService.deleteEmpInfoByEmpInfoIds(empInfoIds));
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('empInfo:info:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }


}