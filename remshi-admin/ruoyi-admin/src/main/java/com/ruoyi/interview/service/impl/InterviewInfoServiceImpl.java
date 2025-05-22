package com.ruoyi.interview.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.interview.mapper.InterviewInfoMapper;
import com.ruoyi.interview.domain.InterviewInfo;
import com.ruoyi.interview.service.IInterviewInfoService;

/**
 * 面试计划Service业务层处理
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
@Service
public class InterviewInfoServiceImpl implements IInterviewInfoService 
{
    @Autowired
    private InterviewInfoMapper interviewInfoMapper;

    /**
     * 查询面试计划
     * 
     * @param id 面试计划主键
     * @return 面试计划
     */
    @Override
    public InterviewInfo selectInterviewInfoById(String id)
    {
        return interviewInfoMapper.selectInterviewInfoById(id);
    }

    /**
     * 查询面试计划列表
     * 
     * @param interviewInfo 面试计划
     * @return 面试计划
     */
    @Override
    public List<InterviewInfo> selectInterviewInfoList(InterviewInfo interviewInfo)
    {
        return interviewInfoMapper.selectInterviewInfoList(interviewInfo);
    }

    /**
     * 新增面试计划
     * 
     * @param interviewInfo 面试计划
     * @return 结果
     */
    @Override
    public int insertInterviewInfo(InterviewInfo interviewInfo)
    {
        return interviewInfoMapper.insertInterviewInfo(interviewInfo);
    }

    /**
     * 修改面试计划
     * 
     * @param interviewInfo 面试计划
     * @return 结果
     */
    @Override
    public int updateInterviewInfo(InterviewInfo interviewInfo)
    {
        return interviewInfoMapper.updateInterviewInfo(interviewInfo);
    }

    /**
     * 批量删除面试计划
     * 
     * @param ids 需要删除的面试计划主键
     * @return 结果
     */
    @Override
    public int deleteInterviewInfoByIds(String[] ids)
    {
        return interviewInfoMapper.deleteInterviewInfoByIds(ids);
    }

    /**
     * 删除面试计划信息
     * 
     * @param id 面试计划主键
     * @return 结果
     */
    @Override
    public int deleteInterviewInfoById(String id)
    {
        return interviewInfoMapper.deleteInterviewInfoById(id);
    }
}
