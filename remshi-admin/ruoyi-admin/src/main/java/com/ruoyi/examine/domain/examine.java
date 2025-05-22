package com.ruoyi.examine.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 请假审批对象 examine_info
 * 
 * @author 苏天霖
 * @date 2025-03-31
 */
public class examine extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long id;

    /** 请假开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "请假开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date examineInfoDateBegin;

    /** 请假结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "请假结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date examineInfoDateEnd;

    /** 申请人工号 */
    @Excel(name = "申请人工号")
    private String examineInfoId;

    /** 请假类型 */
    @Excel(name = "请假类型")
    private String leaveType;

    /** 申请人 */
    private String examineInfoName;

    /** 申请人部门 */
    private String examineInfoDept;

    /** 请假原因 */
    @Excel(name = "请假原因")
    private String examineInfoReason;

    /** 审批人 */
    private String examineInfoPeople;

    /** 审批结果 (0一级审批中1二级审批中2通过3未通过4已撤销) */
    @Excel(name = "审批结果")
    private Long examineInfoResult;
    
    /** 一级审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "一级审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstApprovalTime;
    
    /** 最终审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最终审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;
    
    /** 关联用户ID */
    private Long userId;
    
    /** 备注 */
    private String remark;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setExamineInfoDateBegin(Date examineInfoDateBegin) 
    {
        this.examineInfoDateBegin = examineInfoDateBegin;
    }

    public Date getExamineInfoDateBegin() 
    {
        return examineInfoDateBegin;
    }

    public void setExamineInfoDateEnd(Date examineInfoDateEnd) 
    {
        this.examineInfoDateEnd = examineInfoDateEnd;
    }

    public Date getExamineInfoDateEnd() 
    {
        return examineInfoDateEnd;
    }

    public void setExamineInfoId(String examineInfoId) 
    {
        this.examineInfoId = examineInfoId;
    }

    public String getExamineInfoId() 
    {
        return examineInfoId;
    }

    public void setLeaveType(String leaveType) 
    {
        this.leaveType = leaveType;
    }

    public String getLeaveType() 
    {
        return leaveType;
    }

    public void setExamineInfoName(String examineInfoName) 
    {
        this.examineInfoName = examineInfoName;
    }

    public String getExamineInfoName() 
    {
        return examineInfoName;
    }

    public void setExamineInfoDept(String examineInfoDept) 
    {
        this.examineInfoDept = examineInfoDept;
    }

    public String getExamineInfoDept() 
    {
        return examineInfoDept;
    }

    public void setExamineInfoReason(String examineInfoReason) 
    {
        this.examineInfoReason = examineInfoReason;
    }

    public String getExamineInfoReason() 
    {
        return examineInfoReason;
    }

    public void setExamineInfoPeople(String examineInfoPeople) 
    {
        this.examineInfoPeople = examineInfoPeople;
    }

    public String getExamineInfoPeople() 
    {
        return examineInfoPeople;
    }

    public void setExamineInfoResult(Long examineInfoResult) 
    {
        this.examineInfoResult = examineInfoResult;
    }

    public Long getExamineInfoResult() 
    {
        return examineInfoResult;
    }
    
    public void setFirstApprovalTime(Date firstApprovalTime)
    {
        this.firstApprovalTime = firstApprovalTime;
    }

    public Date getFirstApprovalTime()
    {
        return firstApprovalTime;
    }
    
    public void setApprovalTime(Date approvalTime)
    {
        this.approvalTime = approvalTime;
    }

    public Date getApprovalTime()
    {
        return approvalTime;
    }
    
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    
    @Override
    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public String getRemark()
    {
        return remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("examineInfoDateBegin", getExamineInfoDateBegin())
            .append("examineInfoDateEnd", getExamineInfoDateEnd())
            .append("examineInfoId", getExamineInfoId())
            .append("leaveType", getLeaveType())
            .append("examineInfoName", getExamineInfoName())
            .append("examineInfoDept", getExamineInfoDept())
            .append("examineInfoReason", getExamineInfoReason())
            .append("examineInfoPeople", getExamineInfoPeople())
            .append("examineInfoResult", getExamineInfoResult())
            .append("createTime", getCreateTime())
            .append("firstApprovalTime", getFirstApprovalTime())
            .append("approvalTime", getApprovalTime())
            .append("userId", getUserId())
            .append("remark", getRemark())
            .toString();
    }
}
