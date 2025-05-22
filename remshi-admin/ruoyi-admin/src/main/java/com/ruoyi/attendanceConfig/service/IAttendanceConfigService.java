package com.ruoyi.attendanceConfig.service;

import java.util.List;
import com.ruoyi.attendanceConfig.domain.AttendanceConfig;

/**
 * 考勤规则字典Service接口
 * 
 * @author 苏天霖
 * @date 2025-03-30
 */
public interface IAttendanceConfigService 
{
    /**
     * 查询考勤规则字典
     * 
     * @param id 考勤规则字典主键
     * @return 考勤规则字典
     */
    public AttendanceConfig selectAttendanceConfigById(String id);

    /**
     * 查询考勤规则字典列表
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 考勤规则字典集合
     */
    public List<AttendanceConfig> selectAttendanceConfigList(AttendanceConfig attendanceConfig);

    /**
     * 新增考勤规则字典
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 结果
     */
    public int insertAttendanceConfig(AttendanceConfig attendanceConfig);

    /**
     * 修改考勤规则字典
     * 
     * @param attendanceConfig 考勤规则字典
     * @return 结果
     */
    public int updateAttendanceConfig(AttendanceConfig attendanceConfig);

    /**
     * 批量删除考勤规则字典
     * 
     * @param ids 需要删除的考勤规则字典主键集合
     * @return 结果
     */
    public int deleteAttendanceConfigByIds(String[] ids);

    /**
     * 删除考勤规则字典信息
     * 
     * @param id 考勤规则字典主键
     * @return 结果
     */
    public int deleteAttendanceConfigById(String id);
}
