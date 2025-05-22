package com.ruoyi.interview.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 面试计划对象 interview_info
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
public class InterviewInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private String id;

    /** 面试人 */
    @Excel(name = "面试人")
    private String interviewInfoName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private Long interviewInfoPhone;

    /** 面试时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "面试时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date interviewInfoTime;

    /** 面试官 */
    @Excel(name = "面试官")
    private String interviewInfoPeople;

    /** 面试官工号 */
    private String interviewInfoNumber;

    /** 面试结果 */
    @Excel(name = "面试结果")
    private String interviewInfoStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creatTime;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setInterviewInfoName(String interviewInfoName) 
    {
        this.interviewInfoName = interviewInfoName;
    }

    public String getInterviewInfoName() 
    {
        return interviewInfoName;
    }

    public void setInterviewInfoPhone(Long interviewInfoPhone) 
    {
        this.interviewInfoPhone = interviewInfoPhone;
    }

    public Long getInterviewInfoPhone() 
    {
        return interviewInfoPhone;
    }

    public void setInterviewInfoTime(Date interviewInfoTime) 
    {
        this.interviewInfoTime = interviewInfoTime;
    }

    public Date getInterviewInfoTime() 
    {
        return interviewInfoTime;
    }

    public void setInterviewInfoPeople(String interviewInfoPeople) 
    {
        this.interviewInfoPeople = interviewInfoPeople;
    }

    public String getInterviewInfoPeople() 
    {
        return interviewInfoPeople;
    }

    public void setInterviewInfoNumber(String interviewInfoNumber) 
    {
        this.interviewInfoNumber = interviewInfoNumber;
    }

    public String getInterviewInfoNumber() 
    {
        return interviewInfoNumber;
    }

    public void setInterviewInfoStatus(String interviewInfoStatus) 
    {
        this.interviewInfoStatus = interviewInfoStatus;
    }

    public String getInterviewInfoStatus() 
    {
        return interviewInfoStatus;
    }

    public void setCreatTime(Date creatTime) 
    {
        this.creatTime = creatTime;
    }

    public Date getCreatTime() 
    {
        return creatTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("interviewInfoName", getInterviewInfoName())
            .append("interviewInfoPhone", getInterviewInfoPhone())
            .append("interviewInfoTime", getInterviewInfoTime())
            .append("interviewInfoPeople", getInterviewInfoPeople())
            .append("interviewInfoNumber", getInterviewInfoNumber())
            .append("interviewInfoStatus", getInterviewInfoStatus())
            .append("creatTime", getCreatTime())
            .toString();
    }
}
