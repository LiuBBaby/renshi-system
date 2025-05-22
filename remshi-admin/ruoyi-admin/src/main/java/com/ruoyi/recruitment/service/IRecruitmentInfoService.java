package com.ruoyi.recruitment.service;

import java.util.List;
import com.ruoyi.recruitment.domain.RecruitmentInfo;

/**
 * 招聘需求Service接口
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
public interface IRecruitmentInfoService 
{
    /**
     * 查询招聘需求
     * 
     * @param id 招聘需求主键
     * @return 招聘需求
     */
    public RecruitmentInfo selectRecruitmentInfoById(String id);

    /**
     * 查询招聘需求列表
     * 
     * @param recruitmentInfo 招聘需求
     * @return 招聘需求集合
     */
    public List<RecruitmentInfo> selectRecruitmentInfoList(RecruitmentInfo recruitmentInfo);

    /**
     * 新增招聘需求
     * 
     * @param recruitmentInfo 招聘需求
     * @return 结果
     */
    public int insertRecruitmentInfo(RecruitmentInfo recruitmentInfo);

    /**
     * 修改招聘需求
     * 
     * @param recruitmentInfo 招聘需求
     * @return 结果
     */
    public int updateRecruitmentInfo(RecruitmentInfo recruitmentInfo);

    /**
     * 批量删除招聘需求
     * 
     * @param ids 需要删除的招聘需求主键集合
     * @return 结果
     */
    public int deleteRecruitmentInfoByIds(String[] ids);

    /**
     * 删除招聘需求信息
     * 
     * @param id 招聘需求主键
     * @return 结果
     */
    public int deleteRecruitmentInfoById(String id);
}
