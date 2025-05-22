package com.ruoyi.web.controller.system;

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
import com.ruoyi.system.domain.SystemMessage;
import com.ruoyi.system.service.ISystemMessageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 系统消息Controller
 * 
 * @author ruoyi
 * @date 2023-06-08
 */
@RestController
@RequestMapping("/system/message")
public class SystemMessageController extends BaseController
{
    @Autowired
    private ISystemMessageService systemMessageService;

    /**
     * 查询系统消息列表
     */
    @PreAuthorize("@ss.hasPermi('system:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(SystemMessage systemMessage)
    {
        startPage();
        List<SystemMessage> list = systemMessageService.selectSystemMessageList(systemMessage);
        return getDataTable(list);
    }

    /**
     * 导出系统消息列表
     */
    @PreAuthorize("@ss.hasPermi('system:message:export')")
    @Log(title = "系统消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SystemMessage systemMessage)
    {
        List<SystemMessage> list = systemMessageService.selectSystemMessageList(systemMessage);
        ExcelUtil<SystemMessage> util = new ExcelUtil<SystemMessage>(SystemMessage.class);
        util.exportExcel(response, list, "系统消息数据");
    }

    /**
     * 获取系统消息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:message:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(systemMessageService.selectSystemMessageByMessageId(messageId));
    }

    /**
     * 新增系统消息
     */
    @PreAuthorize("@ss.hasPermi('system:message:add')")
    @Log(title = "系统消息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SystemMessage systemMessage)
    {
        return toAjax(systemMessageService.insertSystemMessage(systemMessage));
    }

    /**
     * 修改系统消息
     */
    @PreAuthorize("@ss.hasPermi('system:message:edit')")
    @Log(title = "系统消息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SystemMessage systemMessage)
    {
        return toAjax(systemMessageService.updateSystemMessage(systemMessage));
    }

    /**
     * 删除系统消息
     */
    @PreAuthorize("@ss.hasPermi('system:message:remove')")
    @Log(title = "系统消息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds)
    {
        return toAjax(systemMessageService.deleteSystemMessageByMessageIds(messageIds));
    }
    
    /**
     * 获取最近的系统消息
     */
    @GetMapping("/latest/{limit}")
    public AjaxResult getLatestMessages(@PathVariable("limit") int limit)
    {
        List<SystemMessage> messages = systemMessageService.selectLatestSystemMessages(limit);
        return success(messages);
    }
} 