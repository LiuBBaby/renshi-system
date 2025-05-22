package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SystemMessage;

/**
 * 系统消息Service接口
 * 
 * @author ruoyi
 * @date 2023-06-08
 */
public interface ISystemMessageService 
{
    /**
     * 查询系统消息
     * 
     * @param messageId 系统消息主键
     * @return 系统消息
     */
    public SystemMessage selectSystemMessageByMessageId(Long messageId);

    /**
     * 查询系统消息列表
     * 
     * @param systemMessage 系统消息
     * @return 系统消息集合
     */
    public List<SystemMessage> selectSystemMessageList(SystemMessage systemMessage);

    /**
     * 查询最近的系统消息
     * 
     * @param limit 限制条数
     * @return 系统消息集合
     */
    public List<SystemMessage> selectLatestSystemMessages(int limit);

    /**
     * 新增系统消息
     * 
     * @param systemMessage 系统消息
     * @return 结果
     */
    public int insertSystemMessage(SystemMessage systemMessage);

    /**
     * 修改系统消息
     * 
     * @param systemMessage 系统消息
     * @return 结果
     */
    public int updateSystemMessage(SystemMessage systemMessage);

    /**
     * 批量删除系统消息
     * 
     * @param messageIds 需要删除的系统消息主键集合
     * @return 结果
     */
    public int deleteSystemMessageByMessageIds(Long[] messageIds);

    /**
     * 删除系统消息信息
     * 
     * @param messageId 系统消息主键
     * @return 结果
     */
    public int deleteSystemMessageByMessageId(Long messageId);
} 