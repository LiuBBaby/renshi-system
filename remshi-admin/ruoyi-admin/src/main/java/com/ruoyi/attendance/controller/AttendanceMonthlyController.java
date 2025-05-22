package com.ruoyi.attendance.controller;

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
import com.ruoyi.attendance.domain.AttendanceMonthly;
import com.ruoyi.attendance.service.IAttendanceMonthlyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考勤月报Controller
 * 
 * @author ruoyi
 * @date 2025-04-26 // 根据实际情况调整日期
 */
@RestController
@RequestMapping("/attendance/monthly") // 调整基础路径
public class AttendanceMonthlyController extends BaseController
{
    @Autowired
    private IAttendanceMonthlyService attendanceMonthlyService;

    /**
     * 查询考勤月报列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:list')") // 调整权限标识
    @GetMapping("/list")
    public TableDataInfo list(AttendanceMonthly attendanceMonthly)
    {
        startPage();
        List<AttendanceMonthly> list = attendanceMonthlyService.selectAttendanceMonthlyList(attendanceMonthly);
        return getDataTable(list);
    }

    /**
     * 导出考勤月报列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:export')") // 调整权限标识
    @Log(title = "考勤月报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendanceMonthly attendanceMonthly)
    {
        List<AttendanceMonthly> list = attendanceMonthlyService.selectAttendanceMonthlyList(attendanceMonthly);
        ExcelUtil<AttendanceMonthly> util = new ExcelUtil<AttendanceMonthly>(AttendanceMonthly.class);
        util.exportExcel(response, list, "考勤月报数据");
    }

    /**
     * 获取考勤月报详细信息
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:query')") // 调整权限标识
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(attendanceMonthlyService.selectAttendanceMonthlyById(id));
    }

    /**
     * 新增考勤月报(通常由系统生成，手动新增可能较少)
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:add')") // 调整权限标识
    @Log(title = "考勤月报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceMonthly attendanceMonthly)
    {
        return toAjax(attendanceMonthlyService.insertAttendanceMonthly(attendanceMonthly));
    }

    /**
     * 修改考勤月报(通常由系统生成，手动修改可能较少)
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:edit')") // 调整权限标识
    @Log(title = "考勤月报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceMonthly attendanceMonthly)
    {
        return toAjax(attendanceMonthlyService.updateAttendanceMonthly(attendanceMonthly));
    }

    /**
     * 删除考勤月报
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:remove')") // 调整权限标识
    @Log(title = "考勤月报", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(attendanceMonthlyService.deleteAttendanceMonthlyByIds(ids));
    }
    
    /**
     * 生成指定年月的考勤月报
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:generate')") // 新增生成权限
    @Log(title = "生成考勤月报", businessType = BusinessType.INSERT) // 日志类型可能需要调整
    @PostMapping("/generate/{year}/{month}")
    public AjaxResult generate(@PathVariable Integer year, @PathVariable Integer month)
    {
        return toAjax(attendanceMonthlyService.generateAttendanceMonthly(year, month));
    }
    
    /**
     * 重新生成指定年月的考勤月报
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:regenerate')") // 新增重新生成权限
    @Log(title = "重新生成考勤月报", businessType = BusinessType.UPDATE) // 日志类型可能需要调整
    @PostMapping("/regenerate/{year}/{month}")
    public AjaxResult regenerate(@PathVariable Integer year, @PathVariable Integer month)
    {
        return toAjax(attendanceMonthlyService.regenerateAttendanceMonthly(year, month));
    }
} 