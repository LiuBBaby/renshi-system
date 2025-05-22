package com.ruoyi.attendance.mapper;

import java.util.List;
import com.ruoyi.attendance.domain.AttendanceMonthly;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 考勤月报Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-25
 */
@Mapper
public interface AttendanceMonthlyMapper 
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
    public AttendanceMonthly selectAttendanceMonthlyByEmpIdAndYearMonth(@Param("empId") String empId, 
                                                                        @Param("year") Integer year, 
                                                                        @Param("month") Integer month);

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
     * 删除考勤月报
     * 
     * @param id 考勤月报ID
     * @return 结果
     */
    public int deleteAttendanceMonthlyById(Integer id);

    /**
     * 批量删除考勤月报
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAttendanceMonthlyByIds(Integer[] ids);

    /**
     * 根据年份和月份删除考勤月报
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    public int deleteAttendanceMonthlyByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 生成指定年月的考勤月报 (调用存储过程或复杂SQL)
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    public int generateMonthlyReport(@Param("year") Integer year, @Param("month") Integer month);
} 
