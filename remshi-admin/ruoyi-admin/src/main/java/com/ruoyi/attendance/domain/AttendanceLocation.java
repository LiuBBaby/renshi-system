package com.ruoyi.attendance.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考勤打卡位置对象 attendance_location
 * 
 * @author ruoyi
 */
public class AttendanceLocation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 位置名称 */
    @Excel(name = "位置名称")
    private String locationName;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 打卡有效半径(米) */
    @Excel(name = "打卡有效半径(米)")
    private Integer radius;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 状态(0有效 1无效) */
    @Excel(name = "状态", readConverterExp = "0=有效,1=无效")
    private String status;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    
    public void setLocationName(String locationName) 
    {
        this.locationName = locationName;
    }

    public String getLocationName() 
    {
        return locationName;
    }
    
    public void setLongitude(BigDecimal longitude) 
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() 
    {
        return longitude;
    }
    
    public void setLatitude(BigDecimal latitude) 
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() 
    {
        return latitude;
    }
    
    public void setRadius(Integer radius) 
    {
        this.radius = radius;
    }

    public Integer getRadius() 
    {
        return radius;
    }
    
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("locationName", getLocationName())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("radius", getRadius())
            .append("address", getAddress())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 