package com.ruoyi.salary.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import com.ruoyi.salary.domain.SalaryMonthly;
import com.ruoyi.salary.service.ISalaryMonthlyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;

/**
 * 月度薪资报表Controller
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@RestController
@RequestMapping("/salary/monthly")
public class SalaryMonthlyController extends BaseController
{
    @Autowired
    private ISalaryMonthlyService salaryMonthlyService;

    /**
     * 查询月度薪资报表列表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:list')")
    @GetMapping("/list")
    public TableDataInfo list(SalaryMonthly salaryMonthly)
    {
        startPage();
        List<SalaryMonthly> list = salaryMonthlyService.selectSalaryMonthlyList(salaryMonthly);
        return getDataTable(list);
    }

    /**
     * 导出月度薪资报表列表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:export')")
    @Log(title = "月度薪资报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalaryMonthly salaryMonthly)
    {
        List<SalaryMonthly> list = salaryMonthlyService.selectSalaryMonthlyList(salaryMonthly);
        ExcelUtil<SalaryMonthly> util = new ExcelUtil<SalaryMonthly>(SalaryMonthly.class);
        util.exportExcel(response, list, "月度薪资报表数据");
    }

    /**
     * 获取月度薪资报表详细信息
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(salaryMonthlyService.selectSalaryMonthlyById(id));
    }

    /**
     * 新增月度薪资报表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:add')")
    @Log(title = "月度薪资报表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SalaryMonthly salaryMonthly)
    {
        return toAjax(salaryMonthlyService.insertSalaryMonthly(salaryMonthly));
    }

    /**
     * 修改月度薪资报表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:edit')")
    @Log(title = "月度薪资报表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SalaryMonthly salaryMonthly)
    {
        return toAjax(salaryMonthlyService.updateSalaryMonthly(salaryMonthly));
    }

    /**
     * 删除月度薪资报表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:remove')")
    @Log(title = "月度薪资报表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(salaryMonthlyService.deleteSalaryMonthlyByIds(ids));
    }
    
    /**
     * 批量生成月度薪资报表
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:generate')")
    @Log(title = "生成月度薪资", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String, Object> params)
    {
        // 从请求参数中获取年份和月份
        Integer year = params.get("year") != null ? Integer.parseInt(params.get("year").toString()) : null;
        Integer month = params.get("month") != null ? Integer.parseInt(params.get("month").toString()) : null;
        
        if (year == null || month == null) {
            return AjaxResult.error("年份和月份不能为空");
        }
        
        try {
            int count = salaryMonthlyService.generateMonthlySalary(year, month);
            if (count > 0) {
                return AjaxResult.success("成功生成" + count + "条月度薪资记录");
            } else {
                // 成功但没有生成任何记录的情况
                return AjaxResult.success(year + "年" + month + "月的工资记录无需生成");
            }
        } catch (ServiceException e) {
            // 这是业务逻辑异常，直接将消息传递给前端
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            // 其他未预期的异常，记录日志
            logger.error("生成月度薪资失败", e);
            return AjaxResult.error("生成月度薪资失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据员工ID获取月度薪资
     */
    @PreAuthorize("@ss.hasPermi('salary:monthly:query')")
    @GetMapping("/employee/{empId}")
    public AjaxResult getByEmpId(@PathVariable("empId") String empId, Integer year, Integer month)
    {
        SalaryMonthly query = new SalaryMonthly();
        query.setEmpId(empId);
        
        if (year != null) {
            query.setYear(year);
        }
        
        if (month != null) {
            query.setMonth(month);
        }
        
        List<SalaryMonthly> list = salaryMonthlyService.selectSalaryMonthlyList(query);
        return success(list);
    }
} 