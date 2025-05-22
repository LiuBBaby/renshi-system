package com.ruoyi.examine.service;

import java.util.List;
import com.ruoyi.examine.domain.examine;

/**
 * 请假审批Service接口
 * 
 * @author 苏天霖
 * @date 2025-03-31
 */
public interface IexamineService 
{
    /**
     * 查询请假审批
     * 
     * @param id 请假审批主键
     * @return 请假审批
     */
    public examine selectexamineById(Long id);

    /**
     * 查询请假审批列表
     * 
     * @param examine 请假审批
     * @return 请假审批集合
     */
    public List<examine> selectexamineList(examine examine);

    /**
     * 新增请假审批
     * 
     * @param examine 请假审批
     * @return 结果
     */
    public int insertexamine(examine examine);

    /**
     * 修改请假审批
     * 
     * @param examine 请假审批
     * @return 结果
     */
    public int updateexamine(examine examine);

    /**
     * 批量删除请假审批
     * 
     * @param ids 需要删除的请假审批主键集合
     * @return 结果
     */
    public int deleteexamineByIds(Long[] ids);

    /**
     * 删除请假审批信息
     * 
     * @param id 请假审批主键
     * @return 结果
     */
    public int deleteexamineById(Long id);
    
    /**
     * 统计指定员工在指定日期范围内的已批准请假天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @return 请假天数
     */
    public int countLeaveDays(String empId, String startDate, String endDate);
    
    /**
     * 根据部门ID列表查询请假审批列表
     * 
     * @param examine 请假审批查询条件
     * @param deptIds 部门ID列表
     * @return 请假审批集合
     */
    public List<examine> selectexamineByDeptIds(examine examine, List<Long> deptIds);
}
