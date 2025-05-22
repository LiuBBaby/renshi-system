package com.ruoyi.interview.controller;

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
import com.ruoyi.interview.domain.InterviewInfo;
import com.ruoyi.interview.service.IInterviewInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 面试计划Controller
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/interview/interview")
public class InterviewInfoController extends BaseController
{
    @Autowired
    private IInterviewInfoService interviewInfoService;

    /**
     * 查询面试计划列表
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterviewInfo interviewInfo)
    {
        startPage();
        List<InterviewInfo> list = interviewInfoService.selectInterviewInfoList(interviewInfo);
        return getDataTable(list);
    }

    /**
     * 导出面试计划列表
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:export')")
    @Log(title = "面试计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterviewInfo interviewInfo)
    {
        List<InterviewInfo> list = interviewInfoService.selectInterviewInfoList(interviewInfo);
        ExcelUtil<InterviewInfo> util = new ExcelUtil<InterviewInfo>(InterviewInfo.class);
        util.exportExcel(response, list, "面试计划数据");
    }

    /**
     * 获取面试计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(interviewInfoService.selectInterviewInfoById(id));
    }

    /**
     * 新增面试计划
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:add')")
    @Log(title = "面试计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InterviewInfo interviewInfo)
    {
        return toAjax(interviewInfoService.insertInterviewInfo(interviewInfo));
    }

    /**
     * 修改面试计划
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:edit')")
    @Log(title = "面试计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InterviewInfo interviewInfo)
    {
        return toAjax(interviewInfoService.updateInterviewInfo(interviewInfo));
    }

    /**
     * 删除面试计划
     */
    @PreAuthorize("@ss.hasPermi('interview:interview:remove')")
    @Log(title = "面试计划", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(interviewInfoService.deleteInterviewInfoByIds(ids));
    }
}
