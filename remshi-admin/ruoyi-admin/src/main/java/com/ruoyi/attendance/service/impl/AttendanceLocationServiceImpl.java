package com.ruoyi.attendance.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.attendance.mapper.AttendanceLocationMapper;
import com.ruoyi.attendance.domain.AttendanceLocation;
import com.ruoyi.attendance.service.IAttendanceLocationService;

/**
 * 考勤打卡位置Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class AttendanceLocationServiceImpl implements IAttendanceLocationService 
{
    @Autowired
    private AttendanceLocationMapper attendanceLocationMapper;

    /**
     * 查询考勤打卡位置
     * 
     * @param id 考勤打卡位置主键
     * @return 考勤打卡位置
     */
    @Override
    public AttendanceLocation selectAttendanceLocationById(Integer id)
    {
        return attendanceLocationMapper.selectAttendanceLocationById(id);
    }

    /**
     * 查询考勤打卡位置列表
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 考勤打卡位置
     */
    @Override
    public List<AttendanceLocation> selectAttendanceLocationList(AttendanceLocation attendanceLocation)
    {
        return attendanceLocationMapper.selectAttendanceLocationList(attendanceLocation);
    }

    /**
     * 新增考勤打卡位置
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 结果
     */
    @Override
    public int insertAttendanceLocation(AttendanceLocation attendanceLocation)
    {
        return attendanceLocationMapper.insertAttendanceLocation(attendanceLocation);
    }

    /**
     * 修改考勤打卡位置
     * 
     * @param attendanceLocation 考勤打卡位置
     * @return 结果
     */
    @Override
    public int updateAttendanceLocation(AttendanceLocation attendanceLocation)
    {
        return attendanceLocationMapper.updateAttendanceLocation(attendanceLocation);
    }

    /**
     * 批量删除考勤打卡位置
     * 
     * @param ids 需要删除的考勤打卡位置主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceLocationByIds(Integer[] ids)
    {
        return attendanceLocationMapper.deleteAttendanceLocationByIds(ids);
    }

    /**
     * 删除考勤打卡位置信息
     * 
     * @param id 考勤打卡位置主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceLocationById(Integer id)
    {
        return attendanceLocationMapper.deleteAttendanceLocationById(id);
    }
} 