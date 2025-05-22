package com.ruoyi.salary.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.salary.mapper.SalaryBaseInfoMapper;
import com.ruoyi.salary.domain.SalaryBaseInfo;
import com.ruoyi.salary.service.ISalaryBaseInfoService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.constant.HttpStatus;

/**
 * 薪资基本信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@Service
public class SalaryBaseInfoServiceImpl implements ISalaryBaseInfoService 
{
    @Autowired
    private SalaryBaseInfoMapper salaryBaseInfoMapper;

    /**
     * 查询薪资基本信息
     * 
     * @param empId 薪资基本信息主键
     * @return 薪资基本信息
     */
    @Override
    public SalaryBaseInfo selectSalaryBaseInfoByEmpId(String empId)
    {
        return salaryBaseInfoMapper.selectSalaryBaseInfoByEmpId(empId);
    }

    /**
     * 查询薪资基本信息列表
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 薪资基本信息
     */
    @Override
    public List<SalaryBaseInfo> selectSalaryBaseInfoList(SalaryBaseInfo salaryBaseInfo)
    {
        return salaryBaseInfoMapper.selectSalaryBaseInfoList(salaryBaseInfo);
    }

    /**
     * 新增薪资基本信息
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 结果
     */
    @Override
    public int insertSalaryBaseInfo(SalaryBaseInfo salaryBaseInfo)
    {
        try {
            // 计算日薪和时薪
            calculateSalaries(salaryBaseInfo);
            salaryBaseInfo.setCreateTime(DateUtils.getNowDate());
            return salaryBaseInfoMapper.insertSalaryBaseInfo(salaryBaseInfo);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("FK_salary_emp_id")) {
                throw new ServiceException("员工工号 " + salaryBaseInfo.getEmpId() + " 不存在，请先添加员工信息！", HttpStatus.ERROR);
            }
            throw e;
        }
    }

    /**
     * 修改薪资基本信息
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 结果
     */
    @Override
    public int updateSalaryBaseInfo(SalaryBaseInfo salaryBaseInfo)
    {
        try {
            // 计算日薪和时薪
            calculateSalaries(salaryBaseInfo);
            salaryBaseInfo.setUpdateTime(DateUtils.getNowDate());
            return salaryBaseInfoMapper.updateSalaryBaseInfo(salaryBaseInfo);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("FK_salary_emp_id")) {
                throw new ServiceException("员工工号 " + salaryBaseInfo.getEmpId() + " 不存在，请先添加员工信息！", HttpStatus.ERROR);
            }
            throw e;
        }
    }

    /**
     * 批量删除薪资基本信息
     * 
     * @param empIds 需要删除的薪资基本信息主键
     * @return 结果
     */
    @Override
    public int deleteSalaryBaseInfoByEmpIds(String[] empIds)
    {
        return salaryBaseInfoMapper.deleteSalaryBaseInfoByEmpIds(empIds);
    }

    /**
     * 删除薪资基本信息信息
     * 
     * @param empId 薪资基本信息主键
     * @return 结果
     */
    @Override
    public int deleteSalaryBaseInfoByEmpId(String empId)
    {
        return salaryBaseInfoMapper.deleteSalaryBaseInfoByEmpId(empId);
    }

    /**
     * 根据基本工资和标准出勤天数计算日薪和时薪
     *
     * @param salaryBaseInfo 薪资基本信息
     */
    private void calculateSalaries(SalaryBaseInfo salaryBaseInfo) {
        if (salaryBaseInfo.getBaseSalary() != null && salaryBaseInfo.getStandardAttendanceDays() != null && salaryBaseInfo.getStandardAttendanceDays() > 0) {
            BigDecimal baseSalary = salaryBaseInfo.getBaseSalary();
            BigDecimal standardDays = BigDecimal.valueOf(salaryBaseInfo.getStandardAttendanceDays());
            BigDecimal standardHoursPerDay = BigDecimal.valueOf(8); // 假设每天标准工作8小时

            // 计算日薪 = 基本工资 / 标准出勤天数
            BigDecimal daySalary = baseSalary.divide(standardDays, 2, RoundingMode.HALF_UP);
            salaryBaseInfo.setDaySalary(daySalary);

            // 计算时薪 = 日薪 / 每天标准工作小时数
            BigDecimal hourSalary = daySalary.divide(standardHoursPerDay, 2, RoundingMode.HALF_UP);
            salaryBaseInfo.setHourSalary(hourSalary);
        }
    }
} 