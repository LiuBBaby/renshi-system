package com.ruoyi.interview.mapper;

import java.util.List;
import com.ruoyi.interview.domain.InterviewInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 面试计划Mapper接口
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
@Mapper
public interface InterviewInfoMapper 
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
     * 删除面试计划
     * 
     * @param id 面试计划主键
     * @return 结果
     */
    public int deleteInterviewInfoById(String id);

    /**
     * 批量删除面试计划
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInterviewInfoByIds(String[] ids);
    
    /**
     * 根据员工ID删除面试计划记录
     *
     * @param interviewInfoNumber 员工ID
     * @return 结果
     */
    public int deleteInterviewInfoByEmpId(String interviewInfoNumber);
}
