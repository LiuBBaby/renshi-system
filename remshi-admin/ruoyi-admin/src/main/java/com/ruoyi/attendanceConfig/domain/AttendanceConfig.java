package com.ruoyi.attendanceConfig.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考勤规则字典对象 attendance_config
 * 
 * @author 苏天霖
 * @date 2025-03-30
 */
public class AttendanceConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 参数名 */
    @Excel(name = "参数名")
    private String paramKey;

    /** 参数值 */
    @Excel(name = "参数值")
    private String paramValue;

    /** 状态 */
    private Long status;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setParamKey(String paramKey) 
    {
        this.paramKey = paramKey;
    }

    public String getParamKey() 
    {
        return paramKey;
    }

    public void setParamValue(String paramValue) 
    {
        this.paramValue = paramValue;
    }

    public String getParamValue() 
    {
        return paramValue;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("paramKey", getParamKey())
            .append("paramValue", getParamValue())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
