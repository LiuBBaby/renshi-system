package com.ruoyi.examine.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.examine.mapper.examineMapper;
import com.ruoyi.examine.domain.examine;
import com.ruoyi.examine.service.IexamineService;

/**
 * 请假审批Service业务层处理
 * 
 * @author 苏天霖
 * @date 2025-03-31
 */
@Service
public class examineServiceImpl implements IexamineService 
{
    @Autowired
    private examineMapper examineMapper;

    /**
     * 查询请假审批
     * 
     * @param id 请假审批主键
     * @return 请假审批
     */
    @Override
    public examine selectexamineById(Long id)
    {
        return examineMapper.selectexamineById(id);
    }

    /**
     * 查询请假审批列表
     * 
     * @param examine 请假审批
     * @return 请假审批
     */
    @Override
    public List<examine> selectexamineList(examine examine)
    {
        return examineMapper.selectexamineList(examine);
    }

    /**
     * 新增请假审批
     * 
     * @param examine 请假审批
     * @return 结果
     */
    @Override
    public int insertexamine(examine examine)
    {
        examine.setCreateTime(DateUtils.getNowDate());
        return examineMapper.insertexamine(examine);
    }

    /**
     * 修改请假审批
     * 
     * @param examine 请假审批
     * @return 结果
     */
    @Override
    public int updateexamine(examine examine)
    {
        return examineMapper.updateexamine(examine);
    }

    /**
     * 批量删除请假审批
     * 
     * @param ids 需要删除的请假审批主键
     * @return 结果
     */
    @Override
    public int deleteexamineByIds(Long[] ids)
    {
        return examineMapper.deleteexamineByIds(ids);
    }

    /**
     * 删除请假审批信息
     * 
     * @param id 请假审批主键
     * @return 结果
     */
    @Override
    public int deleteexamineById(Long id)
    {
        return examineMapper.deleteexamineById(id);
    }

    /**
     * 统计指定员工在指定日期范围内的已批准请假天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @return 请假天数
     */
    @Override
    public int countLeaveDays(String empId, String startDate, String endDate)
    {
        return examineMapper.countLeaveDays(empId, startDate, endDate);
    }
    
    /**
     * 根据部门ID列表查询请假审批列表
     * 
     * @param examine 请假审批查询条件
     * @param deptIds 部门ID列表
     * @return 请假审批集合
     */
    @Override
    public List<examine> selectexamineByDeptIds(examine examine, List<Long> deptIds)
    {
        return examineMapper.selectexamineByDeptIds(examine, deptIds);
    }
}
