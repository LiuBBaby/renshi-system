package com.ruoyi.attendance.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考勤管理对象 attendance_info
 *
 * @author ruoyi
 * @date 2025-03-30
 */
public class AttendanceInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String attendanceInfoId;

    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String attendanceInfoName;

    /** 员工id */
    @Excel(name = "员工id")
    private String empId;

    /** 上班时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上班时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    /** 下班时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下班时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;

    /** 上班打卡状态 (0-正常, 1-迟到, 3-缺勤) */
    @Excel(name = "上班打卡状态", readConverterExp = "0=正常,1=迟到,3=缺勤")
    private Long checkInStatus;
    
    /** 下班打卡状态 (0-正常, 2-早退, 3-缺勤) */
    @Excel(name = "下班打卡状态", readConverterExp = "0=正常,2=早退,3=缺勤")
    private Long checkOutStatus;

    /** 考勤日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "考勤日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date attendanceInfoDate;

    /** 员工评价 */
    @Excel(name = "员工评价")
    private Long evaluate;
    
    /** 上班打卡经度 */
    @Excel(name = "上班打卡经度")
    private BigDecimal goLongitude;
    
    /** 上班打卡纬度 */
    @Excel(name = "上班打卡纬度")
    private BigDecimal goLatitude;
    
    /** 上班打卡地址 */
    @Excel(name = "上班打卡地址")
    private String goAddress;
    
    /** 下班打卡经度 */
    @Excel(name = "下班打卡经度")
    private BigDecimal outLongitude;
    
    /** 下班打卡纬度 */
    @Excel(name = "下班打卡纬度")
    private BigDecimal outLatitude;
    
    /** 下班打卡地址 */
    @Excel(name = "下班打卡地址")
    private String outAddress;

    public void setAttendanceInfoId(String attendanceInfoId)
    {
        this.attendanceInfoId = attendanceInfoId;
    }

    public String getAttendanceInfoId()
    {
        return attendanceInfoId;
    }

    public void setAttendanceInfoName(String attendanceInfoName)
    {
        this.attendanceInfoName = attendanceInfoName;
    }

    public String getAttendanceInfoName()
    {
        return attendanceInfoName;
    }

    public void setEmpId(String empId)
    {
        this.empId = empId;
    }

    public String getEmpId()
    {
        return empId;
    }

    public void setCheckInTime(LocalDateTime checkInTime)
    {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckInTime()
    {
        return checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime)
    {
        this.checkOutTime = checkOutTime;
    }

    public LocalDateTime getCheckOutTime()
    {
        return checkOutTime;
    }

    public void setCheckInStatus(Long checkInStatus)
    {
        this.checkInStatus = checkInStatus;
    }

    public Long getCheckInStatus()
    {
        return checkInStatus;
    }
    
    public void setCheckOutStatus(Long checkOutStatus)
    {
        this.checkOutStatus = checkOutStatus;
    }

    public Long getCheckOutStatus()
    {
        return checkOutStatus;
    }

    public void setAttendanceInfoDate(Date attendanceInfoDate)
    {
        this.attendanceInfoDate = attendanceInfoDate;
    }

    public Date getAttendanceInfoDate()
    {
        return attendanceInfoDate;
    }

    public void setEvaluate(Long evaluate)
    {
        this.evaluate = evaluate;
    }

    public Long getEvaluate()
    {
        return evaluate;
    }

    public BigDecimal getGoLongitude() {
        return goLongitude;
    }

    public void setGoLongitude(BigDecimal goLongitude) {
        this.goLongitude = goLongitude;
    }

    public BigDecimal getGoLatitude() {
        return goLatitude;
    }

    public void setGoLatitude(BigDecimal goLatitude) {
        this.goLatitude = goLatitude;
    }

    public String getGoAddress() {
        return goAddress;
    }

    public void setGoAddress(String goAddress) {
        this.goAddress = goAddress;
    }

    public BigDecimal getOutLongitude() {
        return outLongitude;
    }

    public void setOutLongitude(BigDecimal outLongitude) {
        this.outLongitude = outLongitude;
    }

    public BigDecimal getOutLatitude() {
        return outLatitude;
    }

    public void setOutLatitude(BigDecimal outLatitude) {
        this.outLatitude = outLatitude;
    }

    public String getOutAddress() {
        return outAddress;
    }

    public void setOutAddress(String outAddress) {
        this.outAddress = outAddress;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("attendanceInfoId", getAttendanceInfoId())
                .append("attendanceInfoName", getAttendanceInfoName())
                .append("empId", getEmpId())
                .append("checkInTime", getCheckInTime())
                .append("checkOutTime", getCheckOutTime())
                .append("checkInStatus", getCheckInStatus())
                .append("checkOutStatus", getCheckOutStatus())
                .append("attendanceInfoDate", getAttendanceInfoDate())
                .append("createTime", getCreateTime())
                .append("evaluate", getEvaluate())
                .append("goLongitude", getGoLongitude())
                .append("goLatitude", getGoLatitude())
                .append("goAddress", getGoAddress())
                .append("outLongitude", getOutLongitude())
                .append("outLatitude", getOutLatitude())
                .append("outAddress", getOutAddress())
                .toString();
    }
    
    /**
     * 兼容旧版本的获取方法
     */
    public LocalDateTime getAttendanceInfoGo() {
        return this.checkInTime;
    }
    
    /**
     * 兼容旧版本的设置方法
     */
    public void setAttendanceInfoGo(LocalDateTime attendanceInfoGo) {
        this.checkInTime = attendanceInfoGo;
    }
    
    /**
     * 兼容旧版本的获取方法
     */
    public LocalDateTime getAttendanceInfoOut() {
        return this.checkOutTime;
    }
    
    /**
     * 兼容旧版本的设置方法
     */
    public void setAttendanceInfoOut(LocalDateTime attendanceInfoOut) {
        this.checkOutTime = attendanceInfoOut;
    }
    
    /**
     * 兼容旧版本的获取方法
     */
    public Long getAttendanceInfoStatus() {
        // 优先返回较严重的状态
        if (this.checkInStatus != null && this.checkOutStatus != null) {
            if (this.checkInStatus == 3 || this.checkOutStatus == 3) {
                return 3L; // 缺勤
            } else if (this.checkInStatus == 1) {
                return 1L; // 迟到
            } else if (this.checkOutStatus == 2) {
                return 2L; // 早退
            } else {
                return 0L; // 正常
            }
        } else if (this.checkInStatus != null) {
            return this.checkInStatus;
        } else if (this.checkOutStatus != null) {
            return this.checkOutStatus;
        }
        return null;
    }
    
    /**
     * 兼容旧版本的设置方法
     */
    public void setAttendanceInfoStatus(Long attendanceInfoStatus) {
        // 根据总状态设置分状态
        if (attendanceInfoStatus != null) {
            if (this.checkInTime != null && this.checkOutTime == null) {
                // 只有上班打卡时
                this.checkInStatus = attendanceInfoStatus;
            } else if (this.checkOutTime != null && this.checkInTime == null) {
                // 只有下班打卡时
                this.checkOutStatus = attendanceInfoStatus;
            } else {
                // 都有或都没有时，设置两个状态相同
                this.checkInStatus = attendanceInfoStatus;
                this.checkOutStatus = attendanceInfoStatus;
            }
        }
    }
}

