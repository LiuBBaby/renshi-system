package com.ruoyi.attendance.service;

import java.util.List;
import com.ruoyi.attendance.domain.AttendanceMonthly;

/**
 * 考勤月报Service接口
 * 
 * @author ruoyi
 */
public interface IAttendanceMonthlyService 
{
    /**
     * 查询考勤月报
     * 
     * @param id 考勤月报ID
     * @return 考勤月报
     */
    public AttendanceMonthly selectAttendanceMonthlyById(Integer id);

    /**
     * 查询考勤月报列表
     * 
     * @param attendanceMonthly 考勤月报
     * @return 考勤月报集合
     */
    public List<AttendanceMonthly> selectAttendanceMonthlyList(AttendanceMonthly attendanceMonthly);

    /**
     * 根据员工ID、年份和月份查询考勤月报
     * 
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 考勤月报
     */
    public AttendanceMonthly selectAttendanceMonthlyByEmpIdAndYearMonth(String empId, Integer year, Integer month);

    /**
     * 新增考勤月报
     * 
     * @param attendanceMonthly 考勤月报
     * @return 结果
     */
    public int insertAttendanceMonthly(AttendanceMonthly attendanceMonthly);

    /**
     * 修改考勤月报
     * 
     * @param attendanceMonthly 考勤月报
     * @return 结果
     */
    public int updateAttendanceMonthly(AttendanceMonthly attendanceMonthly);

    /**
     * 批量删除考勤月报
     * 
     * @param ids 需要删除的考勤月报ID
     * @return 结果
     */
    public int deleteAttendanceMonthlyByIds(Integer[] ids);

    /**
     * 删除考勤月报信息
     * 
     * @param id 考勤月报ID
     * @return 结果
     */
    public int deleteAttendanceMonthlyById(Integer id);
    
    /**
     * 生成指定年月的考勤月报
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    public int generateAttendanceMonthly(Integer year, Integer month);
    
    /**
     * 重新生成指定年月的考勤月报
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    public int regenerateAttendanceMonthly(Integer year, Integer month);
} 