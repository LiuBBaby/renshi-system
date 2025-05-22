package com.ruoyi.attendanceConfig.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.attendanceConfig.mapper.AttendanceConfigMapper;
import com.ruoyi.attendanceConfig.domain.AttendanceConfig;
import com.ruoyi.attendanceConfig.service.IAttendanceConfigService;

/**
 * 考勤规则字典Service业务层处理
 * 
 * @author 苏天霖
 * @date 2025-03-30
 */
@Service
public class AttendanceConfigServiceImpl implements IAttendanceConfigService 
{
    @Autowired
    private AttendanceConfigMapper attendanceConfigMapper;

    /**
     * 查询考勤规则字典
     * 
     * @param id 考勤规则字典主键
     * @return 考勤规则字典
     */
    @Override
    public AttendanceConfig selectAttendanceConfigById(String id)
    {
        return attendanceConfigMapper.selectAttendanceConfigById(id);
    }

    /**
     * 查询考勤规则字典列表
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 考勤规则字典
     */
    @Override
    public List<AttendanceConfig> selectAttendanceConfigList(AttendanceConfig attendanceConfig)
    {
        return attendanceConfigMapper.selectAttendanceConfigList(attendanceConfig);
    }

    /**
     * 新增考勤规则字典
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 结果
     */
    @Override
    public int insertAttendanceConfig(AttendanceConfig attendanceConfig)
    {
        return attendanceConfigMapper.insertAttendanceConfig(attendanceConfig);
    }

    /**
     * 修改考勤规则字典
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 结果
     */
    @Override
    public int updateAttendanceConfig(AttendanceConfig attendanceConfig)
    {
        return attendanceConfigMapper.updateAttendanceConfig(attendanceConfig);
    }

    /**
     * 批量删除考勤规则字典
     * 
     * @param ids 需要删除的考勤规则字典主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceConfigByIds(String[] ids)
    {
        return attendanceConfigMapper.deleteAttendanceConfigByIds(ids);
    }

    /**
     * 删除考勤规则字典信息
     * 
     * @param id 考勤规则字典主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceConfigById(String id)
    {
        return attendanceConfigMapper.deleteAttendanceConfigById(id);
    }
}
