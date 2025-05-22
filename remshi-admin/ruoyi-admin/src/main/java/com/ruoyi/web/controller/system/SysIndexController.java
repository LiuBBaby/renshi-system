package com.ruoyi.web.controller.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISystemMessageService;
import com.ruoyi.system.service.impl.EmpInfoServiceImpl;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.attendance.service.IAttendanceInfoService;
import com.ruoyi.examine.service.IexamineService;
import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.examine.domain.examine;
import com.ruoyi.business.service.IBusinessTripInfoService;
import com.ruoyi.business.domain.BusinessTripInfo;
import com.ruoyi.system.domain.SystemMessage;
import com.ruoyi.attendanceConfig.domain.AttendanceConfig;
import com.ruoyi.attendanceConfig.service.IAttendanceConfigService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/")
public class SysIndexController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(SysIndexController.class);
    
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Autowired
    private ISystemMessageService systemMessageService;
    
    @Autowired
    private EmpInfoServiceImpl empInfoService;
    
    @Autowired
    private IAttendanceInfoService attendanceInfoService;
    
    @Autowired
    private IexamineService examineService;
    
    @Autowired
    private IAttendanceConfigService attendanceConfigService;
    
    @Autowired
    private IBusinessTripInfoService businessTripInfoService;
    
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 访问首页，提示语
     */
    @GetMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
    
    /**
     * 获取首页统计数据
     */
    @GetMapping("/system/index/dashboardData")
    public AjaxResult getDashboardData()
    {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            log.info("当前用户部门ID: {}", userDeptId);
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId); // 添加当前部门
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            
            log.info("查询部门IDs: {}", deptIds);
            
            // 获取考勤规则配置
            AttendanceConfig queryConfig = new AttendanceConfig();
            queryConfig.setStatus(1L); // 获取状态为有效的配置
            List<AttendanceConfig> configs = attendanceConfigService.selectAttendanceConfigList(queryConfig);
            Map<String, String> attendanceRules = new HashMap<>();
            
            // 处理配置项
            for (AttendanceConfig config : configs) {
                attendanceRules.put(config.getParamKey(), config.getParamValue());
            }

            // 今日日期
            String today = DateUtils.getDate();
            String yesterday = DateUtils.dateAdd(today, -1);
            
            // 将字符串日期转换为Date对象
            Date todayDate = DateUtils.parseDate(today);
            Date yesterdayDate = DateUtils.parseDate(yesterday);
            
            // 获取本部门及子部门的所有员工
            List<String> empIds = new ArrayList<>();
            for (Long deptId : deptIds) {
                EmpInfo queryEmp = new EmpInfo();
                queryEmp.setDeptId(deptId);
                queryEmp.setStatus(0L); // 只查询状态为正常的员工
                List<EmpInfo> deptEmployees = empInfoService.selectEmpInfoList(queryEmp);
                
                for (EmpInfo emp : deptEmployees) {
                    empIds.add(emp.getEmpInfoId());
                }
            }
            
            log.info("部门员工数量: {}", empIds.size());
            
            // 如果没有员工，返回空数据
            if (empIds.isEmpty()) {
                return success(buildEmptyData());
            }
            
            // 获取今日到勤数据
            AttendanceInfo queryToday = new AttendanceInfo();
            queryToday.setAttendanceInfoDate(todayDate);
            List<AttendanceInfo> allTodayAttendance = attendanceInfoService.selectAttendanceInfoList(queryToday);
            
            // 筛选本部门及子部门的考勤记录（过滤null值）
            List<AttendanceInfo> todayAttendance = allTodayAttendance.stream()
                    .filter(a -> a != null && a.getEmpId() != null && empIds.contains(a.getEmpId()))
                    .collect(Collectors.toList());
            
            // 获取昨日考勤数据
            AttendanceInfo queryYesterday = new AttendanceInfo();
            queryYesterday.setAttendanceInfoDate(yesterdayDate);
            List<AttendanceInfo> allYesterdayAttendance = attendanceInfoService.selectAttendanceInfoList(queryYesterday);
            
            // 筛选本部门及子部门的考勤记录（过滤null值）
            List<AttendanceInfo> yesterdayAttendance = allYesterdayAttendance.stream()
                    .filter(a -> a != null && a.getEmpId() != null && empIds.contains(a.getEmpId()))
                    .collect(Collectors.toList());
            
            // 今日到勤人数 (上班打卡状态为0或1，即正常或迟到)
            long todayAttendanceCount = todayAttendance.stream()
                    .filter(a -> a != null && a.getCheckInStatus() != null && (a.getCheckInStatus() == 0 || a.getCheckInStatus() == 1))
                    .count();
            
            // 昨日到勤人数
            long yesterdayAttendanceCount = yesterdayAttendance.stream()
                    .filter(a -> a != null && a.getCheckInStatus() != null && (a.getCheckInStatus() == 0 || a.getCheckInStatus() == 1))
                    .count();
            
            // 今日迟到人数 (上班打卡状态为1)
            long todayLateCount = todayAttendance.stream()
                    .filter(a -> a != null && a.getCheckInStatus() != null && a.getCheckInStatus() == 1)
                    .count();
            
            // 昨日迟到人数
            long yesterdayLateCount = yesterdayAttendance.stream()
                    .filter(a -> a != null && a.getCheckInStatus() != null && a.getCheckInStatus() == 1)
                    .count();
            
            // 今日早退人数 (下班打卡状态为2)
            long todayEarlyCount = todayAttendance.stream()
                    .filter(a -> a != null && a.getCheckOutStatus() != null && a.getCheckOutStatus() == 2)
                    .count();
            
            // 昨日早退人数
            long yesterdayEarlyCount = yesterdayAttendance.stream()
                    .filter(a -> a != null && a.getCheckOutStatus() != null && a.getCheckOutStatus() == 2)
                    .count();
            
            // 请假中人数（从请假表查询）
            examine queryExamine = new examine();
            queryExamine.setExamineInfoResult(2L); // 审批通过的请假
            // 排除出差类型
            List<examine> allLeaveList = examineService.selectexamineList(queryExamine).stream()
                    .filter(e -> e != null && e.getExamineInfoReason() != null && !"出差".equals(e.getExamineInfoReason()))
                    .collect(Collectors.toList());
            
            // 筛选本部门及子部门的请假记录（过滤null值）
            List<examine> leaveList = allLeaveList.stream()
                    .filter(e -> e != null && e.getExamineInfoId() != null && empIds.contains(e.getExamineInfoId()))
                    .collect(Collectors.toList());
            
            // 今日请假人数
            long onLeaveCount = leaveList.stream()
                    .filter(e -> e != null && e.getExamineInfoDateBegin() != null && e.getExamineInfoDateEnd() != null 
                          && e.getExamineInfoDateBegin().compareTo(todayDate) <= 0 && e.getExamineInfoDateEnd().compareTo(todayDate) >= 0)
                    .count();
            log.info("今日请假人数: {}", onLeaveCount);
            
            // 昨日请假人数
            long yesterdayLeaveCount = leaveList.stream()
                    .filter(e -> e != null && e.getExamineInfoDateBegin() != null && e.getExamineInfoDateEnd() != null 
                          && e.getExamineInfoDateBegin().compareTo(yesterdayDate) <= 0 && e.getExamineInfoDateEnd().compareTo(yesterdayDate) >= 0)
                    .count();
            
            // 本月出差人数 - 从出差表中查询
            String monthStart = DateUtils.getMonthStart();
            String monthEnd = DateUtils.getMonthEnd();
            // 将字符串日期转换为Date对象
            Date monthStartDate = DateUtils.parseDate(monthStart);
            Date monthEndDate = DateUtils.parseDate(monthEnd);
            
            // 获取业务出差表数据
            BusinessTripInfo queryBusinessTrip = new BusinessTripInfo();
            queryBusinessTrip.setTripInfoResult(2); // 状态为2，表示已审批通过
            List<BusinessTripInfo> allBusinessTripList = businessTripInfoService.selectBusinessTripInfoList(queryBusinessTrip);
            
            // 筛选本部门及子部门的出差记录
            List<BusinessTripInfo> businessTripList = allBusinessTripList.stream()
                    .filter(bt -> bt != null && bt.getTripInfoId() != null && empIds.contains(bt.getTripInfoId()))
                    .collect(Collectors.toList());
            
            log.info("所有部门出差记录数: {}", allBusinessTripList.size());
            log.info("本部门及子部门出差记录数: {}", businessTripList.size());
            
            // 本月出差人数
            long businessTripCount = businessTripList.stream()
                    .filter(bt -> bt != null && bt.getTripInfoDateBegin() != null 
                          && bt.getTripInfoDateBegin().compareTo(monthStartDate) >= 0 && bt.getTripInfoDateBegin().compareTo(monthEndDate) <= 0)
                    .count();
            log.info("本月出差人数: {}", businessTripCount);
            
            // 上月出差人数
            String lastMonthStart = DateUtils.getLastMonthStart();
            String lastMonthEnd = DateUtils.getLastMonthEnd();
            // 将字符串日期转换为Date对象
            Date lastMonthStartDate = DateUtils.parseDate(lastMonthStart);
            Date lastMonthEndDate = DateUtils.parseDate(lastMonthEnd);
            
            long lastMonthBusinessTripCount = businessTripList.stream()
                    .filter(bt -> bt != null && bt.getTripInfoDateBegin() != null 
                          && bt.getTripInfoDateBegin().compareTo(lastMonthStartDate) >= 0 && bt.getTripInfoDateBegin().compareTo(lastMonthEndDate) <= 0)
                    .count();
            log.info("上月出差人数: {}", lastMonthBusinessTripCount);
            
            // 在职人数 (本部门及子部门)
            long employeeCount = empIds.size();
            
            // 构建返回数据
            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("value", todayAttendanceCount);
            attendanceData.put("change", todayAttendanceCount - yesterdayAttendanceCount);
            data.put("attendance", attendanceData);
            
            Map<String, Object> leaveData = new HashMap<>();
            leaveData.put("value", onLeaveCount);
            leaveData.put("change", onLeaveCount - yesterdayLeaveCount);
            data.put("leave", leaveData);
            
            Map<String, Object> lateData = new HashMap<>();
            lateData.put("value", todayLateCount);
            lateData.put("change", todayLateCount - yesterdayLateCount);
            data.put("late", lateData);
            
            Map<String, Object> earlyData = new HashMap<>();
            earlyData.put("value", todayEarlyCount);
            earlyData.put("change", todayEarlyCount - yesterdayEarlyCount);
            data.put("early", earlyData);
            
            Map<String, Object> businessTripData = new HashMap<>();
            businessTripData.put("value", businessTripCount);
            businessTripData.put("change", businessTripCount - lastMonthBusinessTripCount);
            data.put("businessTrip", businessTripData);
            
            Map<String, Object> employeeData = new HashMap<>();
            employeeData.put("value", employeeCount);
            employeeData.put("change", 0); // 暂不计算变化，可根据需求添加
            data.put("employee", employeeData);
            
            // 获取系统消息
            List<SystemMessage> messages = systemMessageService.selectLatestSystemMessages(5);
            data.put("messages", messages);
            
            // 添加考勤规则到返回数据
            data.put("attendanceRules", attendanceRules);
            
            return success(data);
        } catch (Exception e) {
            log.error("获取仪表盘数据失败", e);
            return error("获取数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 递归查找所有子部门
     */
    private void findAllChildDepts(Long parentId, List<SysDept> allDepts, List<Long> resultIds) {
        for (SysDept dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                resultIds.add(dept.getDeptId());
                // 递归查找子部门的子部门
                findAllChildDepts(dept.getDeptId(), allDepts, resultIds);
            }
        }
    }
    
    /**
     * 构建空数据
     */
    private Map<String, Object> buildEmptyData() {
        Map<String, Object> data = new HashMap<>();
        
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("value", 0);
        attendanceData.put("change", 0);
        data.put("attendance", attendanceData);
        
        Map<String, Object> leaveData = new HashMap<>();
        leaveData.put("value", 0);
        leaveData.put("change", 0);
        data.put("leave", leaveData);
        
        Map<String, Object> lateData = new HashMap<>();
        lateData.put("value", 0);
        lateData.put("change", 0);
        data.put("late", lateData);
        
        Map<String, Object> earlyData = new HashMap<>();
        earlyData.put("value", 0);
        earlyData.put("change", 0);
        data.put("early", earlyData);
        
        Map<String, Object> businessTripData = new HashMap<>();
        businessTripData.put("value", 0);
        businessTripData.put("change", 0);
        data.put("businessTrip", businessTripData);
        
        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("value", 0);
        employeeData.put("change", 0);
        data.put("employee", employeeData);
        
        // 获取考勤规则配置
        AttendanceConfig queryConfig = new AttendanceConfig();
        queryConfig.setStatus(1L); // 获取状态为有效的配置
        List<AttendanceConfig> configs = attendanceConfigService.selectAttendanceConfigList(queryConfig);
        Map<String, String> attendanceRules = new HashMap<>();
        
        // 处理配置项
        for (AttendanceConfig config : configs) {
            attendanceRules.put(config.getParamKey(), config.getParamValue());
        }
        
        data.put("attendanceRules", attendanceRules);
        data.put("messages", new ArrayList<>());
        
        return data;
    }
    
    /**
     * 获取考勤统计数据
     */
    @GetMapping("/system/index/attendanceStatistics")
    public AjaxResult getAttendanceStatistics(String beginDate, String endDate)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            log.info("统计图表 - 当前用户部门ID: {}", userDeptId);
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId); // 添加当前部门
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            
            log.info("统计图表 - 查询部门IDs: {}", deptIds);
            
            // 获取本部门及子部门的所有员工
            List<String> empIds = new ArrayList<>();
            for (Long deptId : deptIds) {
                EmpInfo queryEmp = new EmpInfo();
                queryEmp.setDeptId(deptId);
                queryEmp.setStatus(0L); // 只查询状态为正常的员工
                List<EmpInfo> deptEmployees = empInfoService.selectEmpInfoList(queryEmp);
                
                for (EmpInfo emp : deptEmployees) {
                    empIds.add(emp.getEmpInfoId());
                }
            }
            
            log.info("统计图表 - 部门员工数量: {}", empIds.size());
            
            // 转换日期格式
            Date beginDateObj = DateUtils.parseDate(beginDate);
            Date endDateObj = DateUtils.parseDate(endDate);
            
            if (beginDateObj == null || endDateObj == null) {
                return AjaxResult.error("日期参数格式错误");
            }
            
            // 查询日期范围内的考勤数据
            AttendanceInfo queryParams = new AttendanceInfo();
            queryParams.setParams(new HashMap<>());
            queryParams.getParams().put("beginAttendanceInfoDate", beginDate);
            queryParams.getParams().put("endAttendanceInfoDate", endDate);
            List<AttendanceInfo> allAttendanceList = attendanceInfoService.selectAttendanceInfoList(queryParams);
            
            // 筛选本部门及子部门的考勤记录
            List<AttendanceInfo> attendanceList = allAttendanceList.stream()
                    .filter(a -> a != null && a.getEmpId() != null && empIds.contains(a.getEmpId()))
                    .collect(Collectors.toList());
            
            log.info("统计图表 - 查询到部门考勤记录数: {}", attendanceList.size());
            
            // 按日期分组统计
            Map<String, Map<String, Integer>> dateStatistics = new HashMap<>();
            
            // 处理所有日期，包括无数据的日期
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDateObj);
            
            while (!calendar.getTime().after(endDateObj)) {
                Date currentDate = calendar.getTime();
                String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", currentDate);
                
                dateStatistics.put(dateStr, new HashMap<String, Integer>() {{
                    put("normal", 0);
                    put("late", 0);
                    put("early", 0);
                    put("absent", 0);
                }});
                
                // 增加一天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            // 按日期分组添加考勤记录
            Map<String, List<AttendanceInfo>> attendanceByDate = new HashMap<>();
            
            // 按日期分组
            for (AttendanceInfo attendance : attendanceList) {
                if (attendance.getAttendanceInfoDate() != null) {
                    String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", attendance.getAttendanceInfoDate());
                    List<AttendanceInfo> dailyList = attendanceByDate.getOrDefault(dateStr, new ArrayList<>());
                    dailyList.add(attendance);
                    attendanceByDate.put(dateStr, dailyList);
                }
            }
            
            // 得到日期对象列表用于请假和出差判断
            Map<String, Date> dateObjects = new HashMap<>();
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(beginDateObj);
            while (!tempCal.getTime().after(endDateObj)) {
                String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", tempCal.getTime());
                dateObjects.put(dateStr, tempCal.getTime());
                tempCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            // 查询请假数据
            examine queryExamine = new examine();
            queryExamine.setExamineInfoResult(2L); // 审批通过的请假
            // 排除出差类型
            List<examine> allLeaveList = examineService.selectexamineList(queryExamine).stream()
                    .filter(e -> e != null && e.getExamineInfoReason() != null && !"出差".equals(e.getExamineInfoReason()))
                    .collect(Collectors.toList());
            
            // 筛选本部门及子部门的请假记录
            List<examine> leaveList = allLeaveList.stream()
                    .filter(e -> e != null && e.getExamineInfoId() != null && empIds.contains(e.getExamineInfoId()))
                    .collect(Collectors.toList());
            
            log.info("统计图表 - 部门请假记录数: {}", leaveList.size());
            
            // 获取出差数据
            BusinessTripInfo queryBusinessTrip = new BusinessTripInfo();
            queryBusinessTrip.setTripInfoResult(2); // 状态为2，表示已审批通过
            List<BusinessTripInfo> allBusinessTripList = businessTripInfoService.selectBusinessTripInfoList(queryBusinessTrip);
            
            // 筛选本部门及子部门的出差记录
            List<BusinessTripInfo> businessTripList = allBusinessTripList.stream()
                    .filter(bt -> bt != null && bt.getTripInfoId() != null && empIds.contains(bt.getTripInfoId()))
                    .collect(Collectors.toList());
            
            log.info("统计图表 - 部门出差记录数: {}", businessTripList.size());
            
            // 对每一天进行统计
            for (Map.Entry<String, Map<String, Integer>> entry : dateStatistics.entrySet()) {
                String dateStr = entry.getKey();
                Map<String, Integer> dailyStats = entry.getValue();
                Date currentDate = dateObjects.get(dateStr);
                
                // 获取该日期的考勤记录
                List<AttendanceInfo> dailyAttendance = attendanceByDate.getOrDefault(dateStr, new ArrayList<>());
                
                // 当天请假人数
                int onLeaveCount = (int) leaveList.stream()
                        .filter(e -> e != null && e.getExamineInfoDateBegin() != null && e.getExamineInfoDateEnd() != null
                              && e.getExamineInfoDateBegin().compareTo(currentDate) <= 0 
                              && e.getExamineInfoDateEnd().compareTo(currentDate) >= 0)
                        .count();
                
                // 当天出差人数
                int onBusinessTripCount = (int) businessTripList.stream()
                        .filter(bt -> bt != null && bt.getTripInfoDateBegin() != null && bt.getTripInfoDateEnd() != null 
                              && bt.getTripInfoDateBegin().compareTo(currentDate) <= 0 
                              && bt.getTripInfoDateEnd().compareTo(currentDate) >= 0)
                        .count();
                
                // 当天应出勤人数 = 总人数 - 请假人数 - 出差人数
                int totalEmployees = empIds.size();
                int shouldAttendCount = totalEmployees - onLeaveCount - onBusinessTripCount;
                
                // 正常出勤人数（上班打卡状态为0或1，即正常或迟到）
                int normalCount = (int) dailyAttendance.stream()
                        .filter(a -> a != null && a.getCheckInStatus() != null && (a.getCheckInStatus() == 0 || a.getCheckInStatus() == 1))
                        .count();
                
                // 迟到人数（上班状态为迟到）
                int lateCount = (int) dailyAttendance.stream()
                        .filter(a -> a != null && a.getCheckInStatus() != null && a.getCheckInStatus() == 1)
                        .count();
                
                // 早退人数（下班状态为早退）
                int earlyCount = (int) dailyAttendance.stream()
                        .filter(a -> a != null && a.getCheckOutStatus() != null && a.getCheckOutStatus() == 2)
                        .count();
                
                // 旧工人数 = 应出勤人数 - 实际出勤人数
                int absentCount = shouldAttendCount - normalCount;
                if (absentCount < 0) absentCount = 0; // 防止计算错误导致负值
                
                // 更新统计数据
                dailyStats.put("normal", normalCount);
                dailyStats.put("late", lateCount);
                dailyStats.put("early", earlyCount);
                dailyStats.put("absent", absentCount);
                
                if (DateUtils.getDate().equals(dateStr)) {
                    log.info("统计图表 - 总人数: {}", totalEmployees);
                    log.info("统计图表 - 当天请假人数: {}", onLeaveCount);
                    log.info("统计图表 - 当天出差人数: {}", onBusinessTripCount);
                    log.info("统计图表 - 应出勤人数: {}", shouldAttendCount);
                    log.info("统计图表 - 实际出勤人数: {}", normalCount);
                    log.info("统计图表 - 迟到人数: {}", lateCount);
                    log.info("统计图表 - 早退人数: {}", earlyCount);
                    log.info("统计图表 - 旧工人数: {}", absentCount);
                }
                
                dateStatistics.put(dateStr, dailyStats);
            }
            
            // 构建返回数据
            List<String> dates = new ArrayList<>(dateStatistics.keySet());
            Collections.sort(dates); // 按日期排序
            
            List<Integer> normalData = new ArrayList<>();
            List<Integer> lateData = new ArrayList<>();
            List<Integer> earlyData = new ArrayList<>();
            List<Integer> absentData = new ArrayList<>();
            
            for (String date : dates) {
                Map<String, Integer> dailyStats = dateStatistics.get(date);
                normalData.add(dailyStats.getOrDefault("normal", 0));
                lateData.add(dailyStats.getOrDefault("late", 0));
                earlyData.add(dailyStats.getOrDefault("early", 0));
                absentData.add(dailyStats.getOrDefault("absent", 0));
            }
            
            result.put("dates", dates);
            result.put("normalData", normalData);
            result.put("lateData", lateData);
            result.put("earlyData", earlyData);
            result.put("absentData", absentData);
            
            return success(result);
        } catch (Exception e) {
            log.error("获取考勤统计数据失败", e);
            return AjaxResult.error("获取考勤统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未打卡员工列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 未打卡员工分页列表
     */
    @GetMapping("/system/index/notCheckedInEmployees")
    public TableDataInfo getNotCheckedInEmployees(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            // 开始分页
            startPage();
            
            // 获取当前登录用户的部门ID
            Long userDeptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            log.info("未打卡员工列表 - 当前用户部门ID: {}", userDeptId);
            
            // 获取当前部门及其所有子部门ID
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(userDeptId); // 添加当前部门
            
            // 查询所有部门
            SysDept deptQuery = new SysDept();
            List<SysDept> allDepts = sysDeptService.selectDeptList(deptQuery);
            
            // 递归查找所有子部门
            findAllChildDepts(userDeptId, allDepts, deptIds);
            log.info("未打卡员工列表 - 查询部门IDs: {}", deptIds);
            
            // 获取今天的日期
            String todayStr = DateUtils.getDate();
            Date today = DateUtils.parseDate(todayStr);
            
            // 查询所有部门下的员工
            EmpInfo queryEmp = new EmpInfo();
            List<EmpInfo> allEmployees = new ArrayList<>();
            
            // 生成部门员工ID列表
            List<String> empIds = new ArrayList<>();
            
            // 对每个部门进行查询
            for (Long departmentId : deptIds) {
                queryEmp.setDeptId(departmentId);
                queryEmp.setStatus(0L); // 只查询状态为正常的员工
                List<EmpInfo> deptEmployees = empInfoService.selectEmpInfoList(queryEmp);
                allEmployees.addAll(deptEmployees);
                
                // 收集员工ID
                for (EmpInfo emp : deptEmployees) {
                    if (emp.getEmpInfoId() != null) {
                        empIds.add(emp.getEmpInfoId());
                    }
                }
            }
            
            log.info("未打卡员工列表 - 总员工数: {}", allEmployees.size());
            
            // 查询今天已打卡的员工ID列表
            AttendanceInfo queryAttendance = new AttendanceInfo();
            queryAttendance.setAttendanceInfoDate(today);
            List<AttendanceInfo> todayAttendance = attendanceInfoService.selectAttendanceInfoList(queryAttendance);
            
            // 获取已打卡员工的ID列表
            List<String> checkedInEmpIds = todayAttendance.stream()
                    .filter(a -> a != null && a.getEmpId() != null)
                    .map(AttendanceInfo::getEmpId)
                    .collect(Collectors.toList());
            
            log.info("未打卡员工列表 - 已打卡员工数: {}", checkedInEmpIds.size());
            
            // 查询请假中的员工（请假审批通过且当前日期在请假范围内）
            examine queryExamine = new examine();
            queryExamine.setExamineInfoResult(2L); // 审批通过的请假
            List<examine> allLeaveList = examineService.selectexamineList(queryExamine);
            
            // 筛选本部门及子部门的请假记录，并且当前日期在请假范围内
            List<String> onLeaveEmpIds = allLeaveList.stream()
                    .filter(e -> e != null && e.getExamineInfoId() != null 
                          && empIds.contains(e.getExamineInfoId())
                          && e.getExamineInfoDateBegin() != null && e.getExamineInfoDateEnd() != null
                          && e.getExamineInfoDateBegin().compareTo(today) <= 0 
                          && e.getExamineInfoDateEnd().compareTo(today) >= 0)
                    .map(examine::getExamineInfoId)
                    .collect(Collectors.toList());
            
            log.info("未打卡员工列表 - 请假中员工数: {}", onLeaveEmpIds.size());
            
            // 查询出差中的员工
            BusinessTripInfo queryBusinessTrip = new BusinessTripInfo();
            queryBusinessTrip.setTripInfoResult(2); // 状态为2，表示已审批通过
            List<BusinessTripInfo> allBusinessTripList = businessTripInfoService.selectBusinessTripInfoList(queryBusinessTrip);
            
            // 筛选本部门及子部门的出差员工，并且当前日期在出差范围内
            List<String> onBusinessTripEmpIds = allBusinessTripList.stream()
                    .filter(bt -> bt != null && bt.getTripInfoId() != null 
                          && empIds.contains(bt.getTripInfoId())
                          && bt.getTripInfoDateBegin() != null && bt.getTripInfoDateEnd() != null
                          && bt.getTripInfoDateBegin().compareTo(today) <= 0 
                          && bt.getTripInfoDateEnd().compareTo(today) >= 0)
                    .map(BusinessTripInfo::getTripInfoId)
                    .collect(Collectors.toList());
            
            log.info("未打卡员工列表 - 出差中员工数: {}", onBusinessTripEmpIds.size());
            
            // 筛选出未打卡并且非请假非出差的员工
            List<EmpInfo> notCheckedInEmployees = allEmployees.stream()
                    .filter(emp -> !checkedInEmpIds.contains(emp.getEmpInfoId()) 
                          && !onLeaveEmpIds.contains(emp.getEmpInfoId())
                          && !onBusinessTripEmpIds.contains(emp.getEmpInfoId()))
                    .collect(Collectors.toList());
            
            log.info("未打卡员工列表 - 筛选后员工数: {}", notCheckedInEmployees.size());
            
            // 返回分页数据
            return getDataTable(notCheckedInEmployees);
        } catch (Exception e) {
            log.error("获取未打卡员工列表失败", e);
            return getDataTable(new ArrayList<>());
        }
    }
}
