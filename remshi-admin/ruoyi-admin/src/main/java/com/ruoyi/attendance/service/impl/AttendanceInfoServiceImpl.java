package com.ruoyi.attendance.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.attendance.mapper.AttendanceInfoMapper;
import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.attendance.service.IAttendanceInfoService;
import com.ruoyi.attendance.domain.AttendanceEvaluationDTO;
import com.ruoyi.attendance.domain.AttendanceMonthlyStatsVO;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import java.util.Collections;
import java.util.stream.Collectors;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.examine.service.IexamineService;
import com.ruoyi.business.service.IBusinessTripInfoService;

/**
 * 考勤管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-30
 */
@Service
public class AttendanceInfoServiceImpl implements IAttendanceInfoService
{
    @Autowired
    private AttendanceInfoMapper attendanceInfoMapper;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IEmpInfoService empInfoService;

    @Autowired
    private IexamineService examineService;

    @Autowired
    private IBusinessTripInfoService businessTripInfoService;

    /**
     * 查询考勤管理
     *
     * @param attendanceInfoId 考勤管理主键
     * @return 考勤管理
     */
    @Override
    public AttendanceInfo selectAttendanceInfoByAttendanceInfoId(String attendanceInfoId)
    {
        return attendanceInfoMapper.selectAttendanceInfoByAttendanceInfoId(attendanceInfoId);
    }

    /**
     * 查询考勤管理列表
     *
     * @param attendanceInfo 考勤管理
     * @return 考勤管理
     */
    @Override
    public List<AttendanceInfo> selectAttendanceInfoList(AttendanceInfo attendanceInfo)
    {
        return attendanceInfoMapper.selectAttendanceInfoList(attendanceInfo);
    }
    
    /**
     * 查询指定日期的考勤记录
     *
     * @param empId 员工ID
     * @param date 日期
     * @return 考勤记录
     */
    @Override
    public AttendanceInfo selectAttendanceInfoByEmpIdAndDate(String empId, Date date)
    {
        AttendanceInfo query = new AttendanceInfo();
        query.setEmpId(empId);
        query.setAttendanceInfoDate(date);
        
        List<AttendanceInfo> list = attendanceInfoMapper.selectAttendanceInfoList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 查询员工的考勤记录列表
     *
     * @param empId 员工ID
     * @param limit 限制数量
     * @return 考勤管理集合
     */
    @Override
    public List<AttendanceInfo> selectAttendanceInfoListByEmpId(String empId, int limit)
    {
        AttendanceInfo query = new AttendanceInfo();
        query.setEmpId(empId);
        // 在实际的DAO层可能需要添加limit处理
        List<AttendanceInfo> list = attendanceInfoMapper.selectAttendanceInfoList(query);
        if (list != null && list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    /**
     * 新增考勤管理
     *
     * @param attendanceInfo 考勤管理
     * @return 结果
     */
    @Override
    public int insertAttendanceInfo(AttendanceInfo attendanceInfo)
    {
        attendanceInfo.setCreateTime(DateUtils.getNowDate());
        return attendanceInfoMapper.insertAttendanceInfo(attendanceInfo);
    }

    /**
     * 修改考勤管理
     *
     * @param attendanceInfo 考勤管理
     * @return 结果
     */
    @Override
    public int updateAttendanceInfo(AttendanceInfo attendanceInfo)
    {
        return attendanceInfoMapper.updateAttendanceInfo(attendanceInfo);
    }

    /**
     * 批量删除考勤管理
     *
     * @param attendanceInfoIds 需要删除的考勤管理主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceInfoByAttendanceInfoIds(String[] attendanceInfoIds)
    {
        return attendanceInfoMapper.deleteAttendanceInfoByAttendanceInfoIds(attendanceInfoIds);
    }

    /**
     * 删除考勤管理信息
     *
     * @param attendanceInfoId 考勤管理主键
     * @return 结果
     */
    @Override
    public int deleteAttendanceInfoByAttendanceInfoId(String attendanceInfoId)
    {
        return attendanceInfoMapper.deleteAttendanceInfoByAttendanceInfoId(attendanceInfoId);
    }

    /**
     * 查询指定部门（及子部门）在指定日期的考勤评价列表
     *
     * @param targetDeptId 目标部门ID (如果为null，则使用当前登录用户的部门ID)
     * @param attendanceDate 考勤日期 (格式 YYYY-MM-DD)
     * @return 考勤评价列表
     */
    @Override
    public List<AttendanceEvaluationDTO> selectAttendanceEvaluationList(Long targetDeptId, String attendanceDate) {
        Long currentUserId = SecurityUtils.getUserId();
        SysUser currentUser = userService.selectUserById(currentUserId);
        Long userDeptId = currentUser.getDeptId();

        Long finalDeptId = (targetDeptId == null) ? userDeptId : targetDeptId;

        // 查询目标部门及其所有子部门
        List<SysDept> allDepts = deptService.selectDeptList(new SysDept()); // 获取所有部门以便查找
        List<Long> deptIdsToQuery = allDepts.stream()
                .filter(dept -> dept.getDeptId().equals(finalDeptId) || 
                               (dept.getAncestors() != null && dept.getAncestors().contains("," + finalDeptId + ",")))
                .map(SysDept::getDeptId)
                .collect(Collectors.toList());

        if (deptIdsToQuery.isEmpty()) {
            return Collections.emptyList();
        }

        List<AttendanceEvaluationDTO> resultList = attendanceInfoMapper.selectAttendanceEvaluationList(deptIdsToQuery, attendanceDate);

        // 处理考勤状态文本
        for (AttendanceEvaluationDTO dto : resultList) {
            dto.setAttendanceStatusText(calculateAttendanceStatusText(dto.getCheckInStatus(), dto.getCheckOutStatus()));
            // 如果evaluation字段为null，设置默认值
            if (dto.getEvaluation() == null) {
                dto.setEvaluation("未评价");
            }
        }

        return resultList;
    }

    // 辅助方法：计算考勤状态文本
    private String calculateAttendanceStatusText(Integer checkInStatus, Integer checkOutStatus) {
        if (checkInStatus != null && checkInStatus == 3) return "缺勤";
        if (checkOutStatus != null && checkOutStatus == 3) return "缺勤";
        if (checkInStatus != null && checkInStatus == 1 && checkOutStatus != null && checkOutStatus == 2) return "迟到且早退";
        if (checkInStatus != null && checkInStatus == 1) return "迟到";
        if (checkOutStatus != null && checkOutStatus == 2) return "早退";
        if (checkInStatus != null && checkInStatus == 0 && checkOutStatus != null && checkOutStatus == 0) return "正常";
        // 处理只有一次打卡或状态不完整的情况
        if (checkInStatus != null && checkInStatus == 0) return "上班正常";
        if (checkOutStatus != null && checkOutStatus == 0) return "下班正常";
        return "异常"; // 或其他默认文本
    }
    
    /**
     * 查询指定员工在指定年月范围内的考勤记录
     *
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceInfo> selectMonthlyAttendanceList(String empId, Integer year, Integer month) {
        if (empId == null || year == null || month == null) {
            return Collections.emptyList();
        }
        
        try {
            // 计算该月的第一天和最后一天
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
            
            // 转换为Date类型用于SQL查询
            Date startDate = Date.from(firstDay.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastDay.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant());
            
            // 创建查询条件
            String startDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, startDate);
            String endDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, endDate);
            
            // 使用原生SQL查询
            // 注意：这里需要在AttendanceInfoMapper中添加一个自定义方法
            return attendanceInfoMapper.selectMonthlyAttendance(empId, startDateStr, endDateStr);
            
        } catch (Exception e) {
            System.err.println("查询月度考勤记录异常: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * 查询指定部门在指定年月范围内的考勤记录
     *
     * @param deptId 部门ID
     * @param year 年份
     * @param month 月份
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceInfo> selectMonthlyAttendanceListByDept(Long deptId, Integer year, Integer month) {
        if (deptId == null || year == null || month == null) {
            return Collections.emptyList();
        }
        
        try {
            // 计算该月的第一天和最后一天
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
            
            // 转换为Date类型用于SQL查询
            Date startDate = Date.from(firstDay.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastDay.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant());
            
            // 创建查询条件
            String startDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, startDate);
            String endDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, endDate);
            
            // 使用部门查询方法
            return attendanceInfoMapper.selectMonthlyAttendanceByDept(deptId, startDateStr, endDateStr);
            
        } catch (Exception e) {
            System.err.println("查询部门月度考勤记录异常: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * 查询月度考勤统计数据
     *
     * @param attendanceInfo 查询条件，包含查询参数
     * @return 月度考勤统计数据列表
     */
    @Override
    public List<AttendanceMonthlyStatsVO> selectMonthlyStatistics(AttendanceInfo attendanceInfo) {
        if (attendanceInfo == null) {
            return Collections.emptyList();
        }
        
        // 获取年月参数
        Integer year = null;
        Integer month = null;
        String deptName = null;
        String empName = null;
        String empId = null;
        
        // 从查询参数中获取筛选条件
        if (attendanceInfo.getParams() != null) {
            Map<String, Object> params = attendanceInfo.getParams();
            if (params.containsKey("year")) {
                try {
                    year = Integer.parseInt(params.get("year").toString());
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            if (params.containsKey("month")) {
                try {
                    month = Integer.parseInt(params.get("month").toString());
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            if (params.containsKey("deptName")) {
                deptName = params.get("deptName").toString();
            }
            if (params.containsKey("empName")) {
                empName = params.get("empName").toString();
            }
        }
        
        // 获取员工ID条件
        empId = attendanceInfo.getEmpId();
        
        // 如果未能获取到年月，使用当前日期
        if (year == null || month == null) {
            java.time.LocalDate today = java.time.LocalDate.now();
            year = today.getYear();
            month = today.getMonthValue();
        }
        
        // 获取当前登录用户的部门ID
        Long currentUserDeptId = SecurityUtils.getLoginUser().getDeptId();
        
        // 1. 递归获取当前部门及所有子部门ID
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(currentUserDeptId);
        
        // 获取所有部门
        SysDept deptQuery = new SysDept();
        if (deptName != null && !deptName.isEmpty()) {
            deptQuery.setDeptName(deptName);
        }
        List<SysDept> allDepts = deptService.selectDeptList(deptQuery);
        
        // 递归收集当前部门的所有子部门ID
        collectChildDeptIds(allDepts, currentUserDeptId, deptIds);
        
        // 如果输入了部门名称进行过滤
        if (deptName != null && !deptName.isEmpty()) {
            List<Long> filteredDeptIds = new ArrayList<>();
            for (SysDept dept : allDepts) {
                if (dept.getDeptName().contains(deptName) && deptIds.contains(dept.getDeptId())) {
                    filteredDeptIds.add(dept.getDeptId());
                }
            }
            if (!filteredDeptIds.isEmpty()) {
                deptIds = filteredDeptIds;
            }
        }
        
        // 2. 从emp_info表查询所有符合条件的员工
        List<EmpInfo> employees = new ArrayList<>();
        
        try {
            // 先通过部门ID数组查询所有员工
            if (!deptIds.isEmpty()) {
                // 如果通过deptName进行了筛选，则优先使用筛选后的部门列表
                if (deptName != null && !deptName.isEmpty()) {
                    // 直接根据部门名称查询
                    List<Long> filteredDeptIds = new ArrayList<>();
                    for (SysDept dept : allDepts) {
                        if (dept.getDeptName().contains(deptName)) {
                            filteredDeptIds.add(dept.getDeptId());
                        }
                    }
                    
                    if (!filteredDeptIds.isEmpty()) {
                        // 只查询匹配部门名称的员工
                        employees = empInfoService.selectEmpInfoByDeptIds(filteredDeptIds);
                    } else {
                        // 如果没有匹配的部门，则返回空结果
                        return Collections.emptyList();
                    }
                } else {
                    // 没有部门名称筛选，使用当前用户及下级部门
                    employees = empInfoService.selectEmpInfoByDeptIds(deptIds);
                }
            } else {
                // 如果没有部门ID，则查询所有员工
                EmpInfo empQuery = new EmpInfo();
                employees = empInfoService.selectEmpInfoList(empQuery);
            }
            
            // 根据员工ID和姓名条件进行过滤
            if (empId != null && !empId.isEmpty()) {
                final String idFilter = empId;
                employees = employees.stream()
                    .filter(emp -> emp.getEmpInfoId() != null && emp.getEmpInfoId().contains(idFilter))
                    .collect(Collectors.toList());
            }
            
            if (empName != null && !empName.isEmpty()) {
                final String nameFilter = empName;
                employees = employees.stream()
                    .filter(emp -> emp.getEmpInfoName() != null && emp.getEmpInfoName().contains(nameFilter))
                    .collect(Collectors.toList());
            }
            
            // 对员工进行去重处理（确保每个empInfoId只出现一次）
            employees = employees.stream()
                .collect(Collectors.toMap(
                    EmpInfo::getEmpInfoId,  // 使用员工ID作为key
                    emp -> emp,             // 值为员工对象本身
                    (existing, replacement) -> existing)) // 如果有重复，保留第一个
                .values()
                .stream()
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("查询员工信息异常: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
        
        if (employees.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 4. 为每个员工统计考勤数据
        List<AttendanceMonthlyStatsVO> result = new ArrayList<>();
        int finalYear = year;
        int finalMonth = month;
        
        // 使用流处理，为每个员工创建考勤统计对象
        result = employees.stream().map(emp -> {
            // 员工ID
            String employeeId = emp.getEmpInfoId();
            
            // 查询部门信息
            Long empDeptId = emp.getDeptId();
            String empDeptName = "未知部门";
            for (SysDept dept : allDepts) {
                if (dept.getDeptId().equals(empDeptId)) {
                    empDeptName = dept.getDeptName();
                    break;
                }
            }
            
            // 计算工作日
            int workDays = calculateWorkingDays(finalYear, finalMonth);
            
            // 创建统计对象
            AttendanceMonthlyStatsVO stats = new AttendanceMonthlyStatsVO();
            
            // 设置基本信息
            stats.setEmpId(employeeId);
            stats.setEmpName(emp.getEmpInfoName());
            stats.setDeptId(empDeptId);
            stats.setDeptName(empDeptName);
            stats.setYear(finalYear);
            stats.setMonth(finalMonth);
            stats.setWorkingDays(workDays);
            
            // 获取考勤记录
            List<AttendanceInfo> attendanceRecords = selectMonthlyAttendanceList(employeeId, finalYear, finalMonth);
            
            // 统计数据
            int actualWorkDays = 0;
            int normalDays = 0;
            int lateTimes = 0;
            int earlyTimes = 0;
            
            // 统计打卡数据
            for (AttendanceInfo record : attendanceRecords) {
                // 有打卡记录的天数计入实际出勤天数
                actualWorkDays++;
                
                // 统计正常打卡、迟到和早退
                if (record.getCheckInStatus() != null && record.getCheckOutStatus() != null) {
                    if (record.getCheckInStatus() == 0 && record.getCheckOutStatus() == 0) {
                        // 正常打卡
                        normalDays++;
                    }
                    if (record.getCheckInStatus() == 1) {
                        // 迟到
                        lateTimes++;
                    }
                    if (record.getCheckOutStatus() == 1) {
                        // 早退
                        earlyTimes++;
                    }
                }
            }
            
            // 获取请假天数
            int leaveDays = getApprovedLeaveDays(employeeId, finalYear, finalMonth);
            
            // 获取出差天数
            int businessTripDays = getApprovedBusinessTripDays(employeeId, finalYear, finalMonth);
            
            // 计算缺勤天数 = 应出勤天数 - 实际出勤天数 - 请假天数 - 出差天数
            int absenceDays = workDays - actualWorkDays - leaveDays - businessTripDays;
            if (absenceDays < 0) absenceDays = 0;
            
            // 设置统计值
            stats.setActualWorkDays(actualWorkDays);
            stats.setNormalDays(normalDays);
            stats.setLateCount(lateTimes);
            stats.setEarlyLeaveCount(earlyTimes);
            stats.setAbsenceDays(absenceDays);
            stats.setLeaveDays(leaveDays);
            stats.setBusinessTripDays(businessTripDays);
            
            // 计算出勤率
            double attendanceRate = 0;
            if (workDays > 0) {
                attendanceRate = (double)(actualWorkDays + leaveDays + businessTripDays) / workDays * 100;
                // 保留两位小数
                attendanceRate = Math.round(attendanceRate * 100) / 100.0;
            }
            stats.setAttendanceRate(attendanceRate);
            
            // 设置工作日数量
            stats.setWorkDays(workDays);
            
            return stats;
        }).collect(Collectors.toList());
        
        return result;
    }
    
    /**
     * 递归收集部门的所有子部门ID
     * 
     * @param allDepts 所有部门列表
     * @param parentId 父部门ID
     * @param deptIds 收集的部门ID列表
     */
    private void collectChildDeptIds(List<SysDept> allDepts, Long parentId, List<Long> deptIds) {
        for (SysDept dept : allDepts) {
            // 如果找到了子部门
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                Long childId = dept.getDeptId();
                // 避免重复添加
                if (!deptIds.contains(childId)) {
                    deptIds.add(childId);
                    // 递归处理其子部门
                    collectChildDeptIds(allDepts, childId, deptIds);
                }
            }
        }
    }
    
    /**
     * 获取已审批通过的请假天数
     * 
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 请假天数
     */
    private int getApprovedLeaveDays(String empId, int year, int month) {
        try {
            // 构建查询日期范围
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
            
            String startDateStr = firstDay.toString();
            String endDateStr = lastDay.toString();
            
            // 使用IexamineService服务查询请假天数
            return examineService.countLeaveDays(empId, startDateStr, endDateStr);
        } catch (Exception e) {
            System.err.println("获取请假天数异常: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 获取已审批通过的出差天数
     * 
     * @param empId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 出差天数
     */
    private int getApprovedBusinessTripDays(String empId, int year, int month) {
        try {
            // 构建查询日期范围
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
            
            String startDateStr = firstDay.toString();
            String endDateStr = lastDay.toString();
            
            // 使用IBusinessTripInfoService服务查询出差天数
            return businessTripInfoService.countBusinessTripDays(empId, startDateStr, endDateStr);
        } catch (Exception e) {
            System.err.println("获取出差天数异常: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 计算某月的工作日天数
     * 默认周一至周五为工作日，排除节假日，加入调休日期
     */
    private int calculateWorkingDays(int year, int month) {
        // 根据需求，直接返回固定的工作日数量22天
        return 22;
        
        /* 
        // 保留原来的实现代码作为参考
        try {
            // 获取该月的第一天和最后一天
            java.time.LocalDate firstDay = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
            
            int workDays = 0;
            java.time.LocalDate currentDay = firstDay;
            
            // 遍历该月每一天
            while (!currentDay.isAfter(lastDay)) {
                // 判断是否为工作日（周一至周五）
                java.time.DayOfWeek dayOfWeek = currentDay.getDayOfWeek();
                if (dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                    // 检查是否为节假日（需要根据实际业务查询）
                    if (!isHoliday(currentDay)) {
                        workDays++;
                    }
                } else {
                    // 检查是否为调休上班日（需要根据实际业务查询）
                    if (isWorkingDayOnWeekend(currentDay)) {
                        workDays++;
                    }
                }
                currentDay = currentDay.plusDays(1);
            }
            
            return workDays;
        } catch (Exception e) {
            // 如果出现异常，返回默认工作日天数（通常为21或22天）
            return 22;
        }
        */
    }
    
    /**
     * 判断指定日期是否为节假日
     * 
     * @param date 日期
     * @return 是否为节假日
     */
    private boolean isHoliday(java.time.LocalDate date) {
        // 这里应该查询attendance_config表中的节假日配置
        // 由于需要与实际业务结合，这里只提供一个框架
        return false;
    }
    
    /**
     * 判断周末是否为调休上班日
     * 
     * @param date 日期
     * @return 是否为调休上班日
     */
    private boolean isWorkingDayOnWeekend(java.time.LocalDate date) {
        // 这里应该查询attendance_config表中的调休上班日配置
        // 由于需要与实际业务结合，这里只提供一个框架
        return false;
    }

    /**
     * 生成月度考勤统计数据
     *
     * @param year 年份
     * @param month 月份
     * @return 影响行数
     */
    @Override
    public int generateMonthlyStatistics(int year, int month) {
        try {
            // 获取当前登录用户的部门ID
            Long currentUserDeptId = SecurityUtils.getLoginUser().getDeptId();
            
            // 递归获取当前部门及所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(currentUserDeptId);
            
            // 获取所有部门
            List<SysDept> allDepts = deptService.selectDeptList(new SysDept());
            
            // 递归收集当前部门的所有子部门ID
            collectChildDeptIds(allDepts, currentUserDeptId, deptIds);
            
            // 获取这些部门的所有用户
            List<SysUser> allUsers = new ArrayList<>();
            for (Long deptId : deptIds) {
                SysUser userQuery = new SysUser();
                userQuery.setDeptId(deptId);
                List<SysUser> deptUsers = userService.selectUserList(userQuery);
                allUsers.addAll(deptUsers);
            }
            
            int successCount = 0;
            
            // 为每个用户生成考勤统计
            for (SysUser user : allUsers) {
                String empId = user.getUserName();
                
                // 生成统计数据的流程
                AttendanceInfo query = new AttendanceInfo();
                query.setEmpId(empId);
                
                Map<String, Object> params = new HashMap<>();
                params.put("year", year);
                params.put("month", month);
                query.setParams(params);
                
                // 通过已实现的统计方法查询该员工的考勤统计
                List<AttendanceMonthlyStatsVO> stats = selectMonthlyStatistics(query);
                
                if (!stats.isEmpty()) {
                    // 如果统计成功，可以在这里保存到数据库
                    // saveMonthlyStatisticsToDatabase(stats.get(0));
                    successCount++;
                }
            }
            
            return successCount;
        } catch (Exception e) {
            System.err.println("生成月度考勤统计数据异常: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
