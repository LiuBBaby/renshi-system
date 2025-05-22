package com.ruoyi.attendance.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.attendance.domain.AttendanceEvaluationDTO;
import com.ruoyi.attendance.domain.AttendanceMonthlyStatsVO;

/**
 * 考勤管理Service接口
 *
 * @author ruoyi
 * @date 2025-03-30
 */
public interface IAttendanceInfoService
{
    /**
     * 查询考勤管理
     *
     * @param attendanceInfoId 考勤管理主键
     * @return 考勤管理
     */
    public AttendanceInfo selectAttendanceInfoByAttendanceInfoId(String attendanceInfoId);

    /**
     * 查询考勤管理列表
     *
     * @param attendanceInfo 考勤管理
     * @return 考勤管理集合
     */
    public List<AttendanceInfo> selectAttendanceInfoList(AttendanceInfo attendanceInfo);
    
    /**
     * 查询指定日期的考勤记录
     *
     * @param empId 员工ID
     * @param date 日期
     * @return 考勤记录
     */
    public AttendanceInfo selectAttendanceInfoByEmpIdAndDate(String empId, Date date);
    
    /**
     * 查询员工的考勤记录列表
     *
     * @param empId 员工ID
     * @param limit 限制数量
     * @return 考勤管理集合
     */
    public List<AttendanceInfo> selectAttendanceInfoListByEmpId(String empId, int limit);

    /**
     * 新增考勤管理
     *
     * @param attendanceInfo 考勤管理
     * @return 结果
     */
    public int insertAttendanceInfo(AttendanceInfo attendanceInfo);

    /**
     * 修改考勤管理
     *
     * @param attendanceInfo 考勤管理
     * @return 结果
     */
    public int updateAttendanceInfo(AttendanceInfo attendanceInfo);

    /**
     * 批量删除考勤管理
     *
     * @param attendanceInfoIds 需要删除的考勤管理主键集合
     * @return 结果
     */
    public int deleteAttendanceInfoByAttendanceInfoIds(String[] attendanceInfoIds);

    /**
     * 删除考勤管理信息
     *
     * @param attendanceInfoId 考勤管理主键
     * @return 结果
     */
    public int deleteAttendanceInfoByAttendanceInfoId(String attendanceInfoId);

    /**
     * 查询指定部门（及子部门）在指定日期的考勤评价列表
     *
     * @param targetDeptId 目标部门ID (如果为null，则使用当前登录用户的部门ID)
     * @param attendanceDate 考勤日期 (格式 YYYY-MM-DD)
     * @return 考勤评价列表
     */
    List<AttendanceEvaluationDTO> selectAttendanceEvaluationList(Long targetDeptId, String attendanceDate);
    
    /**
     * 查询指定员工在指定年月范围内的考勤记录
     *
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 考勤记录列表
     */
    List<AttendanceInfo> selectMonthlyAttendanceList(String empId, Integer year, Integer month);
    
    /**
     * 查询指定部门在指定年月范围内的考勤记录
     *
     * @param deptId 部门ID
     * @param year 年份
     * @param month 月份
     * @return 考勤记录列表
     */
    List<AttendanceInfo> selectMonthlyAttendanceListByDept(Long deptId, Integer year, Integer month);
    
    /**
     * 查询月度考勤统计数据
     *
     * @param attendanceInfo 查询条件，包含部门ID、年份、月份等
     * @return 月度考勤统计数据列表
     */
    List<AttendanceMonthlyStatsVO> selectMonthlyStatistics(AttendanceInfo attendanceInfo);
    
    /**
     * 生成月度考勤统计数据
     *
     * @param year 年份
     * @param month 月份
     * @return 影响行数
     */
    int generateMonthlyStatistics(int year, int month);
}
