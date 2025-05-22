package com.ruoyi.salary.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 月度薪资报表对象 salary_monthly
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public class SalaryMonthly extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Integer id;

    /** 员工工号 */
    @Excel(name = "员工工号")
    private String empId;

    /** 年份 */
    @Excel(name = "年份")
    private Integer year;

    /** 月份 */
    @Excel(name = "月份")
    private Integer month;

    /** 基本工资 */
    @Excel(name = "基本工资")
    private BigDecimal baseSalary;

    /** 实际出勤天数 */
    @Excel(name = "实际出勤天数")
    private Integer actualAttendanceDays;

    /** 缺勤天数 */
    @Excel(name = "缺勤天数")
    private Integer absenceDays;

    /** 迟到/早退次数 */
    @Excel(name = "迟到/早退次数")
    private Integer lateEarlyCount;

    /** 加班小时数 */
    @Excel(name = "加班小时数")
    private BigDecimal overtimeHours;

    /** 出差天数 */
    @Excel(name = "出差天数")
    private Integer businessTripDays;

    /** 请假天数 */
    @Excel(name = "请假天数")
    private Integer leaveDays;

    /** 考勤工资(基本工资×实际出勤比例) */
    @Excel(name = "考勤工资")
    private BigDecimal attendanceSalary;

    /** 异常扣款(迟到早退) */
    @Excel(name = "异常扣款")
    private BigDecimal deductionAmount;

    /** 请假扣款 */
    @Excel(name = "请假扣款")
    private BigDecimal leaveDeduction;

    /** 加班工资 */
    @Excel(name = "加班工资")
    private BigDecimal overtimePay;

    /** 出差补贴 */
    @Excel(name = "出差补贴")
    private BigDecimal businessTripAllowance;

    /** 绩效工资 */
    @Excel(name = "绩效工资")
    private BigDecimal performancePay;

    /** 实发工资 */
    @Excel(name = "实发工资")
    private BigDecimal totalSalary;

    /** 状态(0未结算 1已结算) */
    @Excel(name = "状态", readConverterExp = "0=未结算,1=已结算")
    private String status;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 员工姓名（非数据库字段，关联查询用） */
    @Excel(name = "员工姓名")
    private String empName;

    /** 部门名称（非数据库字段，关联查询用） */
    @Excel(name = "部门")
    private String deptName;

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
    public void setBaseSalary(BigDecimal baseSalary) 
    {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBaseSalary() 
    {
        return baseSalary;
    }
    public void setActualAttendanceDays(Integer actualAttendanceDays) 
    {
        this.actualAttendanceDays = actualAttendanceDays;
    }

    public Integer getActualAttendanceDays() 
    {
        return actualAttendanceDays;
    }
    public void setAbsenceDays(Integer absenceDays) 
    {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() 
    {
        return absenceDays;
    }
    public void setLateEarlyCount(Integer lateEarlyCount) 
    {
        this.lateEarlyCount = lateEarlyCount;
    }

    public Integer getLateEarlyCount() 
    {
        return lateEarlyCount;
    }
    public void setOvertimeHours(BigDecimal overtimeHours) 
    {
        this.overtimeHours = overtimeHours;
    }

    public BigDecimal getOvertimeHours() 
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
    public void setLeaveDays(Integer leaveDays) 
    {
        this.leaveDays = leaveDays;
    }

    public Integer getLeaveDays() 
    {
        return leaveDays;
    }
    public void setAttendanceSalary(BigDecimal attendanceSalary) 
    {
        this.attendanceSalary = attendanceSalary;
    }

    public BigDecimal getAttendanceSalary() 
    {
        return attendanceSalary;
    }
    public void setDeductionAmount(BigDecimal deductionAmount) 
    {
        this.deductionAmount = deductionAmount;
    }

    public BigDecimal getDeductionAmount() 
    {
        return deductionAmount;
    }
    public void setLeaveDeduction(BigDecimal leaveDeduction) 
    {
        this.leaveDeduction = leaveDeduction;
    }

    public BigDecimal getLeaveDeduction() 
    {
        return leaveDeduction;
    }
    public void setOvertimePay(BigDecimal overtimePay) 
    {
        this.overtimePay = overtimePay;
    }

    public BigDecimal getOvertimePay() 
    {
        return overtimePay;
    }
    public void setBusinessTripAllowance(BigDecimal businessTripAllowance) 
    {
        this.businessTripAllowance = businessTripAllowance;
    }

    public BigDecimal getBusinessTripAllowance() 
    {
        return businessTripAllowance;
    }
    public void setPerformancePay(BigDecimal performancePay) 
    {
        this.performancePay = performancePay;
    }

    public BigDecimal getPerformancePay() 
    {
        return performancePay;
    }
    public void setTotalSalary(BigDecimal totalSalary) 
    {
        this.totalSalary = totalSalary;
    }

    public BigDecimal getTotalSalary() 
    {
        return totalSalary;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setUpdateTime(Date updateTime) 
    {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() 
    {
        return updateTime;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("empId", getEmpId())
            .append("year", getYear())
            .append("month", getMonth())
            .append("baseSalary", getBaseSalary())
            .append("actualAttendanceDays", getActualAttendanceDays())
            .append("absenceDays", getAbsenceDays())
            .append("lateEarlyCount", getLateEarlyCount())
            .append("overtimeHours", getOvertimeHours())
            .append("businessTripDays", getBusinessTripDays())
            .append("leaveDays", getLeaveDays())
            .append("attendanceSalary", getAttendanceSalary())
            .append("deductionAmount", getDeductionAmount())
            .append("leaveDeduction", getLeaveDeduction())
            .append("overtimePay", getOvertimePay())
            .append("businessTripAllowance", getBusinessTripAllowance())
            .append("performancePay", getPerformancePay())
            .append("totalSalary", getTotalSalary())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 