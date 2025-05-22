package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.EmpInfo;

/**
 * 员工信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
public interface EmpInfoMapper 
{
    /**
     * 查询员工信息
     * 
     * @param empInfoId 员工信息主键
     * @return 员工信息
     */
    public EmpInfo selectEmpInfoByEmpInfoId(String empInfoId);

    /**
     * 查询员工信息列表
     * 
     * @param empInfo 员工信息
     * @return 员工信息集合
     */
    public List<EmpInfo> selectEmpInfoList(EmpInfo empInfo);

    /**
     * 新增员工信息
     * 
     * @param empInfo 员工信息
     * @return 结果
     */
    public int insertEmpInfo(EmpInfo empInfo);

    /**
     * 修改员工信息
     * 
     * @param empInfo 员工信息
     * @return 结果
     */
    public int updateEmpInfo(EmpInfo empInfo);

    /**
     * 删除员工信息
     * 
     * @param empInfoId 员工信息主键
     * @return 结果
     */
    public int deleteEmpInfoByEmpInfoId(String empInfoId);

    /**
     * 批量删除员工信息
     * 
     * @param empInfoIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmpInfoByEmpInfoIds(String[] empInfoIds);

    EmpInfo selectEmpInfoByUserId(Long userId);

    /**
     * 通过部门ID数组查询员工列表
     *
     * @param deptIds 部门ID数组
     * @return 员工列表
     */
    public List<EmpInfo> selectEmpInfoByDeptIds(List<Long> deptIds);
}
