package com.ruoyi.salary.mapper;

import java.util.List;
import com.ruoyi.salary.domain.SalaryMonthly;
import org.apache.ibatis.annotations.Mapper;

/**
 * 月度薪资报表Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@Mapper
public interface SalaryMonthlyMapper 
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
     * 删除月度薪资报表
     * 
     * @param id 月度薪资报表主键
     * @return 结果
     */
    public int deleteSalaryMonthlyById(Integer id);

    /**
     * 批量删除月度薪资报表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSalaryMonthlyByIds(Integer[] ids);
} 
