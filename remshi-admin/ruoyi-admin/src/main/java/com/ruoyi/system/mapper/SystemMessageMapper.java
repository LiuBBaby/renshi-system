package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SystemMessage;

/**
 * 系统消息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-06-08
 */
public interface SystemMessageMapper 
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
     * 删除系统消息
     * 
     * @param messageId 系统消息主键
     * @return 结果
     */
    public int deleteSystemMessageByMessageId(Long messageId);

    /**
     * 批量删除系统消息
     * 
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSystemMessageByMessageIds(Long[] messageIds);
} 