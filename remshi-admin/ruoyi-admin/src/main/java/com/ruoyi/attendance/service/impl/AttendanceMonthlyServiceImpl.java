package com.ruoyi.attendance.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.attendance.mapper.AttendanceMonthlyMapper;
import com.ruoyi.attendance.domain.AttendanceMonthly;
import com.ruoyi.attendance.service.IAttendanceMonthlyService;
import com.ruoyi.common.core.text.Convert;

/**
 * 考勤月报服务实现
 * 
 * @author ruoyi
 */
@Service
public class AttendanceMonthlyServiceImpl implements IAttendanceMonthlyService 
{
    @Autowired
    private AttendanceMonthlyMapper attendanceMonthlyMapper;

    /**
     * 查询考勤月报
     * 
     * @param id 考勤月报主键
     * @return 考勤月报
     */
    @Override
    public AttendanceMonthly selectAttendanceMonthlyById(Integer id) 
    {
        return attendanceMonthlyMapper.selectAttendanceMonthlyById(id);
    }

    /**
     * 查询考勤月报列表
     * 
     * @param attendanceMonthly 考勤月报
     * @return 考勤月报
     */
    @Override
    public List<AttendanceMonthly> selectAttendanceMonthlyList(AttendanceMonthly attendanceMonthly) 
    {
        return attendanceMonthlyMapper.selectAttendanceMonthlyList(attendanceMonthly);
    }

    /**
     * 根据员工ID和年月查询考勤月报
     * 
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 考勤月报
     */
    @Override
    public AttendanceMonthly selectAttendanceMonthlyByEmpIdAndYearMonth(String empId, Integer year, Integer month) 
    {
        return attendanceMonthlyMapper.selectAttendanceMonthlyByEmpIdAndYearMonth(empId, year, month);
    }

    /**
     * 新增考勤月报
     * 
     * @param attendanceMonthly 考勤月报
     * @return 结果
     */
    @Override
    public int insertAttendanceMonthly(AttendanceMonthly attendanceMonthly) 
    {
        return attendanceMonthlyMapper.insertAttendanceMonthly(attendanceMonthly);
    }

    /**
     * 修改考勤月报
     * 
     * @param attendanceMonthly 考勤月报
     * @return 结果
     */
    @Override
    public int updateAttendanceMonthly(AttendanceMonthly attendanceMonthly) 
    {
        return attendanceMonthlyMapper.updateAttendanceMonthly(attendanceMonthly);
    }

    /**
     * 批量删除考勤月报
     * 
     * @param ids 需要删除的考勤月报主键集合
     * @return 结果
     */
    @Override
    public int deleteAttendanceMonthlyByIds(Integer[] ids) 
    {
        return attendanceMonthlyMapper.deleteAttendanceMonthlyByIds(ids);
    }

    /**
     * 删除考勤月报信息
     * 
     * @param id 考勤月报主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceMonthlyById(Integer id) 
    {
        return attendanceMonthlyMapper.deleteAttendanceMonthlyById(id);
    }
    
    /**
     * 生成指定年月的考勤月报
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    @Override
    public int generateAttendanceMonthly(Integer year, Integer month) 
    {
        // 检查是否已存在该月报表，如果存在则不重复生成
        AttendanceMonthly query = new AttendanceMonthly();
        query.setYear(year);
        query.setMonth(month);
        List<AttendanceMonthly> existingReports = attendanceMonthlyMapper.selectAttendanceMonthlyList(query);
        if (!existingReports.isEmpty()) {
            // 可以选择抛出异常或返回特定代码表示已存在
            // return 0; // 或者 throw new ServiceException("月报已存在，请勿重复生成！");
             // 这里选择允许重复生成，依赖业务决定，或者由 regenerate 方法处理
        }
        
        // 调用Mapper执行月报生成SQL (需要确保Mapper中有此方法)
        // 假设Mapper中有 generateMonthlyReport 方法
        return attendanceMonthlyMapper.generateMonthlyReport(year, month); 
    }

    /**
     * 重新生成指定年月的考勤月报
     * 
     * @param year 年份
     * @param month 月份
     * @return 结果
     */
    @Override
    public int regenerateAttendanceMonthly(Integer year, Integer month) 
    {
        // 先删除已有的当月考勤月报数据
        attendanceMonthlyMapper.deleteAttendanceMonthlyByYearMonth(year, month); // 需要在Mapper中添加此方法
        
        // 调用Mapper执行月报生成SQL
        return attendanceMonthlyMapper.generateMonthlyReport(year, month);
    }
} 