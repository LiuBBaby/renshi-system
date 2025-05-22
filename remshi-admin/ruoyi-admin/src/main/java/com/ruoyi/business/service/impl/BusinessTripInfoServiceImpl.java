package com.ruoyi.business.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.mapper.BusinessTripInfoMapper;
import com.ruoyi.business.domain.BusinessTripInfo;
import com.ruoyi.business.service.IBusinessTripInfoService;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.EmpInfoMapper;
import com.ruoyi.system.domain.EmpInfo;

/**
 * 出差申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@Service
public class BusinessTripInfoServiceImpl implements IBusinessTripInfoService 
{
    @Autowired
    private BusinessTripInfoMapper businessTripInfoMapper;
    
    @Autowired
    private EmpInfoMapper empInfoMapper;
    
    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 查询出差申请
     * 
     * @param id 出差申请主键
     * @return 出差申请
     */
    @Override
    public BusinessTripInfo selectBusinessTripInfoById(Integer id)
    {
        return businessTripInfoMapper.selectBusinessTripInfoById(id);
    }

    /**
     * 查询出差申请列表
     * 
     * @param businessTripInfo 出差申请
     * @return 出差申请
     */
    @Override
    public List<BusinessTripInfo> selectBusinessTripInfoList(BusinessTripInfo businessTripInfo)
    {
        return businessTripInfoMapper.selectBusinessTripInfoList(businessTripInfo);
    }

    /**
     * 新增出差申请
     * 
     * @param businessTripInfo 出差申请
     * @return 结果
     */
    @Override
    public int insertBusinessTripInfo(BusinessTripInfo businessTripInfo)
    {
        // 如果前端没有提供申请人信息，则使用当前登录用户信息
        if (businessTripInfo.getTripInfoId() == null || "".equals(businessTripInfo.getTripInfoId())) {
            // 获取当前登录用户关联的员工信息
            Long userId = SecurityUtils.getUserId();
            EmpInfo empInfo = empInfoMapper.selectEmpInfoByUserId(userId);
            if (empInfo != null) {
                businessTripInfo.setTripInfoId(empInfo.getEmpInfoId());
                businessTripInfo.setTripInfoName(empInfo.getEmpInfoName());
                
                // 获取部门信息
                if (empInfo.getDeptId() != null) {
                    SysDept dept = deptMapper.selectDeptById(empInfo.getDeptId());
                    if (dept != null) {
                        businessTripInfo.setTripInfoDept(dept.getDeptName());
                    }
                }
            }
        }
        
        // 默认初始状态为一级审批中
        if (businessTripInfo.getTripInfoResult() == null) {
            businessTripInfo.setTripInfoResult(0);
        }
        
        businessTripInfo.setCreateTime(DateUtils.getNowDate());
        return businessTripInfoMapper.insertBusinessTripInfo(businessTripInfo);
    }

    /**
     * 修改出差申请
     * 
     * @param businessTripInfo 出差申请
     * @return 结果
     */
    @Override
    public int updateBusinessTripInfo(BusinessTripInfo businessTripInfo)
    {
        return businessTripInfoMapper.updateBusinessTripInfo(businessTripInfo);
    }

    /**
     * 批量删除出差申请
     * 
     * @param ids 需要删除的出差申请主键
     * @return 结果
     */
    @Override
    public int deleteBusinessTripInfoByIds(Integer[] ids)
    {
        return businessTripInfoMapper.deleteBusinessTripInfoByIds(ids);
    }

    /**
     * 删除出差申请信息
     * 
     * @param id 出差申请主键
     * @return 结果
     */
    @Override
    public int deleteBusinessTripInfoById(Integer id)
    {
        return businessTripInfoMapper.deleteBusinessTripInfoById(id);
    }
    
    /**
     * 根据员工ID查询出差申请
     * 
     * @param empId 员工ID
     * @return 出差申请列表
     */
    @Override
    public List<BusinessTripInfo> selectBusinessTripInfoByEmpId(String empId)
    {
        return businessTripInfoMapper.selectBusinessTripInfoByEmpId(empId);
    }
    
    /**
     * 审批出差申请
     * 
     * @param id 出差申请ID
     * @param result 审批结果
     * @param approver 审批人
     * @return 结果
     */
    @Override
    public int approveBusinessTripInfo(Integer id, Integer result, String approver)
    {
        BusinessTripInfo businessTripInfo = businessTripInfoMapper.selectBusinessTripInfoById(id);
        if (businessTripInfo == null) {
            return 0;
        }
        
        businessTripInfo.setTripInfoResult(result);
        businessTripInfo.setTripInfoPeople(approver);
        
        return businessTripInfoMapper.updateBusinessTripInfo(businessTripInfo);
    }
    
    /**
     * 统计指定时间范围内的出差天数
     * 
     * @param empId 员工工号
     * @param startDate 开始日期 格式yyyy-MM-dd
     * @param endDate 结束日期 格式yyyy-MM-dd
     * @return 出差天数
     */
    @Override
    public int countBusinessTripDays(String empId, String startDate, String endDate)
    {
        return businessTripInfoMapper.countBusinessTripDays(empId, startDate, endDate);
    }
    
    /**
     * 根据部门ID列表查询出差申请列表
     * 
     * @param businessTripInfo 出差申请查询条件
     * @param deptIds 部门ID列表
     * @return 出差申请集合
     */
    @Override
    public List<BusinessTripInfo> selectBusinessTripInfoByDeptIds(BusinessTripInfo businessTripInfo, List<Long> deptIds)
    {
        return businessTripInfoMapper.selectBusinessTripInfoByDeptIds(businessTripInfo, deptIds);
    }
}