package com.ruoyi.interview.service;

import java.util.List;
import com.ruoyi.interview.domain.InterviewInfo;

/**
 * 面试计划Service接口
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
public interface IInterviewInfoService 
{
    /**
     * 查询面试计划
     * 
     * @param id 面试计划主键
     * @return 面试计划
     */
    public InterviewInfo selectInterviewInfoById(String id);

    /**
     * 查询面试计划列表
     * 
     * @param interviewInfo 面试计划
     * @return 面试计划集合
     */
    public List<InterviewInfo> selectInterviewInfoList(InterviewInfo interviewInfo);

    /**
     * 新增面试计划
     * 
     * @param interviewInfo 面试计划
     * @return 结果
     */
    public int insertInterviewInfo(InterviewInfo interviewInfo);

    /**
     * 修改面试计划
     * 
     * @param interviewInfo 面试计划
     * @return 结果
     */
    public int updateInterviewInfo(InterviewInfo interviewInfo);

    /**
     * 批量删除面试计划
     * 
     * @param ids 需要删除的面试计划主键集合
     * @return 结果
     */
    public int deleteInterviewInfoByIds(String[] ids);

    /**
     * 删除面试计划信息
     * 
     * @param id 面试计划主键
     * @return 结果
     */
    public int deleteInterviewInfoById(String id);
}
