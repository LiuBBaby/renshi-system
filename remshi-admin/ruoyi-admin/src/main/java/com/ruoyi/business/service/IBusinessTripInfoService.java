package com.ruoyi.business.service;

import java.util.List;
import com.ruoyi.business.domain.BusinessTripInfo;

/**
 * 出差申请Service接口
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
public interface IBusinessTripInfoService 
{
    /**
     * 查询出差申请
     * 
     * @param id 出差申请主键
     * @return 出差申请
     */
    public BusinessTripInfo selectBusinessTripInfoById(Integer id);

    /**
     * 查询出差申请列表
     * 
     * @param businessTripInfo 出差申请
     * @return 出差申请集合
     */
    public List<BusinessTripInfo> selectBusinessTripInfoList(BusinessTripInfo businessTripInfo);

    /**
     * 新增出差申请
     * 
     * @param businessTripInfo 出差申请
     * @return 结果
     */
    public int insertBusinessTripInfo(BusinessTripInfo businessTripInfo);

    /**
     * 修改出差申请
     * 
     * @param businessTripInfo 出差申请
     * @return 结果
     */
    public int updateBusinessTripInfo(BusinessTripInfo businessTripInfo);

    /**
     * 批量删除出差申请
     * 
     * @param ids 需要删除的出差申请主键集合
     * @return 结果
     */
    public int deleteBusinessTripInfoByIds(Integer[] ids);

    /**
     * 删除出差申请信息
     * 
     * @param id 出差申请主键
     * @return 结果
     */
    public int deleteBusinessTripInfoById(Integer id);
    
    /**
     * 根据员工ID查询出差申请
     * 
     * @param empId 员工ID
     * @return 出差申请列表
     */
    public List<BusinessTripInfo> selectBusinessTripInfoByEmpId(String empId);
    
    /**
     * 审批出差申请
     * 
     * @param id 出差申请ID
     * @param result 审批结果
     * @param approver 审批人
     * @return 结果
     */
    public int approveBusinessTripInfo(Integer id, Integer result, String approver);
    
    /**
     * 统计指定时间范围内的出差天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期 格式yyyy-MM-dd
     * @param endDate 结束日期 格式yyyy-MM-dd
     * @return 出差天数
     */
    public int countBusinessTripDays(String empId, String startDate, String endDate);
    
    /**
     * 根据部门ID列表查询出差申请列表
     * 
     * @param businessTripInfo 出差申请查询条件
     * @param deptIds 部门ID列表
     * @return 出差申请集合
     */
    public List<BusinessTripInfo> selectBusinessTripInfoByDeptIds(BusinessTripInfo businessTripInfo, List<Long> deptIds);
} 