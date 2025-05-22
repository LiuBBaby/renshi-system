package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SystemMessageMapper;
import com.ruoyi.system.domain.SystemMessage;
import com.ruoyi.system.service.ISystemMessageService;

/**
 * 系统消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-06-08
 */
@Service
public class SystemMessageServiceImpl implements ISystemMessageService 
{
    @Autowired
    private SystemMessageMapper systemMessageMapper;

    /**
     * 查询系统消息
     * 
     * @param messageId 系统消息主键
     * @return 系统消息
     */
    @Override
    public SystemMessage selectSystemMessageByMessageId(Long messageId)
    {
        return systemMessageMapper.selectSystemMessageByMessageId(messageId);
    }

    /**
     * 查询系统消息列表
     * 
     * @param systemMessage 系统消息
     * @return 系统消息
     */
    @Override
    public List<SystemMessage> selectSystemMessageList(SystemMessage systemMessage)
    {
        return systemMessageMapper.selectSystemMessageList(systemMessage);
    }

    /**
     * 查询最近的系统消息
     * 
     * @param limit 限制条数
     * @return 系统消息集合
     */
    @Override
    public List<SystemMessage> selectLatestSystemMessages(int limit)
    {
        return systemMessageMapper.selectLatestSystemMessages(limit);
    }

    /**
     * 新增系统消息
     * 
     * @param systemMessage 系统消息
     * @return 结果
     */
    @Override
    public int insertSystemMessage(SystemMessage systemMessage)
    {
        return systemMessageMapper.insertSystemMessage(systemMessage);
    }

    /**
     * 修改系统消息
     * 
     * @param systemMessage 系统消息
     * @return 结果
     */
    @Override
    public int updateSystemMessage(SystemMessage systemMessage)
    {
        return systemMessageMapper.updateSystemMessage(systemMessage);
    }

    /**
     * 批量删除系统消息
     * 
     * @param messageIds 需要删除的系统消息主键
     * @return 结果
     */
    @Override
    public int deleteSystemMessageByMessageIds(Long[] messageIds)
    {
        return systemMessageMapper.deleteSystemMessageByMessageIds(messageIds);
    }

    /**
     * 删除系统消息信息
     * 
     * @param messageId 系统消息主键
     * @return 结果
     */
    @Override
    public int deleteSystemMessageByMessageId(Long messageId)
    {
        return systemMessageMapper.deleteSystemMessageByMessageId(messageId);
    }
} 