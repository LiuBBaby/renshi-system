package com.ruoyi.attendance.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考勤月报对象 attendance_monthly
 * 
 * @author ruoyi
 */
public class AttendanceMonthly extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 考勤月报ID */
    private Integer id;

    /** 员工ID */
    @Excel(name = "员工ID")
    private String empId;

    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String empName;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 年份 */
    @Excel(name = "年份")
    private Integer year;

    /** 月份 */
    @Excel(name = "月份")
    private Integer month;

    /** 应出勤天数 */
    @Excel(name = "应出勤天数")
    private Integer workDays;

    /** 实际出勤天数 */
    @Excel(name = "实际出勤天数")
    private Integer actualWorkDays;

    /** 缺勤天数 */
    @Excel(name = "缺勤天数")
    private Integer absentDays;

    /** 请假天数 */
    @Excel(name = "请假天数")
    private Integer leaveDays;

    /** 迟到次数 */
    @Excel(name = "迟到次数")
    private Integer lateTimes;

    /** 早退次数 */
    @Excel(name = "早退次数")
    private Integer earlyTimes;

    /** 加班时长(小时) */
    @Excel(name = "加班时长(小时)")
    private Double overtimeHours;

    /** 出差天数 */
    @Excel(name = "出差天数")
    private Integer businessTripDays;

    /** 考勤得分 */
    @Excel(name = "考勤得分")
    private Double attendanceScore;

    private Integer normalDays; // 正常出勤天数

    public Integer getNormalDays() {
        return normalDays;
    }

    public void setNormalDays(Integer normalDays) {
        this.normalDays = normalDays;
    }

    private Integer lateCount; // 迟到次数，与lateTimes字段同义

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
        // 同步更新lateTimes字段以保持一致性
        this.lateTimes = lateCount;
    }

    private Integer earlyLeaveCount; // 早退次数，与earlyTimes字段同义

    public Integer getEarlyLeaveCount() {
        return earlyLeaveCount;
    }

    public void setEarlyLeaveCount(Integer earlyLeaveCount) {
        this.earlyLeaveCount = earlyLeaveCount;
        // 同步更新earlyTimes字段以保持一致性
        this.earlyTimes = earlyLeaveCount;
    }

    private Integer absenceDays; // 无效天数，与absentDays字段同义

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
        // 同步更新absentDays字段以保持一致性
        this.absentDays = absenceDays;
    }

    private Integer workingDays; // 应出勤工作日天数，与workDays字段同义

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
        // 同步更新workDays字段以保持一致性
        this.workDays = workingDays;
    }

    private Double attendanceRate; // 出勤率(%)

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }


    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    
    public void setEmpId(String empId) 
    {
        this.empId = empId;
    }

    public String getEmpId() 
    {
        return empId;
    }
    
    public void setEmpName(String empName) 
    {
        this.empName = empName;
    }

    public String getEmpName() 
    {
        return empName;
    }
    
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    
    public void setYear(Integer year) 
    {
        this.year = year;
    }

    public Integer getYear() 
    {
        return year;
    }
    
    public void setMonth(Integer month) 
    {
        this.month = month;
    }

    public Integer getMonth() 
    {
        return month;
    }
    
    public void setWorkDays(Integer workDays) 
    {
        this.workDays = workDays;
    }

    public Integer getWorkDays() 
    {
        return workDays;
    }
    
    public void setActualWorkDays(Integer actualWorkDays) 
    {
        this.actualWorkDays = actualWorkDays;
    }

    public Integer getActualWorkDays() 
    {
        return actualWorkDays;
    }
    
    public void setAbsentDays(Integer absentDays) 
    {
        this.absentDays = absentDays;
    }

    public Integer getAbsentDays() 
    {
        return absentDays;
    }
    
    public void setLeaveDays(Integer leaveDays) 
    {
        this.leaveDays = leaveDays;
    }

    public Integer getLeaveDays() 
    {
        return leaveDays;
    }
    
    public void setLateTimes(Integer lateTimes) 
    {
        this.lateTimes = lateTimes;
    }

    public Integer getLateTimes() 
    {
        return lateTimes;
    }
    
    public void setEarlyTimes(Integer earlyTimes) 
    {
        this.earlyTimes = earlyTimes;
    }

    public Integer getEarlyTimes() 
    {
        return earlyTimes;
    }
    
    public void setOvertimeHours(Double overtimeHours) 
    {
        this.overtimeHours = overtimeHours;
    }

    public Double getOvertimeHours() 
    {
        return overtimeHours;
    }
    
    public void setBusinessTripDays(Integer businessTripDays) 
    {
        this.businessTripDays = businessTripDays;
    }

    public Integer getBusinessTripDays() 
    {
        return businessTripDays;
    }
    
    public void setAttendanceScore(Double attendanceScore) 
    {
        this.attendanceScore = attendanceScore;
    }

    public Double getAttendanceScore() 
    {
        return attendanceScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("empId", getEmpId())
            .append("empName", getEmpName())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("year", getYear())
            .append("month", getMonth())
            .append("workDays", getWorkDays())
            .append("actualWorkDays", getActualWorkDays())
            .append("absentDays", getAbsentDays())
            .append("leaveDays", getLeaveDays())
            .append("lateTimes", getLateTimes())
            .append("earlyTimes", getEarlyTimes())
            .append("overtimeHours", getOvertimeHours())
            .append("businessTripDays", getBusinessTripDays())
            .append("attendanceScore", getAttendanceScore())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
} 
