package com.ruoyi.examine.mapper;

import java.util.List;
import com.ruoyi.examine.domain.examine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 请假审批Mapper接口
 * 
 * @author 苏天霖
 * @date 2025-03-31
 */
@Mapper
public interface examineMapper 
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
     * 删除请假审批
     * 
     * @param id 请假审批主键
     * @return 结果
     */
    public int deleteexamineById(Long id);

    /**
     * 批量删除请假审批
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteexamineByIds(Long[] ids);
    
    /**
     * 根据员工ID删除请假审批记录
     *
     * @param examineInfoId 员工ID
     * @return 结果
     */
    public int deleteexamineByEmpId(String examineInfoId);
    
    /**
     * 统计指定时间范围内的请假天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 请奇天数
     */
    public int countLeaveDays(@Param("empId") String empId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 根据部门ID列表查询请假审批列表
     * 
     * @param examine 请假审批查询条件
     * @param deptIds 部门ID列表
     * @return 请假审批集合
     */
    public List<examine> selectexamineByDeptIds(@Param("examine") examine examine, @Param("deptIds") List<Long> deptIds);
}
