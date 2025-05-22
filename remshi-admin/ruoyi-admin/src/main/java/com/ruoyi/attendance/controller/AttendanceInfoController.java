package com.ruoyi.attendance.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.attendance.domain.AttendanceMonthlyStatsVO;
import com.ruoyi.attendance.service.IAttendanceInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考勤管理Controller
 *
 * @author ruoyi
 * @date 2025-03-30
 */
@RestController
@RequestMapping("/attendance/attendance")
public class AttendanceInfoController extends BaseController
{
    @Autowired
    private IAttendanceInfoService attendanceInfoService;

    /**
     * 查询考勤管理列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:list')")
    @GetMapping("/list")
    public TableDataInfo list(AttendanceInfo attendanceInfo)
    {
        startPage();
        List<AttendanceInfo> list = attendanceInfoService.selectAttendanceInfoList(attendanceInfo);
        return getDataTable(list);
    }

    /**
     * 导出考勤管理列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:export')")
    @Log(title = "考勤管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendanceInfo attendanceInfo)
    {
        List<AttendanceInfo> list = attendanceInfoService.selectAttendanceInfoList(attendanceInfo);
        ExcelUtil<AttendanceInfo> util = new ExcelUtil<AttendanceInfo>(AttendanceInfo.class);
        util.exportExcel(response, list, "考勤管理数据");
    }

    /**
     * 获取考勤管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:query')")
    @GetMapping(value = "/{attendanceInfoId}")
    public AjaxResult getInfo(@PathVariable("attendanceInfoId") String attendanceInfoId)
    {
        return success(attendanceInfoService.selectAttendanceInfoByAttendanceInfoId(attendanceInfoId));
    }

    /**
     * 新增考勤管理
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:add')")
    @Log(title = "考勤管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceInfo attendanceInfo)
    {
        return toAjax(attendanceInfoService.insertAttendanceInfo(attendanceInfo));
    }

    /**
     * 修改考勤管理
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:edit')")
    @Log(title = "考勤管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceInfo attendanceInfo)
    {
        return toAjax(attendanceInfoService.updateAttendanceInfo(attendanceInfo));
    }

    /**
     * 删除考勤管理
     */
    @PreAuthorize("@ss.hasPermi('attendance:attendance:remove')")
    @Log(title = "考勤管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{attendanceInfoIds}")
    public AjaxResult remove(@PathVariable String[] attendanceInfoIds)
    {
        return toAjax(attendanceInfoService.deleteAttendanceInfoByAttendanceInfoIds(attendanceInfoIds));
    }
    
    /**
     * 查询月度考勤列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:list')")
    @GetMapping("/monthly/list")
    public TableDataInfo monthlyList(AttendanceInfo attendanceInfo)
    {
        startPage();
        List<AttendanceMonthlyStatsVO> list = attendanceInfoService.selectMonthlyStatistics(attendanceInfo);
        return getDataTable(list);
    }

    /**
     * 导出月度考勤数据
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:export')")
    @Log(title = "月度考勤", businessType = BusinessType.EXPORT)
    @PostMapping("/monthly/export")
    public void exportMonthly(HttpServletResponse response, AttendanceInfo attendanceInfo)
    {
        List<AttendanceMonthlyStatsVO> list = attendanceInfoService.selectMonthlyStatistics(attendanceInfo);
        ExcelUtil<AttendanceMonthlyStatsVO> util = new ExcelUtil<AttendanceMonthlyStatsVO>(AttendanceMonthlyStatsVO.class);
        util.exportExcel(response, list, "月度考勤数据");
    }
    
    /**
     * 生成月度考勤统计
     */
    @PreAuthorize("@ss.hasPermi('attendance:monthly:generate')")
    @Log(title = "月度考勤", businessType = BusinessType.INSERT)
    @PostMapping("/monthly/generate")
    public AjaxResult generateMonthly(@RequestBody Map<String, Object> params)
    {
        try {
            // 确保正确解析年份和月份参数
            int year = 0;
            int month = 0;
            
            // 年份处理
            if (params.containsKey("year")) {
                Object yearObj = params.get("year");
                if (yearObj instanceof Integer) {
                    year = (Integer) yearObj;
                } else if (yearObj instanceof String) {
                    year = Integer.parseInt((String) yearObj);
                } else {
                    return error("年份参数格式错误");
                }
            } else {
                return error("缺少年份参数");
            }
            
            // 月份处理
            if (params.containsKey("month")) {
                Object monthObj = params.get("month");
                if (monthObj instanceof Integer) {
                    month = (Integer) monthObj;
                } else if (monthObj instanceof String) {
                    month = Integer.parseInt((String) monthObj);
                } else {
                    return error("月份参数格式错误");
                }
            } else {
                return error("缺少月份参数");
            }
            
            return toAjax(attendanceInfoService.generateMonthlyStatistics(year, month));
        } catch (Exception e) {
            logger.error("生成月度考勤统计失败", e);
            return error("生成月度考勤统计失败：" + e.getMessage());
        }
    }
}
