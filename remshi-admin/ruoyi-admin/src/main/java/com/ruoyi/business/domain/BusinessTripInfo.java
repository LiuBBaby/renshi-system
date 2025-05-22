package com.ruoyi.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 出差申请对象 business_trip_info
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public class BusinessTripInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Integer id;

    /** 出差开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出差开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date tripInfoDateBegin;

    /** 出差结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出差结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date tripInfoDateEnd;

    /** 申请人工号 */
    @Excel(name = "申请人工号")
    private String tripInfoId;

    /** 申请人 */
    @Excel(name = "申请人")
    private String tripInfoName;

    /** 申请人部门 */
    @Excel(name = "申请人部门")
    private String tripInfoDept;

    /** 出差目的地 */
    @Excel(name = "出差目的地")
    private String tripInfoDestination;

    /** 出差原因 */
    @Excel(name = "出差原因")
    private String tripInfoReason;

    /** 审批人 */
    @Excel(name = "审批人")
    private String tripInfoPeople;

    /** 审批结果 (0一级审批中1二级审批中2通过3未通过4已撤销) */
    @Excel(name = "审批结果", readConverterExp = "0=一级审批中,1=二级审批中,2=通过,3=未通过,4=已撤销")
    private Integer tripInfoResult;

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

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }

    public void setTripInfoDateBegin(Date tripInfoDateBegin) 
    {
        this.tripInfoDateBegin = tripInfoDateBegin;
    }

    public Date getTripInfoDateBegin() 
    {
        return tripInfoDateBegin;
    }

    public void setTripInfoDateEnd(Date tripInfoDateEnd) 
    {
        this.tripInfoDateEnd = tripInfoDateEnd;
    }

    public Date getTripInfoDateEnd() 
    {
        return tripInfoDateEnd;
    }

    public void setTripInfoId(String tripInfoId) 
    {
        this.tripInfoId = tripInfoId;
    }

    public String getTripInfoId() 
    {
        return tripInfoId;
    }

    public void setTripInfoName(String tripInfoName) 
    {
        this.tripInfoName = tripInfoName;
    }

    public String getTripInfoName() 
    {
        return tripInfoName;
    }

    public void setTripInfoDept(String tripInfoDept) 
    {
        this.tripInfoDept = tripInfoDept;
    }

    public String getTripInfoDept() 
    {
        return tripInfoDept;
    }

    public void setTripInfoDestination(String tripInfoDestination) 
    {
        this.tripInfoDestination = tripInfoDestination;
    }

    public String getTripInfoDestination() 
    {
        return tripInfoDestination;
    }

    public void setTripInfoReason(String tripInfoReason) 
    {
        this.tripInfoReason = tripInfoReason;
    }

    public String getTripInfoReason() 
    {
        return tripInfoReason;
    }

    public void setTripInfoPeople(String tripInfoPeople) 
    {
        this.tripInfoPeople = tripInfoPeople;
    }

    public String getTripInfoPeople() 
    {
        return tripInfoPeople;
    }

    public void setTripInfoResult(Integer tripInfoResult) 
    {
        this.tripInfoResult = tripInfoResult;
    }

    public Integer getTripInfoResult() 
    {
        return tripInfoResult;
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

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("tripInfoDateBegin", getTripInfoDateBegin())
            .append("tripInfoDateEnd", getTripInfoDateEnd())
            .append("tripInfoId", getTripInfoId())
            .append("tripInfoName", getTripInfoName())
            .append("tripInfoDept", getTripInfoDept())
            .append("tripInfoDestination", getTripInfoDestination())
            .append("tripInfoReason", getTripInfoReason())
            .append("tripInfoPeople", getTripInfoPeople())
            .append("tripInfoResult", getTripInfoResult())
            .append("createTime", getCreateTime())
            .append("firstApprovalTime", getFirstApprovalTime())
            .append("approvalTime", getApprovalTime())
            .append("userId", getUserId())
            .append("remark", getRemark())
            .toString();
    }
}