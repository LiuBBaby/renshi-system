package com.ruoyi.attendance.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 月度考勤统计视图对象
 * 
 * @author ruoyi
 */
public class AttendanceMonthlyStatsVO extends BaseEntity
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

    /** 应出勤工作日天数 */
    @Excel(name = "应出勤工作日天数")
    private Integer workingDays;

    /** 实际出勤天数 */
    @Excel(name = "实际出勤天数")
    private Integer actualWorkDays;

    /** 正常出勤天数 */
    @Excel(name = "正常出勤天数")
    private Integer normalDays;
    
    /** 无效/缺勤天数 */
    @Excel(name = "缺勤天数")
    private Integer absenceDays;

    /** 请假天数 */
    @Excel(name = "请假天数")
    private Integer leaveDays;

    /** 迟到次数 */
    @Excel(name = "迟到次数")
    private Integer lateCount;

    /** 早退次数 */
    @Excel(name = "早退次数")
    private Integer earlyLeaveCount;

    /** 加班时长(小时) */
    @Excel(name = "加班时长(小时)")
    private Double overtimeHours;

    /** 出差天数 */
    @Excel(name = "出差天数")
    private Integer businessTripDays;

    /** 考勤得分 */
    @Excel(name = "考勤得分")
    private Double attendanceScore;
    
    /** 出勤率(%) */
    @Excel(name = "出勤率(%)")
    private Double attendanceRate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }

    public Integer getActualWorkDays() {
        return actualWorkDays;
    }

    public void setActualWorkDays(Integer actualWorkDays) {
        this.actualWorkDays = actualWorkDays;
    }

    public Integer getNormalDays() {
        return normalDays;
    }

    public void setNormalDays(Integer normalDays) {
        this.normalDays = normalDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public Integer getEarlyLeaveCount() {
        return earlyLeaveCount;
    }

    public void setEarlyLeaveCount(Integer earlyLeaveCount) {
        this.earlyLeaveCount = earlyLeaveCount;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Integer getBusinessTripDays() {
        return businessTripDays;
    }

    public void setBusinessTripDays(Integer businessTripDays) {
        this.businessTripDays = businessTripDays;
    }

    public Double getAttendanceScore() {
        return attendanceScore;
    }

    public void setAttendanceScore(Double attendanceScore) {
        this.attendanceScore = attendanceScore;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
    
    // 为兼容前端代码提供别名方法
    public Integer getWorkDays() {
        return workingDays;
    }
    
    public void setWorkDays(Integer workDays) {
        this.workingDays = workDays;
    }
    
    public Integer getLateTimes() {
        return lateCount;
    }
    
    public void setLateTimes(Integer lateTimes) {
        this.lateCount = lateTimes;
    }
    
    public Integer getEarlyTimes() {
        return earlyLeaveCount;
    }
    
    public void setEarlyTimes(Integer earlyTimes) {
        this.earlyLeaveCount = earlyTimes;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("empId", getEmpId())
            .append("empName", getEmpName())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("year", getYear())
            .append("month", getMonth())
            .append("workingDays", getWorkingDays())
            .append("actualWorkDays", getActualWorkDays())
            .append("normalDays", getNormalDays())
            .append("absenceDays", getAbsenceDays())
            .append("leaveDays", getLeaveDays())
            .append("lateCount", getLateCount())
            .append("earlyLeaveCount", getEarlyLeaveCount())
            .append("overtimeHours", getOvertimeHours())
            .append("businessTripDays", getBusinessTripDays())
            .append("attendanceScore", getAttendanceScore())
            .append("attendanceRate", getAttendanceRate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
