package com.ruoyi.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 员工信息对象 emp_info
 * 
 * @author ruoyi
 * @date 2025-03-21
 */

public class EmpInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 员工工号 */
    @Excel(name = "员工工号")
    private String empInfoId;

    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String empInfoName;

    /** 员工性别（0男 1女） */
    @Excel(name = "员工性别", readConverterExp = "0=男,1=女")
    private Long empInfoSex;

    /** 员工年龄 */
    @Excel(name = "员工年龄")
    private Long empInfoAge;

    /** 员工联系方式 */
    @Excel(name = "员工联系方式")
    private String empInfoIphone;
    /** 部门ID */
    @Excel(name = "部门编号", type = Excel.Type.IMPORT)
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 用户ID */
    private Long userId;

    /** 学历 */
    @Excel(name = "学历")
    private String empInfoEducational;

    /** 入职时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date empInfoEntry;

    /** 状态（0正常 1离职） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=离职")
    private Long status;

    /** 登录账号 */
    @Excel(name = "登录账号")
    private String username;

    /** 登录密码 */
    @Excel(name = "登录密码")
    private String passsword;

    public void setEmpInfoId(String empInfoId) {
        this.empInfoId = empInfoId;
    }

    public String getEmpInfoId() {
        return empInfoId;
    }

    public void setEmpInfoName(String empInfoName) {
        this.empInfoName = empInfoName;
    }

    public String getEmpInfoName() {
        return empInfoName;
    }

    public void setEmpInfoSex(Long empInfoSex) {
        this.empInfoSex = empInfoSex;
    }

    public Long getEmpInfoSex() {
        return empInfoSex;
    }

    public void setEmpInfoAge(Long empInfoAge) {
        this.empInfoAge = empInfoAge;
    }

    public Long getEmpInfoAge() {
        return empInfoAge;
    }

    public void setEmpInfoIphone(String empInfoIphone) {
        this.empInfoIphone = empInfoIphone;
    }

    public String getEmpInfoIphone() {
        return empInfoIphone;
    }

    public void setEmpInfoEducational(String empInfoEducational) {
        this.empInfoEducational = empInfoEducational;
    }

    public String getEmpInfoEducational() {
        return empInfoEducational;
    }

    public void setEmpInfoEntry(Date empInfoEntry) {
        this.empInfoEntry = empInfoEntry;
    }

    public Date getEmpInfoEntry() {
        return empInfoEntry;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
    }

    public String getPasssword() {
        return passsword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("empInfoId", getEmpInfoId())
            .append("empInfoName", getEmpInfoName())
            .append("empInfoSex", getEmpInfoSex())
            .append("empInfoAge", getEmpInfoAge())
            .append("empInfoIphone", getEmpInfoIphone())
            .append("empInfoEducational", getEmpInfoEducational())
            .append("empInfoEntry", getEmpInfoEntry())
            .append("status", getStatus())
            .append("deptId", getDeptId())
            .append("createTime", getCreateTime())
            .append("username", getUsername())
            .append("passsword", getPasssword())
            .append("userId", getUserId())
            .toString();
    }
}