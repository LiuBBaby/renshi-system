package com.ruoyi.recruitment.controller;

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
import com.ruoyi.recruitment.domain.RecruitmentInfo;
import com.ruoyi.recruitment.service.IRecruitmentInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 招聘需求Controller
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/recruitment/recruitment")
public class RecruitmentInfoController extends BaseController
{
    @Autowired
    private IRecruitmentInfoService recruitmentInfoService;

    /**
     * 查询招聘需求列表
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:list')")
    @GetMapping("/list")
    public TableDataInfo list(RecruitmentInfo recruitmentInfo)
    {
        startPage();
        List<RecruitmentInfo> list = recruitmentInfoService.selectRecruitmentInfoList(recruitmentInfo);
        return getDataTable(list);
    }

    /**
     * 导出招聘需求列表
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:export')")
    @Log(title = "招聘需求", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RecruitmentInfo recruitmentInfo)
    {
        List<RecruitmentInfo> list = recruitmentInfoService.selectRecruitmentInfoList(recruitmentInfo);
        ExcelUtil<RecruitmentInfo> util = new ExcelUtil<RecruitmentInfo>(RecruitmentInfo.class);
        util.exportExcel(response, list, "招聘需求数据");
    }

    /**
     * 获取招聘需求详细信息
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(recruitmentInfoService.selectRecruitmentInfoById(id));
    }

    /**
     * 新增招聘需求
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:add')")
    @Log(title = "招聘需求", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RecruitmentInfo recruitmentInfo)
    {
        return toAjax(recruitmentInfoService.insertRecruitmentInfo(recruitmentInfo));
    }

    /**
     * 修改招聘需求
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:edit')")
    @Log(title = "招聘需求", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RecruitmentInfo recruitmentInfo)
    {
        return toAjax(recruitmentInfoService.updateRecruitmentInfo(recruitmentInfo));
    }

    /**
     * 删除招聘需求
     */
    @PreAuthorize("@ss.hasPermi('recruitment:recruitment:remove')")
    @Log(title = "招聘需求", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(recruitmentInfoService.deleteRecruitmentInfoByIds(ids));
    }
}
