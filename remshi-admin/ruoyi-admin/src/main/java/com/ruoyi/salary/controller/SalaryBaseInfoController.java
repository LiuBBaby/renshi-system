package com.ruoyi.salary.controller;

import java.util.List;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.salary.domain.SalaryBaseInfo;
import com.ruoyi.salary.service.ISalaryBaseInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 薪资基本信息Controller
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@RestController
@RequestMapping("/salary/base")
public class SalaryBaseInfoController extends BaseController
{
    @Autowired
    private ISalaryBaseInfoService salaryBaseInfoService;

    /**
     * 查询薪资基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(SalaryBaseInfo salaryBaseInfo)
    {
        startPage();
        List<SalaryBaseInfo> list = salaryBaseInfoService.selectSalaryBaseInfoList(salaryBaseInfo);
        return getDataTable(list);
    }
    
    /**
     * 根路径访问重定向到列表
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:list')")
    @GetMapping
    public TableDataInfo index()
    {
        return list(new SalaryBaseInfo());
    }

    /**
     * 导出薪资基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:export')")
    @Log(title = "薪资基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalaryBaseInfo salaryBaseInfo)
    {
        List<SalaryBaseInfo> list = salaryBaseInfoService.selectSalaryBaseInfoList(salaryBaseInfo);
        ExcelUtil<SalaryBaseInfo> util = new ExcelUtil<SalaryBaseInfo>(SalaryBaseInfo.class);
        util.exportExcel(response, list, "薪资基本信息数据");
    }

    /**
     * 获取薪资基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:query')")
    @GetMapping(value = "/{empId}")
    public AjaxResult getInfo(@PathVariable("empId") String empId)
    {
        return success(salaryBaseInfoService.selectSalaryBaseInfoByEmpId(empId));
    }

    /**
     * 新增薪资基本信息
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:add')")
    @Log(title = "薪资基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SalaryBaseInfo salaryBaseInfo)
    {
        return toAjax(salaryBaseInfoService.insertSalaryBaseInfo(salaryBaseInfo));
    }

    /**
     * 修改薪资基本信息
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:edit')")
    @Log(title = "薪资基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SalaryBaseInfo salaryBaseInfo)
    {
        return toAjax(salaryBaseInfoService.updateSalaryBaseInfo(salaryBaseInfo));
    }

    /**
     * 删除薪资基本信息
     */
    @PreAuthorize("@ss.hasPermi('salary:baseInfo:remove')")
    @Log(title = "薪资基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{empIds}")
    public AjaxResult remove(@PathVariable String[] empIds)
    {
        return toAjax(salaryBaseInfoService.deleteSalaryBaseInfoByEmpIds(empIds));
    }
} 
