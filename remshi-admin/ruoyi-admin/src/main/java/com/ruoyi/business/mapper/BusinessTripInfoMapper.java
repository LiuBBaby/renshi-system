package com.ruoyi.business.mapper;

import java.util.List;
import com.ruoyi.business.domain.BusinessTripInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 出差申请Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@Mapper
public interface BusinessTripInfoMapper 
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
     * 删除出差申请
     * 
     * @param id 出差申请主键
     * @return 结果
     */
    public int deleteBusinessTripInfoById(Integer id);

    /**
     * 批量删除出差申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusinessTripInfoByIds(Integer[] ids);
    
    /**
     * 根据员工工号查询出差申请
     * 
     * @param tripInfoId 员工工号
     * @return 出差申请集合
     */
    public List<BusinessTripInfo> selectBusinessTripInfoByEmpId(String tripInfoId);
    
    /**
     * 统计指定时间范围内的出差天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 出差天数
     */
    public int countBusinessTripDays(@Param("empId") String empId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 根据部门ID列表查询出差申请列表
     * 
     * @param businessTripInfo 出差申请查询条件
     * @param deptIds 部门ID列表
     * @return 出差申请集合
     */
    public List<BusinessTripInfo> selectBusinessTripInfoByDeptIds(@Param("businessTripInfo") BusinessTripInfo businessTripInfo, @Param("deptIds") List<Long> deptIds);
} 
