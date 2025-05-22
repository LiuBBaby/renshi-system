package com.ruoyi.attendance.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.attendance.domain.AttendanceInfo;
import com.ruoyi.attendance.domain.AttendanceMonthly;
import com.ruoyi.attendance.domain.dto.CheckInDTO;
import com.ruoyi.attendance.service.IAttendanceInfoService;
import com.ruoyi.attendance.service.IAttendanceMonthlyService;
import com.ruoyi.attendanceConfig.domain.AttendanceConfig;
import com.ruoyi.attendanceConfig.service.IAttendanceConfigService;
import com.ruoyi.business.domain.BusinessTripInfo;
import com.ruoyi.business.service.IBusinessTripInfoService;
import com.ruoyi.examine.domain.examine;
import com.ruoyi.examine.service.IexamineService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.attendance.domain.AttendanceEvaluationDTO;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 微信小程序考勤打卡接口
 * 主要功能：
 * 1. 员工打卡（上班、下班）
 * 2. 获取考勤状态
 * 3. 考勤记录查询
 * 4. 考勤统计
 * 5. 考勤评价
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/attendance")
public class WxAttendanceController extends BaseController
{
    @Autowired
    private IAttendanceInfoService attendanceInfoService;
    
    @Autowired
    private IAttendanceConfigService attendanceConfigService;

    @Autowired
    private IEmpInfoService empInfoService;

    @Autowired
    private ISysDeptService deptService;
    
    @Autowired
    private IexamineService examineService;
    
    @Autowired
    private IBusinessTripInfoService businessTripInfoService;

    /**
     * 员工打卡接口
     *
     * 功能：
     * 1. 处理员工上班和下班打卡
     * 2. 记录打卡时间、位置信息
     * 3. 判断是否迟到、早退
     * 4. 更新考勤记录
     *
     * 打卡状态说明：
     * - 0: 正常打卡
     * - 1: 迟到
     * - 2: 早退
     * - 3: 缺勤
     *
     * @param checkInDTO 打卡信息，包含打卡类型（上班/下班）和位置信息
     * @return 打卡结果
     */
    @PostMapping("/checkin")
    public AjaxResult checkIn(@RequestBody CheckInDTO checkInDTO)
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            logger.info("员工打卡, 用户ID: {}", userId);
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 使用员工表中的ID和姓名
            String empId = empInfo.getEmpInfoId();
            String empName = empInfo.getEmpInfoName();
            
            logger.info("找到员工信息: 工号={}, 姓名={}", empId, empName);
            
            // 获取打卡类型（上班/下班）
            String type = checkInDTO.getType();
            
            // 获取当前日期和时间
            LocalDate currentDate = LocalDate.now();
            LocalDateTime currentTime = LocalDateTime.now();
            
            // 获取位置信息 - 使用DTO中的属性
            java.math.BigDecimal longitude = checkInDTO.getLongitude();
            java.math.BigDecimal latitude = checkInDTO.getLatitude();
            String address = checkInDTO.getAddress();
            
            // 查询考勤配置信息
            AttendanceConfig configParam = new AttendanceConfig();
            List<AttendanceConfig> configs = attendanceConfigService.selectAttendanceConfigList(configParam);
            
            // 配置信息Map，方便查询
            Map<String, String> configMap = new HashMap<>();
            for (AttendanceConfig config : configs) {
                configMap.put(config.getParamKey(), config.getParamValue());
            }
            
            if ("checkin".equals(type)) {
                // 上班打卡
                int status = 0; // 默认正常
                
                // 判断是否迟到
                LocalDateTime attendanceTime = LocalDateTime.parse(currentDate + " " + configMap.get("attendance_time"), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                if (currentTime.isAfter(attendanceTime)) {
                    status = 1; // 迟到
                }
                
                // 查询当天是否已有打卡记录
                AttendanceInfo queryParam = new AttendanceInfo();
                queryParam.setEmpId(empId);
                
                // 确保只使用日期部分，不包含时间
                Date today = DateUtils.parseDate(currentDate.toString());
                
                // 打印日志查看日期值
                logger.info("查询考勤日期: {}, 员工ID: {}", today, empId);
                
                queryParam.setAttendanceInfoDate(today);
                List<AttendanceInfo> existingRecords = attendanceInfoService.selectAttendanceInfoList(queryParam);
                
                // 打印查询结果数量，便于调试
                logger.info("查询到考勤记录数: {}", existingRecords.size());
                
                if (existingRecords.isEmpty()) {
                    // 没有当天记录，创建新记录
                    AttendanceInfo attendanceInfo = new AttendanceInfo();
                    attendanceInfo.setEmpId(empId);
                    attendanceInfo.setAttendanceInfoName(empName);
                    attendanceInfo.setCheckInTime(currentTime);
                    attendanceInfo.setCheckInStatus((long) status);
                    attendanceInfo.setAttendanceInfoDate(today);
                    attendanceInfo.setCreateTime(DateUtils.getNowDate());
                    
                    // 保存位置信息
                    if (longitude != null) {
                        attendanceInfo.setGoLongitude(longitude);
                    }
                    if (latitude != null) {
                        attendanceInfo.setGoLatitude(latitude);
                    }
                    attendanceInfo.setGoAddress(address);
                    
                    // 插入考勤记录
                    attendanceInfoService.insertAttendanceInfo(attendanceInfo);
                } else {
                    // 已有打卡记录，检查是否已上班打卡
                    AttendanceInfo existingRecord = existingRecords.get(0);
                    if (existingRecord.getCheckInTime() != null) {
                        return AjaxResult.error("今日已完成上班打卡");
                    }
                    
                    // 更新上班打卡信息
                    existingRecord.setCheckInTime(currentTime);
                    existingRecord.setCheckInStatus((long) status);
                    
                    // 保存位置信息
                    if (longitude != null) {
                        existingRecord.setGoLongitude(longitude);
                    }
                    if (latitude != null) {
                        existingRecord.setGoLatitude(latitude);
                    }
                    existingRecord.setGoAddress(address);
                    
                    // 更新记录
                    attendanceInfoService.updateAttendanceInfo(existingRecord);
                }
                
                return AjaxResult.success("上班打卡成功", status == 0 ? "正常打卡" : "迟到打卡");
                
            } else if ("checkout".equals(type)) {
                // 下班打卡
                int status = 0; // 默认正常
                
                try {
                    // 判断是否早退
                    LocalDateTime closingTime = LocalDateTime.parse(currentDate + " " + configMap.get("closing_time"), 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    logger.info("下班时间配置: {}, 当前时间: {}", closingTime, currentTime);
                    
                    if (currentTime.isBefore(closingTime)) {
                        status = 2; // 早退
                        logger.info("检测到早退");
                    }
                    
                    // 查询当天的打卡记录
                    AttendanceInfo queryParam = new AttendanceInfo();
                    queryParam.setEmpId(empId);
                    
                    // 确保使用相同的日期对象
                    Date today = DateUtils.parseDate(currentDate.toString());
                    logger.info("查询下班考勤日期: {}, 员工ID: {}", today, empId);
                    
                    queryParam.setAttendanceInfoDate(today);
                    List<AttendanceInfo> existingRecords = attendanceInfoService.selectAttendanceInfoList(queryParam);
                    
                    logger.info("查询到下班考勤记录数: {}", existingRecords.size());
                    
                    if (existingRecords.isEmpty()) {
                        // 没有上班打卡记录，创建新记录（直接下班打卡）
                        logger.info("未找到上班打卡记录，创建新的下班打卡记录");
                        AttendanceInfo attendanceInfo = new AttendanceInfo();
                        attendanceInfo.setEmpId(empId);
                        attendanceInfo.setAttendanceInfoName(empName);
                        attendanceInfo.setCheckOutTime(currentTime);
                        attendanceInfo.setCheckOutStatus((long) status);
                        attendanceInfo.setAttendanceInfoDate(today);
                        attendanceInfo.setCreateTime(DateUtils.getNowDate());
                        
                        // 保存下班位置信息
                        if (longitude != null) {
                            attendanceInfo.setOutLongitude(longitude);
                            logger.info("保存下班经度: {}", longitude);
                        }
                        if (latitude != null) {
                            attendanceInfo.setOutLatitude(latitude);
                            logger.info("保存下班纬度: {}", latitude);
                        }
                        if (address != null) {
                            attendanceInfo.setOutAddress(address);
                            logger.info("保存下班地址: {}", address);
                        }
                        
                        // 插入考勤记录
                        logger.info("插入新的下班打卡记录");
                        attendanceInfoService.insertAttendanceInfo(attendanceInfo);
                    } else {
                        // 更新上班打卡记录
                        AttendanceInfo existingRecord = existingRecords.get(0);
                        logger.info("找到上班打卡记录，ID: {}, 上班时间: {}", 
                                existingRecord.getAttendanceInfoId(), 
                                existingRecord.getCheckInTime());
                        
                        // 检查是否已下班打卡
                        if (existingRecord.getCheckOutTime() != null) {
                            logger.info("已经完成下班打卡，下班时间: {}", existingRecord.getCheckOutTime());
                            return AjaxResult.error("今日已完成下班打卡");
                        }
                        
                        // 设置下班时间和状态
                        existingRecord.setCheckOutTime(currentTime);
                        existingRecord.setCheckOutStatus((long) status);
                        
                        // 保存下班位置信息
                        if (longitude != null) {
                            existingRecord.setOutLongitude(longitude);
                        }
                        if (latitude != null) {
                            existingRecord.setOutLatitude(latitude);
                        }
                        if (address != null) {
                            existingRecord.setOutAddress(address);
                        }
                        
                        // 更新记录
                        attendanceInfoService.updateAttendanceInfo(existingRecord);
                    }
                    
                    return AjaxResult.success("下班打卡成功", status == 0 ? "正常打卡" : "早退打卡");
                    
                } catch (Exception e) {
                    logger.error("下班打卡异常", e);
                    return AjaxResult.error("下班打卡出错：" + e.getMessage());
                }
            } else {
                return AjaxResult.error("不支持的打卡类型");
            }
        } catch (Exception e) {
            logger.error("打卡异常", e);
            return AjaxResult.error("打卡出错：" + e.getMessage());
        }
    }

    /**
     * 获取今日考勤状态
     *
     * 功能：
     * 1. 获取当前用户的今日打卡记录
     * 2. 返回上下班打卡时间和状态
     * 3. 返回考勤配置信息（上下班时间）
     *
     * 返回数据：
     * - date: 日期
     * - attendanceTime: 规定上班时间
     * - closingTime: 规定下班时间
     * - checkInStatus: 上班打卡状态
     * - checkOutStatus: 下班打卡状态
     * - checkInTime: 上班打卡时间
     * - checkOutTime: 下班打卡时间
     *
     * @return 今日考勤状态信息
     */
    @GetMapping("/today")
    public AjaxResult getTodayStatus()
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 使用员工表中的ID
            String empId = empInfo.getEmpInfoId();
            
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            Date today = DateUtils.parseDate(currentDate.toString());
            
            // 查询今日考勤记录
            AttendanceInfo queryParam = new AttendanceInfo();
            queryParam.setEmpId(empId);
            queryParam.setAttendanceInfoDate(today);
            List<AttendanceInfo> todayRecords = attendanceInfoService.selectAttendanceInfoList(queryParam);
            
            // 查询考勤配置信息
            AttendanceConfig configParam = new AttendanceConfig();
            List<AttendanceConfig> configs = attendanceConfigService.selectAttendanceConfigList(configParam);
            
            // 配置信息Map，方便查询
            Map<String, String> configMap = new HashMap<>();
            for (AttendanceConfig config : configs) {
                configMap.put(config.getParamKey(), config.getParamValue());
            }
            
            // 提取上下班时间配置
            String attendanceTimeStr = configMap.getOrDefault("attendance_time", "09:00");
            String closingTimeStr = configMap.getOrDefault("closing_time", "18:00");
            
            // 构建返回数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", currentDate.toString());
            resultMap.put("attendanceTime", attendanceTimeStr);
            resultMap.put("closingTime", closingTimeStr);
            
            if (todayRecords.isEmpty()) {
                // 今日尚未打卡
                resultMap.put("checkInStatus", -1); // -1表示未打卡
                resultMap.put("checkOutStatus", -1);
                resultMap.put("checkInTime", null);
                resultMap.put("checkOutTime", null);
            } else {
                // 有考勤记录
                AttendanceInfo record = todayRecords.get(0);
                
                // 上班打卡状态
                if (record.getCheckInTime() != null) {
                    resultMap.put("checkInStatus", record.getCheckInStatus());
                    // 使用LocalDateTime格式化
                    resultMap.put("checkInTime", record.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } else {
                    resultMap.put("checkInStatus", -1);
                    resultMap.put("checkInTime", null);
                }

                // 下班打卡状态
                if (record.getCheckOutTime() != null) {
                    resultMap.put("checkOutStatus", record.getCheckOutStatus());
                    // 使用LocalDateTime格式化
                    resultMap.put("checkOutTime", record.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } else {
                    resultMap.put("checkOutStatus", -1);
                    resultMap.put("checkOutTime", null);
                }
            }
            
            return AjaxResult.success(resultMap);
        } catch (Exception e) {
            logger.error("获取今日考勤状态异常", e);
            return AjaxResult.error("获取考勤状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取考勤列表
     *
     * 功能：
     * 1. 获取当前登录用户的所有考勤记录
     * 2. 支持分页查询
     * 3. 按日期排序展示
     *
     * @param attendanceInfo 查询条件
     * @return 考勤记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AttendanceInfo attendanceInfo) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return new TableDataInfo();
            }
            
            // 设置员工ID，确保只查询当前用户的考勤记录
            attendanceInfo.setEmpId(empInfo.getEmpInfoId());
            
            startPage();
            List<AttendanceInfo> list = attendanceInfoService.selectAttendanceInfoList(attendanceInfo);
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("获取考勤列表异常", e);
            return new TableDataInfo();
        }
    }
    
    /**
     * 获取本周打卡详情
     *
     * 功能：
     * 1. 获取本周（周一至周日）的所有考勤记录
     * 2. 统计每日打卡状态
     * 3. 包含未打卡日期的记录
     *
     * 返回数据：
     * - 日期
     * - 星期
     * - 打卡状态
     * - 打卡时间
     *
     * @return 本周考勤详情
     */
    @GetMapping("/week")
    public AjaxResult getWeekAttendance() {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 员工ID
            String empId = empInfo.getEmpInfoId();
            
            // 获取本周的开始日期和结束日期
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
            
            // 转换为Date格式
            Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            
            // 构建查询条件
            AttendanceInfo queryParams = new AttendanceInfo();
            queryParams.setEmpId(empId);
            
            // 设置参数对象中的日期范围参数
            Map<String, Object> params = new HashMap<>();
            params.put("beginAttendanceInfoDate", DateUtils.parseDateToStr("yyyy-MM-dd", startDate));
            params.put("endAttendanceInfoDate", DateUtils.parseDateToStr("yyyy-MM-dd", endDate));
            queryParams.setParams(params);
            
            // 查询数据
            List<AttendanceInfo> list = attendanceInfoService.selectAttendanceInfoList(queryParams);
            
            // 按日期格式化数据
            List<Map<String, Object>> resultList = new ArrayList<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (AttendanceInfo info : list) {
                Map<String, Object> dayData = new HashMap<>();
                
                // 将日期转换为LocalDate格式
                Date attendanceDate = info.getAttendanceInfoDate();
                if (attendanceDate != null) {
                    String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", attendanceDate);
                    LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    dayData.put("date", dateStr);
                    dayData.put("dayOfWeek", localDate.getDayOfWeek().getValue()); // 1=周一, 7=周日
                    
                    // 设置打卡状态
                    Long checkInStatus = info.getCheckInStatus();
                    Long checkOutStatus = info.getCheckOutStatus();
                    
                    // 上班打卡时间
                    if (info.getCheckInTime() != null) {
                        try {
                            // 修复：正确处理LocalDateTime类型转换为时间字符串
                            LocalDateTime checkInTime = info.getCheckInTime();
                            String checkInTimeStr = checkInTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                            dayData.put("checkInTime", checkInTimeStr);
                        } catch (Exception e) {
                            logger.error("格式化上班打卡时间失败", e);
                            dayData.put("checkInTime", null);
                        }
                        dayData.put("checkInStatus", checkInStatus != null ? checkInStatus : -1);
                    } else {
                        dayData.put("checkInTime", null);
                        dayData.put("checkInStatus", -1);
                    }
                    
                    // 下班打卡时间
                    if (info.getCheckOutTime() != null) {
                        try {
                            // 修复：正确处理LocalDateTime类型转换为时间字符串
                            LocalDateTime checkOutTime = info.getCheckOutTime();
                            String checkOutTimeStr = checkOutTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                            dayData.put("checkOutTime", checkOutTimeStr);
                        } catch (Exception e) {
                            logger.error("格式化下班打卡时间失败", e);
                            dayData.put("checkOutTime", null);
                        }
                        dayData.put("checkOutStatus", checkOutStatus != null ? checkOutStatus : -1);
                    } else {
                        dayData.put("checkOutTime", null);
                        dayData.put("checkOutStatus", -1);
                    }
                    
                    resultList.add(dayData);
                }
            }
            
            // 确保每天都有数据，包括未打卡的日期
            Map<LocalDate, Map<String, Object>> dateMap = new HashMap<>();
            for (Map<String, Object> data : resultList) {
                String dateStr = (String) data.get("date");
                LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                dateMap.put(date, data);
            }
            
            // 填充本周所有日期
            List<Map<String, Object>> finalResult = new ArrayList<>();
            for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
                Map<String, Object> dayData = dateMap.get(date);
                if (dayData == null) {
                    // 该日期没有打卡记录，创建空记录
                    dayData = new HashMap<>();
                    dayData.put("date", date.format(dateFormatter));
                    dayData.put("dayOfWeek", date.getDayOfWeek().getValue());
                    dayData.put("checkInTime", null);
                    dayData.put("checkOutTime", null);
                    dayData.put("checkInStatus", -1);
                    dayData.put("checkOutStatus", -1);
                }
                finalResult.add(dayData);
            }
            
            return AjaxResult.success(finalResult);
        } catch (Exception e) {
            logger.error("获取本周打卡详情异常", e);
            return AjaxResult.error("获取本周打卡详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 考勤评价接口
     *
     * 功能：
     * 1. 获取指定月份的考勤统计
     * 2. 支持按部门筛选
     * 3. 统计出勤率、迟到早退次数等
     *
     * @param empId 员工ID（可选）
     * @param year 年份
     * @param month 月份
     * @return 考勤评价数据
     */
    @GetMapping("/evaluate")
    public AjaxResult getAttendanceEvaluate(
            @RequestParam(required = false) String empId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        
        // 如果empId为空，则获取当前登录用户的员工ID
        if (empId == null || empId.isEmpty()) {
            // 这里需要根据当前登录用户获取关联的员工ID
            empId = SecurityUtils.getUsername();
        }
        
        // 构造查询日期范围（当月的第一天到最后一天）
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 转换为Date类型
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        // 查询考勤数据
        AttendanceInfo query = new AttendanceInfo();
        query.setEmpId(empId);
        // 这里需要设置日期范围条件
        
        List<AttendanceInfo> attendanceList = attendanceInfoService.selectAttendanceInfoList(query);
        
        // 过滤日期范围内的数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (AttendanceInfo info : attendanceList) {
            if (info.getAttendanceInfoDate() != null) {
                LocalDate infoDate = info.getAttendanceInfoDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                
                if (!infoDate.isBefore(startDate) && !infoDate.isAfter(endDate)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", info.getAttendanceInfoId());
                    item.put("empId", info.getEmpId());
                    item.put("name", info.getAttendanceInfoName());
                    item.put("date", formatter.format(infoDate));
                    item.put("checkInTime", info.getCheckInTime());
                    item.put("checkOutTime", info.getCheckOutTime());
                    item.put("checkInStatus", info.getCheckInStatus());
                    item.put("checkOutStatus", info.getCheckOutStatus());
                    item.put("evaluate", info.getEvaluate());
                    
                    // 计算考勤状态
                    String status = "正常";
                    if (info.getCheckInStatus() != null && info.getCheckInStatus() == 1) {
                        status = "迟到";
                    } else if (info.getCheckOutStatus() != null && info.getCheckOutStatus() == 2) {
                        status = "早退";
                    } else if ((info.getCheckInStatus() != null && info.getCheckInStatus() == 3) ||
                               (info.getCheckOutStatus() != null && info.getCheckOutStatus() == 3)) {
                        status = "缺勤";
                    }
                    item.put("status", status);
                    
                    resultList.add(item);
                }
            }
        }
        
        return AjaxResult.success(resultList);
    }
    
    /**
     * 提交考勤评价
     * 
     * 功能：
     * 1. 更新指定考勤记录的评价信息
     * 2. 记录评价操作日志
     *
     * @param params 包含考勤ID和评价值的参数
     * @return 操作结果
     */
    @PostMapping("/evaluate")
    @Log(title = "考勤评价", businessType = BusinessType.UPDATE)
    public AjaxResult submitAttendanceEvaluate(@RequestBody Map<String, Object> params) {
        String attendanceId = (String) params.get("attendanceInfoId");
        Integer evaluateValue = (Integer) params.get("evaluate");
        
        if (attendanceId == null || evaluateValue == null) {
            return AjaxResult.error("参数不完整");
        }
        
        // 更新考勤评价
        AttendanceInfo info = attendanceInfoService.selectAttendanceInfoByAttendanceInfoId(attendanceId);
        if (info == null) {
            return AjaxResult.error("考勤记录不存在");
        }
        
        info.setEvaluate(evaluateValue.longValue());
        int rows = attendanceInfoService.updateAttendanceInfo(info);
        
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("评价提交失败");
    }
    
    /**
     * 获取月度考勤汇总数据
     * 
     * 功能：
     * 1. 获取指定月份的考勤记录
     * 2. 统计考勤状态分布
     * 3. 生成日历格式的考勤数据
     * 4. 计算出勤率等统计指标
     *
     * 返回数据：
     * - 统计数据（正常、迟到、早退、缺勤天数）
     * - 考勤记录列表
     * - 日历数据
     *
     * @param year 年份
     * @param month 月份
     * @param deptId 部门ID（可选）
     * @return 月度考勤汇总数据
     */
    @GetMapping("/monthly")
    public AjaxResult getMonthlyAttendance(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Long deptId) {
        
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            logger.info("获取用户{}的{}年{}月考勤数据", userId, year, month);
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 员工ID
            String empId = empInfo.getEmpInfoId();
            
            // 创建返回对象
            com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO result = new com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO();
            result.setYear(year);
            result.setMonth(month);

            // 获取月份的第一天和最后一天
            LocalDate firstDay = LocalDate.of(year, month, 1);
            LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

            List<AttendanceInfo> attendanceList;
            
            // 判断是否提供了部门ID参数
            if (deptId != null) {
                // 使用部门ID查询该部门下所有员工的考勤记录
                logger.info("根据部门ID{}查询{}年{}月考勤数据", deptId, year, month);
                attendanceList = attendanceInfoService.selectMonthlyAttendanceListByDept(deptId, year, month);
            } else {
                // 使用员工ID查询考勤记录
                logger.info("查询员工{}在{}年{}月的考勤记录", empId, year, month);
                attendanceList = attendanceInfoService.selectMonthlyAttendanceList(empId, year, month);
            }
            
            logger.info("查询到{}年{}月考勤记录{}条", year, month, attendanceList.size());
            
            // 设置考勤记录
            result.setRecords(attendanceList);
            
            // 统计数据
            com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO.StatisticsVO statistics = new com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO.StatisticsVO();
            statistics.setNormalDays(0);
            statistics.setLateDays(0);
            statistics.setEarlyDays(0);
            statistics.setAbsenceDays(0);
            statistics.setOvertimeDays(0);
            
            // 构建日历数据
            List<com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO.CalendarDayVO> calendarData = new ArrayList<>();
            
            // 记录每天的考勤状态
            Map<String, AttendanceInfo> dailyAttendanceMap = new HashMap<>();
            for (AttendanceInfo attendance : attendanceList) {
                // 获取日期字符串 YYYY-MM-DD
                Date attendanceDate = attendance.getAttendanceInfoDate();
                if (attendanceDate != null) {
                    // 使用DateUtils工具类安全地转换日期
                    String dateStr = DateUtils.parseDateToStr("yyyy-MM-dd", attendanceDate);
                    
                    // 如果使用部门ID查询，需要按员工和日期组合作为键
                    String mapKey = (deptId != null) ? 
                            attendance.getEmpId() + "_" + dateStr : dateStr;
                    
                    dailyAttendanceMap.put(mapKey, attendance);
                    
                    // 根据考勤状态统计
                    Long checkInStatus = attendance.getCheckInStatus();
                    Long checkOutStatus = attendance.getCheckOutStatus();
                    
                    boolean isNormal = (checkInStatus != null && checkInStatus == 0) && 
                                  (checkOutStatus != null && checkOutStatus == 0);
                    boolean isLate = checkInStatus != null && checkInStatus == 1;
                    boolean isEarly = checkOutStatus != null && checkOutStatus == 2;
                    boolean isAbsent = (checkInStatus != null && checkInStatus == 3) || 
                                  (checkOutStatus != null && checkOutStatus == 3);
                    boolean isOvertime = false; // 暂无加班逻辑，可以根据需要添加
                    
                    if (isNormal && !isLate && !isEarly && !isAbsent) {
                        statistics.setNormalDays(statistics.getNormalDays() + 1);
                    }
                    if (isLate) {
                        statistics.setLateDays(statistics.getLateDays() + 1);
                    }
                    if (isEarly) {
                        statistics.setEarlyDays(statistics.getEarlyDays() + 1);
                    }
                    if (isAbsent) {
                        statistics.setAbsenceDays(statistics.getAbsenceDays() + 1);
                    }
                    if (isOvertime) {
                        statistics.setOvertimeDays(statistics.getOvertimeDays() + 1);
                    }
                }
            }
            
            // 如果提供了部门ID，则不构建日历数据，只返回记录和统计信息
            if (deptId != null) {
                // 设置统计数据
                result.setStatistics(statistics);
                return AjaxResult.success(result);
            }
            
            // 构建当月所有日期的日历数据（仅当查询个人考勤时）
            for (int day = 1; day <= lastDay.getDayOfMonth(); day++) {
                LocalDate date = LocalDate.of(year, month, day);
                String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO.CalendarDayVO calendarDay = new com.ruoyi.attendance.domain.vo.AttendanceMonthlyVO.CalendarDayVO();
                calendarDay.setDate(dateStr);
                calendarDay.setDay(day);
                calendarDay.setIsCurrentMonth(true);
                
                // 判断是否有考勤记录
                if (dailyAttendanceMap.containsKey(dateStr)) {
                    AttendanceInfo attendance = dailyAttendanceMap.get(dateStr);
                    Integer status = 0; // 默认正常
                    
                    // 判断考勤状态
                    Long checkInStatus = attendance.getCheckInStatus();
                    Long checkOutStatus = attendance.getCheckOutStatus();
                    
                    if (checkInStatus != null && checkInStatus == 1) {
                        status = 1; // 迟到
                    } else if (checkOutStatus != null && checkOutStatus == 2) {
                        status = 2; // 早退
                    } else if ((checkInStatus != null && checkInStatus == 3) || 
                               (checkOutStatus != null && checkOutStatus == 3)) {
                        status = 3; // 缺勤
                    }
                    
                    calendarDay.setStatus(status);
                    calendarDay.setStatusText(getStatusTextShort(status));
                    calendarDay.setRecordId(Long.valueOf(attendance.getAttendanceInfoId()));
                } else {
                    // 没有考勤记录的情况
                    // 如果是周末或节假日，可以特殊处理
                    if (date.getDayOfWeek().getValue() >= 6) { // 周末
                        calendarDay.setStatus(null);
                        calendarDay.setStatusText("休");
                    } else {
                        // 如果是过去的日期并且是工作日，标记为缺勤
                        LocalDate today = LocalDate.now();
                        if (date.isBefore(today)) {
                            calendarDay.setStatus(3); // 缺勤
                            calendarDay.setStatusText("缺勤");
                        } else {
                            calendarDay.setStatus(null);
                            calendarDay.setStatusText(null);
                        }
                    }
                }
                
                calendarData.add(calendarDay);
            }
            
            // 设置统计和日历数据
            result.setStatistics(statistics);
            result.setCalendarData(calendarData);
            
            return AjaxResult.success(result);
            
        } catch (Exception e) {
            logger.error("获取月度考勤数据异常", e);
            return AjaxResult.error("获取考勤数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查请假和出差状态
     *
     * 功能：
     * 1. 检查当前用户是否有已批准的请假记录
     * 2. 检查是否有已批准的出差记录
     * 3. 返回请假/出差的详细信息
     *
     * 返回数据：
     * - hasApprovedLeave: 是否有已批准的请假
     * - hasApprovedBusinessTrip: 是否有已批准的出差
     * - leaveInfo: 请假详情
     * - businessTripInfo: 出差详情
     *
     * @return 请假和出差状态信息
     */
    @GetMapping("/checkLeaveOrBusinessTrip")
    public AjaxResult checkLeaveOrBusinessTrip() {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            logger.info("检查用户ID: {}是否有已批准的请假或出差记录", userId);
            
            // 通过userId查询员工信息
            EmpInfo empInfo = empInfoService.selectEmpInfoByUserId(userId);
            if (empInfo == null) {
                logger.error("未找到用户ID为{}的员工信息记录", userId);
                return AjaxResult.error("未找到对应的员工信息，请联系管理员");
            }
            
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            Date today = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("hasApprovedLeave", false);
            result.put("hasApprovedBusinessTrip", false);
            result.put("leaveInfo", null);
            result.put("businessTripInfo", null);
            
            // 1. 检查是否有已批准的请假记录
            examine examineQuery = new examine();
            examineQuery.setUserId(userId);
            examineQuery.setExamineInfoResult(2L); // 2表示审批通过
            
            // 获取已批准的请假列表
            List<examine> leaveList = examineService.selectexamineList(examineQuery);
            
            if (leaveList != null && !leaveList.isEmpty()) {
                // 筛选当前日期的请假记录
                for (examine leave : leaveList) {
                    Date startDate = leave.getExamineInfoDateBegin();
                    Date endDate = leave.getExamineInfoDateEnd();
                    
                    if (startDate != null && endDate != null) {
                        if (!today.before(startDate) && !today.after(endDate)) {
                            // 当前日期在请假日期范围内
                            result.put("hasApprovedLeave", true);
                            result.put("leaveInfo", leave);
                            break;
                        }
                    }
                }
            }
            
            // 2. 检查是否有已批准的出差记录
            BusinessTripInfo tripQuery = new BusinessTripInfo();
            tripQuery.setUserId(userId);
            tripQuery.setTripInfoResult(2); // 2表示审批通过
            
            // 获取已批准的出差列表
            List<BusinessTripInfo> tripList = businessTripInfoService.selectBusinessTripInfoList(tripQuery);
            
            if (tripList != null && !tripList.isEmpty()) {
                // 筛选当前日期的出差记录
                for (BusinessTripInfo trip : tripList) {
                    Date startDate = trip.getTripInfoDateBegin();
                    Date endDate = trip.getTripInfoDateEnd();
                    
                    if (startDate != null && endDate != null) {
                        if (!today.before(startDate) && !today.after(endDate)) {
                            // 当前日期在出差日期范围内
                            result.put("hasApprovedBusinessTrip", true);
                            result.put("businessTripInfo", trip);
                            break;
                        }
                    }
                }
            }
            
            return AjaxResult.success(result);
            
        } catch (Exception e) {
            logger.error("检查请假/出差记录异常", e);
            return AjaxResult.error("检查请假/出差记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取部门考勤详细列表
     *
     * 功能：
     * 1. 获取指定部门的所有员工考勤记录
     * 2. 支持查看子部门数据
     * 3. 排除当前用户的考勤记录
     * 4. 按创建时间倒序排序
     *
     * @param deptId 部门ID（可选）
     * @param year 年份
     * @param month 月份
     * @return 部门考勤记录列表
     */
    @GetMapping("/dept/monthly")
    public AjaxResult getDeptMonthlyDetail(
            @RequestParam(required = false) Long deptId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            // 获取当前用户的员工信息，用于后续排除自己的考勤记录
            EmpInfo currentUserEmpInfo = empInfoService.selectEmpInfoByUserId(userId);
            String currentEmpId = currentUserEmpInfo != null ? currentUserEmpInfo.getEmpInfoId() : null;
            
            // 如果未提供部门ID，则获取当前用户的部门ID
            Long finalDeptId = deptId;
            if (finalDeptId == null) {
                if (currentUserEmpInfo != null && currentUserEmpInfo.getDeptId() != null) {
                    finalDeptId = currentUserEmpInfo.getDeptId();
                    logger.info("使用当前用户部门ID: {}", finalDeptId);
                } else {
                    logger.error("未找到用户部门信息");
                    return AjaxResult.error("未找到用户部门信息");
                }
            }
            
            logger.info("获取部门ID{}及其下级部门的{}年{}月考勤详细数据", finalDeptId, year, month);
            
            // 获取指定部门及所有子部门的ID列表
            List<SysDept> allDepts = deptService.selectDeptList(new SysDept());
            List<Long> deptIds = allDepts.stream()
                    .map(SysDept::getDeptId)
                    .collect(Collectors.toList());
            
            if (deptIds.isEmpty()) {
                deptIds.add(finalDeptId); // 确保至少包含当前部门
            }
            
            logger.info("查询的部门ID列表: {}", deptIds);
            
            // 查询所有这些部门的员工考勤数据
            List<AttendanceInfo> allAttendanceList = new ArrayList<>();
            
            // 遍历每个部门ID，获取该部门的考勤数据
            for (Long dept : deptIds) {
                List<AttendanceInfo> deptAttendanceList = 
                        attendanceInfoService.selectMonthlyAttendanceListByDept(dept, year, month);
                if (deptAttendanceList != null && !deptAttendanceList.isEmpty()) {
                    allAttendanceList.addAll(deptAttendanceList);
                }
            }
            
            // 过滤掉当前用户自己的考勤记录
            if (currentEmpId != null) {
                List<AttendanceInfo> filteredList = allAttendanceList.stream()
                        .filter(record -> !currentEmpId.equals(record.getEmpId()))
                        .collect(Collectors.toList());
                
                logger.info("从{}条记录中过滤掉自己的考勤记录后，剩余{}条记录", 
                        allAttendanceList.size(), filteredList.size());
                
                allAttendanceList = filteredList;
            }
            
            logger.info("查询到{}年{}月部门{}及其下级部门考勤记录共{}条(已排除自己)", 
                    year, month, finalDeptId, allAttendanceList.size());

            // 按createTime倒序排序
            allAttendanceList = allAttendanceList.stream()
                    .sorted(Comparator.comparing(AttendanceInfo::getCreateTime, 
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            
            logger.info("已按创建时间倒序排序考勤记录");

            return AjaxResult.success(allAttendanceList);
            
        } catch (Exception e) {
            logger.error("获取部门考勤详细数据异常", e);
            return AjaxResult.error("获取考勤数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取考勤配置信息
     *
     * 功能：
     * 1. 获取考勤时间配置
     * 2. 获取考勤规则配置
     * 3. 如果配置不存在则返回默认值
     *
     * 配置项包括：
     * - 上午打卡时间范围
     * - 下午打卡时间范围
     * - 标准上下班时间
     *
     * @return 考勤配置信息
     */
    @GetMapping("/config")
    public AjaxResult getAttendanceConfig()
    {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            
            // 查询所有考勤配置
            AttendanceConfig queryParam = new AttendanceConfig();
            List<AttendanceConfig> configs = attendanceConfigService.selectAttendanceConfigList(queryParam);
            
            if (configs == null || configs.isEmpty()) {
                logger.warn("未找到考勤配置信息，将返回默认配置");
                // 返回默认配置
                Map<String, String> defaultConfig = new HashMap<>();
                defaultConfig.put("morningStartTime", "06:00");
                defaultConfig.put("morningEndTime", "09:30");
                defaultConfig.put("normalWorkTime", "08:30");
                defaultConfig.put("afternoonStartTime", "16:30");
                defaultConfig.put("afternoonEndTime", "23:59");
                defaultConfig.put("normalOffTime", "18:30");
                return AjaxResult.success(defaultConfig);
            }
            
            // 将配置项转换为前端易用的格式
            Map<String, String> configMap = new HashMap<>();
            for (AttendanceConfig config : configs) {
                configMap.put(config.getParamKey(), config.getParamValue());
            }
            
            // 确保所有必要的配置项都存在，如果不存在则使用默认值
            if (!configMap.containsKey("morningStartTime")) {
                configMap.put("morningStartTime", "06:00");
            }
            if (!configMap.containsKey("morningEndTime")) {
                configMap.put("morningEndTime", "09:30");
            }
            if (!configMap.containsKey("normalWorkTime")) {
                configMap.put("normalWorkTime", "08:30");
            }
            if (!configMap.containsKey("afternoonStartTime")) {
                configMap.put("afternoonStartTime", "16:30");
            }
            if (!configMap.containsKey("afternoonEndTime")) {
                configMap.put("afternoonEndTime", "23:59");
            }
            if (!configMap.containsKey("normalOffTime")) {
                configMap.put("normalOffTime", "18:30");
            }
            
            return AjaxResult.success(configMap);
        } catch (Exception e) {
            logger.error("获取考勤配置信息异常", e);
            return AjaxResult.error("获取考勤配置信息异常，请联系管理员");
        }
    }

    /**
     * 获取考勤状态的中文描述
     *
     * @param status 考勤状态代码
     * @return 状态描述
     */
    private String getStatusTextShort(int status) {
        switch (status) {
            case 0:
                return "正常";
            case 1:
                return "迟到";
            case 2:
                return "早退";
            case 3:
                return "缺勤";
            default:
                return "未知";
        }
    }
}
