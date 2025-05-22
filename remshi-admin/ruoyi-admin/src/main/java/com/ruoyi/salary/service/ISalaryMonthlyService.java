package com.ruoyi.salary.service;

import java.util.List;
import com.ruoyi.salary.domain.SalaryMonthly;

/**
 * 月度薪资报表Service接口
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public interface ISalaryMonthlyService 
{
    /**
     * 查询月度薪资报表
     * 
     * @param id 月度薪资报表主键
     * @return 月度薪资报表
     */
    public SalaryMonthly selectSalaryMonthlyById(Integer id);

    /**
     * 查询月度薪资报表列表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 月度薪资报表集合
     */
    public List<SalaryMonthly> selectSalaryMonthlyList(SalaryMonthly salaryMonthly);

    /**
     * 新增月度薪资报表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 结果
     */
    public int insertSalaryMonthly(SalaryMonthly salaryMonthly);

    /**
     * 修改月度薪资报表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 结果
     */
    public int updateSalaryMonthly(SalaryMonthly salaryMonthly);

    /**
     * 批量删除月度薪资报表
     * 
     * @param ids 需要删除的月度薪资报表主键集合
     * @return 结果
     */
    public int deleteSalaryMonthlyByIds(Integer[] ids);

    /**
     * 删除月度薪资报表信息
     * 
     * @param id 月度薪资报表主键
     * @return 结果
     */
    public int deleteSalaryMonthlyById(Integer id);

    /**
     * 批量生成月度薪资报表
     * 
     * @param year 年份
     * @param month 月份
     * @return 生成的记录数
     */
    public int generateMonthlySalary(Integer year, Integer month);
} 