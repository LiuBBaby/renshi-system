package com.ruoyi.attendance.mapper;

import java.util.List;
import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.attendance.domain.AttendanceEvaluationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 考勤管理Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-30
 */
@Mapper
public interface AttendanceInfoMapper
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
     * 删除考勤管理
     *
     * @param attendanceInfoId 考勤管理主键
     * @return 结果
     */
    public int deleteAttendanceInfoByAttendanceInfoId(String attendanceInfoId);

    /**
     * 批量删除考勤管理
     *
     * @param attendanceInfoIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAttendanceInfoByAttendanceInfoIds(String[] attendanceInfoIds);

    /**
     * 根据员工ID删除考勤记录
     *
     * @param empId 员工ID
     * @return 结果
     */
    public int deleteAttendanceInfoByEmpId(String empId);

    /**
     * 查询指定部门（及子部门）在指定日期的考勤评价列表
     *
     * @param deptIds 部门ID列表
     * @param attendanceDate 考勤日期
     * @return 考勤评价列表
     */
    List<AttendanceEvaluationDTO> selectAttendanceEvaluationList(@Param("deptIds") List<Long> deptIds, 
                                                             @Param("attendanceDate") String attendanceDate);
    
    /**
     * 查询指定员工在指定日期范围内的考勤记录
     *
     * @param empId 员工ID
     * @param startDate 开始日期 (格式 YYYY-MM-DD)
     * @param endDate 结束日期 (格式 YYYY-MM-DD)
     * @return 考勤记录列表
     */
    List<AttendanceInfo> selectMonthlyAttendance(@Param("empId") String empId, 
                                              @Param("startDate") String startDate, 
                                              @Param("endDate") String endDate);
    
    /**
     * 查询指定部门下员工在指定日期范围内的考勤记录
     *
     * @param deptId 部门ID
     * @param startDate 开始日期 (格式 YYYY-MM-DD)
     * @param endDate 结束日期 (格式 YYYY-MM-DD)
     * @return 考勤记录列表
     */
    List<AttendanceInfo> selectMonthlyAttendanceByDept(@Param("deptId") Long deptId, 
                                                   @Param("startDate") String startDate, 
                                                   @Param("endDate") String endDate);
}
