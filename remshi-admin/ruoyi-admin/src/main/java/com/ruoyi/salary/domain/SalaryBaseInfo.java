package com.ruoyi.salary.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 薪资基本信息对象 salary_base_info
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public class SalaryBaseInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工工号 */
    @Excel(name = "员工工号")
    private String empId;

    /** 基本工资 */
    @Excel(name = "基本工资")
    private BigDecimal baseSalary;

    /** 标准出勤天数 */
    @Excel(name = "标准出勤天数")
    private Integer standardAttendanceDays;

    /** 小时工资 */
    @Excel(name = "小时工资")
    private BigDecimal hourSalary;

    /** 日工资 */
    @Excel(name = "日工资")
    private BigDecimal daySalary;

    /** 绩效基数 */
    @Excel(name = "绩效基数")
    private BigDecimal performanceBase;

    /** 迟到/早退扣款(每次) */
    @Excel(name = "迟到/早退扣款(每次)")
    private BigDecimal lateDeduction;

    /** 旷工扣款比例 */
    @Excel(name = "旷工扣款比例")
    private BigDecimal absenceDeductionRatio;

    /** 加班工资比例 */
    @Excel(name = "加班工资比例")
    private BigDecimal overtimeRatio;

    /** 出差补贴比例 */
    @Excel(name = "出差补贴比例")
    private BigDecimal businessTripRatio;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 员工姓名(非数据库字段) */
    @Excel(name = "员工姓名")
    private String empName;

    /** 部门名称(非数据库字段) */
    @Excel(name = "所属部门")
    private String deptName;

    public void setEmpId(String empId) 
    {
        this.empId = empId;
    }

    public String getEmpId() 
    {
        return empId;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) 
    {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBaseSalary() 
    {
        return baseSalary;
    }
    
    public void setStandardAttendanceDays(Integer standardAttendanceDays) 
    {
        this.standardAttendanceDays = standardAttendanceDays;
    }

    public Integer getStandardAttendanceDays() 
    {
        return standardAttendanceDays;
    }
    
    public void setHourSalary(BigDecimal hourSalary) 
    {
        this.hourSalary = hourSalary;
    }

    public BigDecimal getHourSalary() 
    {
        return hourSalary;
    }
    
    public void setDaySalary(BigDecimal daySalary) 
    {
        this.daySalary = daySalary;
    }

    public BigDecimal getDaySalary() 
    {
        return daySalary;
    }
    
    public void setPerformanceBase(BigDecimal performanceBase) 
    {
        this.performanceBase = performanceBase;
    }

    public BigDecimal getPerformanceBase() 
    {
        return performanceBase;
    }
    
    public void setLateDeduction(BigDecimal lateDeduction) 
    {
        this.lateDeduction = lateDeduction;
    }

    public BigDecimal getLateDeduction() 
    {
        return lateDeduction;
    }
    
    public void setAbsenceDeductionRatio(BigDecimal absenceDeductionRatio) 
    {
        this.absenceDeductionRatio = absenceDeductionRatio;
    }

    public BigDecimal getAbsenceDeductionRatio() 
    {
        return absenceDeductionRatio;
    }
    
    public void setOvertimeRatio(BigDecimal overtimeRatio) 
    {
        this.overtimeRatio = overtimeRatio;
    }

    public BigDecimal getOvertimeRatio() 
    {
        return overtimeRatio;
    }
    
    public void setBusinessTripRatio(BigDecimal businessTripRatio) 
    {
        this.businessTripRatio = businessTripRatio;
    }

    public BigDecimal getBusinessTripRatio() 
    {
        return businessTripRatio;
    }
    
    @Override
    public void setUpdateTime(Date updateTime) 
    {
        this.updateTime = updateTime;
    }

    @Override
    public Date getUpdateTime() 
    {
        return updateTime;
    }

    public String getEmpName() 
    {
        return empName;
    }

    public void setEmpName(String empName) 
    {
        this.empName = empName;
    }

    public String getDeptName() 
    {
        return deptName;
    }

    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("empId", getEmpId())
            .append("baseSalary", getBaseSalary())
            .append("standardAttendanceDays", getStandardAttendanceDays())
            .append("hourSalary", getHourSalary())
            .append("daySalary", getDaySalary())
            .append("performanceBase", getPerformanceBase())
            .append("lateDeduction", getLateDeduction())
            .append("absenceDeductionRatio", getAbsenceDeductionRatio())
            .append("overtimeRatio", getOvertimeRatio())
            .append("businessTripRatio", getBusinessTripRatio())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 