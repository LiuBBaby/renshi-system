package com.ruoyi.attendance.service;

import java.util.List;
import com.ruoyi.attendance.domain.AttendanceLocation;

/**
 * 考勤打卡位置Service接口
 * 
 * @author ruoyi
 */
public interface IAttendanceLocationService 
{
    /**
     * 查询考勤打卡位置
     * 
     * @param id 考勤打卡位置主键
     * @return 考勤打卡位置
     */
    public AttendanceLocation selectAttendanceLocationById(Integer id);

    /**
     * 查询考勤打卡位置列表
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 考勤打卡位置集合
     */
    public List<AttendanceLocation> selectAttendanceLocationList(AttendanceLocation attendanceLocation);

    /**
     * 新增考勤打卡位置
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 结果
     */
    public int insertAttendanceLocation(AttendanceLocation attendanceLocation);

    /**
     * 修改考勤打卡位置
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 结果
     */
    public int updateAttendanceLocation(AttendanceLocation attendanceLocation);

    /**
     * 批量删除考勤打卡位置
     * 
     * @param ids 需要删除的考勤打卡位置主键集合
     * @return 结果
     */
    public int deleteAttendanceLocationByIds(Integer[] ids);

    /**
     * 删除考勤打卡位置信息
     * 
     * @param id 考勤打卡位置主键
     * @return 结果
     */
    public int deleteAttendanceLocationById(Integer id);
} 