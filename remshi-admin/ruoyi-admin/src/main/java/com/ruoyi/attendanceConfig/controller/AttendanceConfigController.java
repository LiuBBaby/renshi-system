package com.ruoyi.attendanceConfig.controller;

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
import com.ruoyi.attendanceConfig.domain.AttendanceConfig;
import com.ruoyi.attendanceConfig.service.IAttendanceConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考勤规则字典Controller
 * 
 * @author 苏天霖
 * @date 2025-03-30
 */
@RestController
@RequestMapping("/attendanceConfig/attendanceConfig")
public class AttendanceConfigController extends BaseController
{
    @Autowired
    private IAttendanceConfigService attendanceConfigService;

    /**
     * 查询考勤规则字典列表
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(AttendanceConfig attendanceConfig)
    {
        startPage();
        List<AttendanceConfig> list = attendanceConfigService.selectAttendanceConfigList(attendanceConfig);
        return getDataTable(list);
    }

    /**
     * 导出考勤规则字典列表
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:export')")
    @Log(title = "考勤规则字典", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendanceConfig attendanceConfig)
    {
        List<AttendanceConfig> list = attendanceConfigService.selectAttendanceConfigList(attendanceConfig);
        ExcelUtil<AttendanceConfig> util = new ExcelUtil<AttendanceConfig>(AttendanceConfig.class);
        util.exportExcel(response, list, "考勤规则字典数据");
    }

    /**
     * 获取考勤规则字典详细信息
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(attendanceConfigService.selectAttendanceConfigById(id));
    }

    /**
     * 新增考勤规则字典
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:add')")
    @Log(title = "考勤规则字典", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceConfig attendanceConfig)
    {
        return toAjax(attendanceConfigService.insertAttendanceConfig(attendanceConfig));
    }

    /**
     * 修改考勤规则字典
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:edit')")
    @Log(title = "考勤规则字典", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceConfig attendanceConfig)
    {
        return toAjax(attendanceConfigService.updateAttendanceConfig(attendanceConfig));
    }

    /**
     * 删除考勤规则字典
     */
    @PreAuthorize("@ss.hasPermi('attendanceConfig:attendanceConfig:remove')")
    @Log(title = "考勤规则字典", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(attendanceConfigService.deleteAttendanceConfigByIds(ids));
    }
}
