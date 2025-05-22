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
import com.ruoyi.attendance.domain.AttendanceLocation;
import com.ruoyi.attendance.service.IAttendanceLocationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考勤打卡位置Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/attendance/location")
public class AttendanceLocationController extends BaseController
{
    @Autowired
    private IAttendanceLocationService attendanceLocationService;

    /**
     * 查询考勤打卡位置列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:list')")
    @GetMapping("/list")
    public TableDataInfo list(AttendanceLocation attendanceLocation)
    {
        startPage();
        List<AttendanceLocation> list = attendanceLocationService.selectAttendanceLocationList(attendanceLocation);
        return getDataTable(list);
    }

    /**
     * 导出考勤打卡位置列表
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:export')")
    @Log(title = "考勤打卡位置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendanceLocation attendanceLocation)
    {
        List<AttendanceLocation> list = attendanceLocationService.selectAttendanceLocationList(attendanceLocation);
        ExcelUtil<AttendanceLocation> util = new ExcelUtil<AttendanceLocation>(AttendanceLocation.class);
        util.exportExcel(response, list, "考勤打卡位置数据");
    }

    /**
     * 获取考勤打卡位置详细信息
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return AjaxResult.success(attendanceLocationService.selectAttendanceLocationById(id));
    }

    /**
     * 新增考勤打卡位置
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:add')")
    @Log(title = "考勤打卡位置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceLocation attendanceLocation)
    {
        return toAjax(attendanceLocationService.insertAttendanceLocation(attendanceLocation));
    }

    /**
     * 修改考勤打卡位置
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:edit')")
    @Log(title = "考勤打卡位置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceLocation attendanceLocation)
    {
        return toAjax(attendanceLocationService.updateAttendanceLocation(attendanceLocation));
    }

    /**
     * 删除考勤打卡位置
     */
    @PreAuthorize("@ss.hasPermi('attendance:location:remove')")
    @Log(title = "考勤打卡位置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(attendanceLocationService.deleteAttendanceLocationByIds(ids));
    }
} 