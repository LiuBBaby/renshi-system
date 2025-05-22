package com.ruoyi.recruitment.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.recruitment.mapper.RecruitmentInfoMapper;
import com.ruoyi.recruitment.domain.RecruitmentInfo;
import com.ruoyi.recruitment.service.IRecruitmentInfoService;
import com.ruoyi.system.mapper.EmpInfoMapper;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.common.exception.ServiceException;

/**
 * 招聘需求Service业务层处理
 * 
 * @author 苏天霖
 * @date 2025-03-27
 */
@Service
public class RecruitmentInfoServiceImpl implements IRecruitmentInfoService 
{
    @Autowired
    private RecruitmentInfoMapper recruitmentInfoMapper;
    
    @Autowired
    private EmpInfoMapper empInfoMapper;

    /**
     * 查询招聘需求
     * 
     * @param id 招聘需求主键
     * @return 招聘需求
     */
    @Override
    public RecruitmentInfo selectRecruitmentInfoById(String id)
    {
        return recruitmentInfoMapper.selectRecruitmentInfoById(id);
    }

    /**
     * 查询招聘需求列表
     * 
     * @param recruitmentInfo 招聘需求
     * @return 招聘需求
     */
    @Override
    public List<RecruitmentInfo> selectRecruitmentInfoList(RecruitmentInfo recruitmentInfo)
    {
        return recruitmentInfoMapper.selectRecruitmentInfoList(recruitmentInfo);
    }

    /**
     * 新增招聘需求
     * 
     * @param recruitmentInfo 招聘需求
     * @return 结果
     */
    @Override
    public int insertRecruitmentInfo(RecruitmentInfo recruitmentInfo)
    {
        // 验证员工工号是否存在
        EmpInfo empInfo = empInfoMapper.selectEmpInfoByEmpInfoId(recruitmentInfo.getRecruitmentInfoId().toString());
        if (empInfo == null) {
            throw new ServiceException("员工工号不存在，请检查输入");
        }
        
        // 验证同一发布者是否已发布相同岗位
        RecruitmentInfo queryInfo = new RecruitmentInfo();
        queryInfo.setRecruitmentInfoId(recruitmentInfo.getRecruitmentInfoId());
        queryInfo.setRecruitmentInfoPost(recruitmentInfo.getRecruitmentInfoPost());
        List<RecruitmentInfo> existingRecords = recruitmentInfoMapper.selectRecruitmentInfoList(queryInfo);
        if (existingRecords != null && !existingRecords.isEmpty()) {
            for (RecruitmentInfo existingRecord : existingRecords) {
                // 若已有相同岗位且状态是"进行中"(0)，则不允许重复添加
                if (existingRecord.getRecruitmentInfoStatus() != null && existingRecord.getRecruitmentInfoStatus() == 0) {
                    throw new ServiceException("该发布者已发布相同岗位且正在进行中，不能重复发布");
                }
            }
        }
        
        recruitmentInfo.setCreateTime(DateUtils.getNowDate());
        return recruitmentInfoMapper.insertRecruitmentInfo(recruitmentInfo);
    }

    /**
     * 修改招聘需求
     * 
     * @param recruitmentInfo 招聘需求
     * @return 结果
     */
    @Override
    public int updateRecruitmentInfo(RecruitmentInfo recruitmentInfo)
    {
        // 验证员工工号是否存在
        EmpInfo empInfo = empInfoMapper.selectEmpInfoByEmpInfoId(recruitmentInfo.getRecruitmentInfoId().toString());
        if (empInfo == null) {
            throw new ServiceException("员工工号不存在，请检查输入");
        }
        
        return recruitmentInfoMapper.updateRecruitmentInfo(recruitmentInfo);
    }

    /**
     * 批量删除招聘需求
     * 
     * @param ids 需要删除的招聘需求主键
     * @return 结果
     */
    @Override
    public int deleteRecruitmentInfoByIds(String[] ids)
    {
        return recruitmentInfoMapper.deleteRecruitmentInfoByIds(ids);
    }

    /**
     * 删除招聘需求信息
     * 
     * @param id 招聘需求主键
     * @return 结果
     */
    @Override
    public int deleteRecruitmentInfoById(String id)
    {
        return recruitmentInfoMapper.deleteRecruitmentInfoById(id);
    }
}
