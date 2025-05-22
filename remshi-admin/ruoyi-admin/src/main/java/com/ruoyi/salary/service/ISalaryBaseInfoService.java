package com.ruoyi.salary.service;

import java.util.List;
import com.ruoyi.salary.domain.SalaryBaseInfo;

/**
 * 薪资基本信息Service接口
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public interface ISalaryBaseInfoService 
{
    /**
     * 查询薪资基本信息
     * 
     * @param empId 薪资基本信息主键
     * @return 薪资基本信息
     */
    public SalaryBaseInfo selectSalaryBaseInfoByEmpId(String empId);

    /**
     * 查询薪资基本信息列表
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 薪资基本信息集合
     */
    public List<SalaryBaseInfo> selectSalaryBaseInfoList(SalaryBaseInfo salaryBaseInfo);

    /**
     * 新增薪资基本信息
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 结果
     */
    public int insertSalaryBaseInfo(SalaryBaseInfo salaryBaseInfo);

    /**
     * 修改薪资基本信息
     * 
     * @param salaryBaseInfo 薪资基本信息
     * @return 结果
     */
    public int updateSalaryBaseInfo(SalaryBaseInfo salaryBaseInfo);

    /**
     * 批量删除薪资基本信息
     * 
     * @param empIds 需要删除的薪资基本信息主键集合
     * @return 结果
     */
    public int deleteSalaryBaseInfoByEmpIds(String[] empIds);

    /**
     * 删除薪资基本信息信息
     * 
     * @param empId 薪资基本信息主键
     * @return 结果
     */
    public int deleteSalaryBaseInfoByEmpId(String empId);
} 