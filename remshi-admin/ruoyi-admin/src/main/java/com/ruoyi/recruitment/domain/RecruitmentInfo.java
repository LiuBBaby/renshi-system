package com.ruoyi.recruitment.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 招聘需求对象 recruitment_info
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
public class RecruitmentInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private String id;

    /** 需求人数 */
    @Excel(name = "需求人数")
    private Long recruitmentInfoNumber;

    /** 招聘岗位 */
    @Excel(name = "招聘岗位")
    private String recruitmentInfoPost;

    /** 学历要求（0专1本2研究3其他） */
    @Excel(name = "学历要求", readConverterExp = "0=专1本2研究3其他")
    private Long recruitmentInfoDegree;

    /** 工作年限 */
    @Excel(name = "工作年限")
    private Long recruitmentInfoDuration;

    /** 招聘需求 */
    @Excel(name = "招聘需求")
    private String recruitmentInfoDemand;

    /** 负责人工号 */
    @Excel(name = "负责人工号")
    private String recruitmentInfoId;

    /** 招聘负责人 */
    @Excel(name = "招聘负责人")
    private String recruitmentInfoResponsible;

    /** 招聘状态（0进行中1已完成） */
    @Excel(name = "招聘状态", readConverterExp = "0=进行中1已完成")
    private Long recruitmentInfoStatus;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setRecruitmentInfoNumber(Long recruitmentInfoNumber) 
    {
        this.recruitmentInfoNumber = recruitmentInfoNumber;
    }

    public Long getRecruitmentInfoNumber() 
    {
        return recruitmentInfoNumber;
    }

    public void setRecruitmentInfoPost(String recruitmentInfoPost) 
    {
        this.recruitmentInfoPost = recruitmentInfoPost;
    }

    public String getRecruitmentInfoPost() 
    {
        return recruitmentInfoPost;
    }

    public void setRecruitmentInfoDegree(Long recruitmentInfoDegree) 
    {
        this.recruitmentInfoDegree = recruitmentInfoDegree;
    }

    public Long getRecruitmentInfoDegree() 
    {
        return recruitmentInfoDegree;
    }

    public void setRecruitmentInfoDuration(Long recruitmentInfoDuration) 
    {
        this.recruitmentInfoDuration = recruitmentInfoDuration;
    }

    public Long getRecruitmentInfoDuration() 
    {
        return recruitmentInfoDuration;
    }

    public void setRecruitmentInfoDemand(String recruitmentInfoDemand) 
    {
        this.recruitmentInfoDemand = recruitmentInfoDemand;
    }

    public String getRecruitmentInfoDemand() 
    {
        return recruitmentInfoDemand;
    }

    public void setRecruitmentInfoId(String recruitmentInfoId) 
    {
        this.recruitmentInfoId = recruitmentInfoId;
    }

    public String getRecruitmentInfoId() 
    {
        return recruitmentInfoId;
    }

    public void setRecruitmentInfoResponsible(String recruitmentInfoResponsible) 
    {
        this.recruitmentInfoResponsible = recruitmentInfoResponsible;
    }

    public String getRecruitmentInfoResponsible() 
    {
        return recruitmentInfoResponsible;
    }

    public void setRecruitmentInfoStatus(Long recruitmentInfoStatus) 
    {
        this.recruitmentInfoStatus = recruitmentInfoStatus;
    }

    public Long getRecruitmentInfoStatus() 
    {
        return recruitmentInfoStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("recruitmentInfoNumber", getRecruitmentInfoNumber())
            .append("recruitmentInfoPost", getRecruitmentInfoPost())
            .append("recruitmentInfoDegree", getRecruitmentInfoDegree())
            .append("recruitmentInfoDuration", getRecruitmentInfoDuration())
            .append("recruitmentInfoDemand", getRecruitmentInfoDemand())
            .append("recruitmentInfoId", getRecruitmentInfoId())
            .append("recruitmentInfoResponsible", getRecruitmentInfoResponsible())
            .append("recruitmentInfoStatus", getRecruitmentInfoStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
