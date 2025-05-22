package com.ruoyi.attendance.mapper;

import java.util.List;
import com.ruoyi.attendance.domain.AttendanceLocation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤打卡位置Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface AttendanceLocationMapper 
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
     * 删除考勤打卡位置
     * 
     * @param id 考勤打卡位置主键
     * @return 结果
     */
    public int deleteAttendanceLocationById(Integer id);

    /**
     * 批量删除考勤打卡位置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAttendanceLocationByIds(Integer[] ids);
} 
