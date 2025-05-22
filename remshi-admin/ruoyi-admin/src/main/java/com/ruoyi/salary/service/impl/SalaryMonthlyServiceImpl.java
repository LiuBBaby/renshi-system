package com.ruoyi.salary.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.salary.mapper.SalaryMonthlyMapper;
import com.ruoyi.salary.domain.SalaryMonthly;
import com.ruoyi.salary.service.ISalaryMonthlyService;
import com.ruoyi.salary.mapper.SalaryBaseInfoMapper;
import com.ruoyi.salary.domain.SalaryBaseInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.service.IEmpInfoService;
import com.ruoyi.system.domain.EmpInfo;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.attendance.service.IAttendanceMonthlyService;
import com.ruoyi.attendance.domain.AttendanceMonthly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 月度薪资报表Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-26
 */
@Service
public class SalaryMonthlyServiceImpl implements ISalaryMonthlyService 
{
    private static final Logger logger = LoggerFactory.getLogger(SalaryMonthlyServiceImpl.class);
    
    @Autowired
    private SalaryMonthlyMapper salaryMonthlyMapper;
    
    @Autowired
    private IEmpInfoService empInfoService;
    
    @Autowired
    private SalaryBaseInfoMapper salaryBaseInfoMapper;
    
    @Autowired
    private IAttendanceMonthlyService attendanceMonthlyService;

    /**
     * 查询月度薪资报表
     * 
     * @param id 月度薪资报表主键
     * @return 月度薪资报表
     */
    @Override
    public SalaryMonthly selectSalaryMonthlyById(Integer id)
    {
        return salaryMonthlyMapper.selectSalaryMonthlyById(id);
    }

    /**
     * 查询月度薪资报表列表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 月度薪资报表
     */
    @Override
    public List<SalaryMonthly> selectSalaryMonthlyList(SalaryMonthly salaryMonthly)
    {
        return salaryMonthlyMapper.selectSalaryMonthlyList(salaryMonthly);
    }

    /**
     * 新增月度薪资报表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 结果
     */
    @Override
    public int insertSalaryMonthly(SalaryMonthly salaryMonthly)
    {
        // 验证员工是否存在
        String empId = salaryMonthly.getEmpId();
        EmpInfo empInfo = empInfoService.selectEmpInfoByEmpInfoId(empId);
        if (empInfo == null)
        {
            throw new ServiceException("员工ID不存在，请检查输入的员工工号");
        }
        
        // 检查同一员工在同一年月是否已存在记录
        SalaryMonthly param = new SalaryMonthly();
        param.setEmpId(empId);
        param.setYear(salaryMonthly.getYear());
        param.setMonth(salaryMonthly.getMonth());
        List<SalaryMonthly> existList = salaryMonthlyMapper.selectSalaryMonthlyList(param);
        if (existList != null && !existList.isEmpty())
        {
            throw new ServiceException("该员工在" + salaryMonthly.getYear() + "年" + salaryMonthly.getMonth() + "月已有工资记录，不允许重复添加");
        }
        
        salaryMonthly.setCreateTime(DateUtils.getNowDate());
        return salaryMonthlyMapper.insertSalaryMonthly(salaryMonthly);
    }

    /**
     * 修改月度薪资报表
     * 
     * @param salaryMonthly 月度薪资报表
     * @return 结果
     */
    @Override
    public int updateSalaryMonthly(SalaryMonthly salaryMonthly)
    {
        // 验证员工是否存在
        String empId = salaryMonthly.getEmpId();
        if (empId != null && !empId.isEmpty())
        {
            EmpInfo empInfo = empInfoService.selectEmpInfoByEmpInfoId(empId);
            if (empInfo == null)
            {
                throw new ServiceException("员工ID不存在，请检查输入的员工工号");
            }
        }
        
        // 检查是否与其他记录冲突(同一员工同一年月不能有多条记录)
        SalaryMonthly param = new SalaryMonthly();
        param.setEmpId(empId);
        param.setYear(salaryMonthly.getYear());
        param.setMonth(salaryMonthly.getMonth());
        List<SalaryMonthly> existList = salaryMonthlyMapper.selectSalaryMonthlyList(param);
        
        // 如果存在记录，且不是当前正在修改的记录
        for (SalaryMonthly exist : existList)
        {
            if (!exist.getId().equals(salaryMonthly.getId()))
            {
                throw new ServiceException("该员工在" + salaryMonthly.getYear() + "年" + salaryMonthly.getMonth() + "月已有其他工资记录，不能修改为此年月");
            }
        }
        
        // 获取当前员工的基本工资信息
        SalaryBaseInfo baseParam = new SalaryBaseInfo();
        baseParam.setEmpId(empId);
        List<SalaryBaseInfo> baseInfoList = salaryBaseInfoMapper.selectSalaryBaseInfoList(baseParam);
        
        // 如果有基本工资信息，根据考勤数据重新计算各项工资
        if (baseInfoList != null && !baseInfoList.isEmpty())
        {
            // 获取该员工最新的基本工资信息
            SalaryBaseInfo baseInfo = baseInfoList.get(0);
            for (SalaryBaseInfo info : baseInfoList) {
                if (info.getUpdateTime() != null && 
                    (baseInfo.getUpdateTime() == null || 
                        info.getUpdateTime().after(baseInfo.getUpdateTime()))) {
                    baseInfo = info;
                }
            }
            
            // 获取标准出勤天数
            Integer standardAttendanceDays = baseInfo.getStandardAttendanceDays();
            if (standardAttendanceDays == null || standardAttendanceDays <= 0) {
                standardAttendanceDays = 22; // 默认标准出勤天数
            }
            
            // 获取基本工资
            BigDecimal baseSalary = salaryMonthly.getBaseSalary();
            if (baseSalary == null) {
                baseSalary = baseInfo.getBaseSalary() != null ? baseInfo.getBaseSalary() : new BigDecimal(0);
                salaryMonthly.setBaseSalary(baseSalary);
            }
            
            // 使用基本工资信息中的日工资和小时工资，如果有的话
            BigDecimal dailySalary;
            BigDecimal hourSalary;
            
            if (baseInfo.getDaySalary() != null && baseInfo.getDaySalary().compareTo(BigDecimal.ZERO) > 0) {
                dailySalary = baseInfo.getDaySalary();
            } else if (standardAttendanceDays > 0) {
                dailySalary = baseSalary.divide(new BigDecimal(standardAttendanceDays), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                dailySalary = BigDecimal.ZERO;
            }
            
            if (baseInfo.getHourSalary() != null && baseInfo.getHourSalary().compareTo(BigDecimal.ZERO) > 0) {
                hourSalary = baseInfo.getHourSalary();
            } else {
                hourSalary = dailySalary.divide(new BigDecimal(8), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // 1. 计算考勤工资
            int actualDays = salaryMonthly.getActualAttendanceDays() != null ? salaryMonthly.getActualAttendanceDays() : 0;
            int absenceDays = salaryMonthly.getAbsenceDays() != null ? salaryMonthly.getAbsenceDays() : 0;
            BigDecimal attendanceDays = new BigDecimal(actualDays - absenceDays);
            
            BigDecimal attendanceSalary = dailySalary.multiply(attendanceDays);
            salaryMonthly.setAttendanceSalary(attendanceSalary);
            
            // 2. 异常扣款
            int lateEarlyCount = salaryMonthly.getLateEarlyCount() != null ? salaryMonthly.getLateEarlyCount() : 0;
            BigDecimal lateDeduction = baseInfo.getLateDeduction();
            if (lateDeduction == null) {
                lateDeduction = new BigDecimal(50); // 默认每次迟到/早退扣款50元
            }
            
            BigDecimal deductionAmount = lateDeduction.multiply(new BigDecimal(lateEarlyCount));
            salaryMonthly.setDeductionAmount(deductionAmount);
            
            // 3. 请假扣款
            int leaveDays = salaryMonthly.getLeaveDays() != null ? salaryMonthly.getLeaveDays() : 0;
            int allowedLeaveDays = 2; // 假设每月允许请假2天不扣款
            int excessLeaveDays = Math.max(0, leaveDays - allowedLeaveDays);
            
            // 使用基本工资信息中的旷工扣款比例
            BigDecimal absenceDeductionRatio = baseInfo.getAbsenceDeductionRatio();
            if (absenceDeductionRatio == null) {
                absenceDeductionRatio = new BigDecimal("1.5"); // 默认1.5倍
            }
            
            BigDecimal leaveDeduction = dailySalary.multiply(new BigDecimal(excessLeaveDays))
                    .multiply(absenceDeductionRatio);
            salaryMonthly.setLeaveDeduction(leaveDeduction);
            
            // 4.1 加班费
            BigDecimal overtimeHours = salaryMonthly.getOvertimeHours();
            if (overtimeHours == null) {
                overtimeHours = BigDecimal.ZERO;
            }
            
            // 使用基本工资信息中的加班工资比例
            BigDecimal overtimeRatio = baseInfo.getOvertimeRatio();
            if (overtimeRatio == null) {
                overtimeRatio = new BigDecimal("1.5"); // 默认1.5倍
            }
            
            BigDecimal overtimePay = hourSalary.multiply(overtimeHours).multiply(overtimeRatio);
            salaryMonthly.setOvertimePay(overtimePay);
            
            // 4.2 出差补贴
            int businessTripDays = salaryMonthly.getBusinessTripDays() != null ? salaryMonthly.getBusinessTripDays() : 0;
            
            // 使用基本工资信息中的出差补贴比例
            BigDecimal businessTripRatio = baseInfo.getBusinessTripRatio();
            if (businessTripRatio == null) {
                businessTripRatio = new BigDecimal("1.5"); // 默认1.5倍
            }
            
            BigDecimal tripAllowance = dailySalary.multiply(new BigDecimal(businessTripDays))
                    .multiply(businessTripRatio);
            salaryMonthly.setBusinessTripAllowance(tripAllowance);
            
            // 设置绩效工资，如果没有设置，则使用基本工资信息中的绩效基数
            BigDecimal performancePay = salaryMonthly.getPerformancePay();
            if (performancePay == null || performancePay.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal performanceBase = baseInfo.getPerformanceBase();
                if (performanceBase != null) {
                    salaryMonthly.setPerformancePay(performanceBase);
                } else {
                    salaryMonthly.setPerformancePay(new BigDecimal(0));
                }
            }
            
            // 计算总工资
            BigDecimal totalSalary = attendanceSalary
                    .add(salaryMonthly.getPerformancePay())
                    .add(overtimePay)
                    .add(tripAllowance)
                    .subtract(deductionAmount)
                    .subtract(leaveDeduction);
            
            salaryMonthly.setTotalSalary(totalSalary);
        }
        
        salaryMonthly.setUpdateTime(DateUtils.getNowDate());
        return salaryMonthlyMapper.updateSalaryMonthly(salaryMonthly);
    }

    /**
     * 批量删除月度薪资报表
     * 
     * @param ids 需要删除的月度薪资报表主键
     * @return 结果
     */
    @Override
    public int deleteSalaryMonthlyByIds(Integer[] ids)
    {
        return salaryMonthlyMapper.deleteSalaryMonthlyByIds(ids);
    }

    /**
     * 删除月度薪资报表信息
     * 
     * @param id 月度薪资报表主键
     * @return 结果
     */
    @Override
    public int deleteSalaryMonthlyById(Integer id)
    {
        return salaryMonthlyMapper.deleteSalaryMonthlyById(id);
    }

    /**
     * 批量生成月度薪资报表
     * 
     * @param year 年份
     * @param month 月份
     * @return 生成的记录数
     */
    @Override
    @Transactional
    public int generateMonthlySalary(Integer year, Integer month)
    {
        // 参数校验
        if (year == null || month == null) {
            throw new ServiceException("年份和月份不能为空");
        }
        
        if (month < 1 || month > 12) {
            throw new ServiceException("月份必须在1-12之间");
        }
        
        // 检查当前年月是否已过
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1; // 月份从0开始，所以加1
        
        if (year > currentYear || (year == currentYear && month > currentMonth)) {
            throw new ServiceException("不能生成未来月份的工资");
        }
        
        // 获取所有在职员工
        EmpInfo empParam = new EmpInfo();
        empParam.setStatus(0L); // 0表示在职状态
        List<EmpInfo> empList = empInfoService.selectEmpInfoList(empParam);
        
        if (empList == null || empList.isEmpty()) {
            throw new ServiceException("没有找到任何在职员工");
        }
        
        int successCount = 0;
        int existCount = 0;
        List<String> noBaseInfoEmpIds = new ArrayList<>();
        List<String> otherFailedEmpIds = new ArrayList<>();
        
        // 遍历所有员工，生成工资记录
        for (EmpInfo emp : empList) {
            try {
                // 检查该员工在此年月是否已有工资记录
                SalaryMonthly existCheck = new SalaryMonthly();
                existCheck.setEmpId(emp.getEmpInfoId());
                existCheck.setYear(year);
                existCheck.setMonth(month);
                List<SalaryMonthly> existList = salaryMonthlyMapper.selectSalaryMonthlyList(existCheck);
                
                if (existList != null && !existList.isEmpty()) {
                    existCount++; // 记录已存在的数量，但不视为失败
                    continue; // 跳过已有记录的员工
                }
                
                // 查询员工基本工资信息
                SalaryBaseInfo baseParam = new SalaryBaseInfo();
                baseParam.setEmpId(emp.getEmpInfoId());
                List<SalaryBaseInfo> baseInfoList = salaryBaseInfoMapper.selectSalaryBaseInfoList(baseParam);
                
                if (baseInfoList == null || baseInfoList.isEmpty()) {
                    noBaseInfoEmpIds.add(emp.getEmpInfoId());
                    continue; // 跳过没有基本工资信息的员工
                }
                
                // 获取该员工最新的基本工资信息
                SalaryBaseInfo baseInfo = baseInfoList.get(0);
                for (SalaryBaseInfo info : baseInfoList) {
                    if (info.getUpdateTime() != null && 
                        (baseInfo.getUpdateTime() == null || 
                         info.getUpdateTime().after(baseInfo.getUpdateTime()))) {
                        baseInfo = info;
                    }
                }
                
                // 查询考勤信息
                AttendanceMonthly attendParam = new AttendanceMonthly();
                attendParam.setEmpId(emp.getEmpInfoId());
                attendParam.setYear(year);
                attendParam.setMonth(month);
                List<AttendanceMonthly> attendList = attendanceMonthlyService.selectAttendanceMonthlyList(attendParam);
                
                // 生成月度工资记录
                SalaryMonthly salary = new SalaryMonthly();
                salary.setEmpId(emp.getEmpInfoId());
                salary.setYear(year);
                salary.setMonth(month);
                salary.setBaseSalary(baseInfo.getBaseSalary());
                
                // 默认值初始化
                salary.setActualAttendanceDays(0);
                salary.setAbsenceDays(0);
                salary.setLateEarlyCount(0);
                salary.setLeaveDays(0);
                salary.setOvertimeHours(new BigDecimal(0));
                salary.setBusinessTripDays(0);
                salary.setDeductionAmount(new BigDecimal(0));
                salary.setLeaveDeduction(new BigDecimal(0));
                salary.setOvertimePay(new BigDecimal(0));
                salary.setBusinessTripAllowance(new BigDecimal(0));
                salary.setPerformancePay(new BigDecimal(0));
                
                // 根据考勤情况计算实际出勤天数和扣除
                if (attendList != null && !attendList.isEmpty()) {
                    AttendanceMonthly attendance = attendList.get(0);
                    
                    // 设置实际出勤天数
                    if (attendance.getActualWorkDays() != null) {
                        salary.setActualAttendanceDays(attendance.getActualWorkDays());
                    }
                    
                    // 设置缺勤天数
                    if (attendance.getAbsentDays() != null) {
                        salary.setAbsenceDays(attendance.getAbsentDays());
                    }
                    
                    // 设置迟到早退次数
                    int lateEarlyCount = 0;
                    if (attendance.getLateTimes() != null) {
                        lateEarlyCount += attendance.getLateTimes();
                    }
                    if (attendance.getEarlyTimes() != null) {
                        lateEarlyCount += attendance.getEarlyTimes();
                    }
                    salary.setLateEarlyCount(lateEarlyCount);
                    
                    // 设置请假天数
                    if (attendance.getLeaveDays() != null) {
                        salary.setLeaveDays(attendance.getLeaveDays());
                    }
                    
                    // 设置加班小时数
                    if (attendance.getOvertimeHours() != null) {
                        salary.setOvertimeHours(new BigDecimal(attendance.getOvertimeHours()));
                    }
                    
                    // 设置出差天数
                    if (attendance.getBusinessTripDays() != null) {
                        salary.setBusinessTripDays(attendance.getBusinessTripDays());
                    }
                }
                
                // 根据公式计算薪资项目
                // 1. 计算考勤工资：基本工资÷标准出勤天数×（实际出勤天数–缺勤天数）
                Integer standardAttendanceDays = baseInfo.getStandardAttendanceDays();
                if (standardAttendanceDays == null || standardAttendanceDays <= 0) {
                    standardAttendanceDays = 22; // 默认标准出勤天数
                }
                
                BigDecimal baseSalary = salary.getBaseSalary();
                if (baseSalary == null) {
                    baseSalary = new BigDecimal(0);
                }
                
                // 使用基本工资信息中的日工资和小时工资，如果有的话
                BigDecimal dailySalary;
                BigDecimal hourSalary;
                
                if (baseInfo.getDaySalary() != null && baseInfo.getDaySalary().compareTo(BigDecimal.ZERO) > 0) {
                    dailySalary = baseInfo.getDaySalary();
                } else if (standardAttendanceDays > 0) {
                    dailySalary = baseSalary.divide(new BigDecimal(standardAttendanceDays), 2, BigDecimal.ROUND_HALF_UP);
                } else {
                    dailySalary = BigDecimal.ZERO;
                }
                
                if (baseInfo.getHourSalary() != null && baseInfo.getHourSalary().compareTo(BigDecimal.ZERO) > 0) {
                    hourSalary = baseInfo.getHourSalary();
                } else {
                    hourSalary = dailySalary.divide(new BigDecimal(8), 2, BigDecimal.ROUND_HALF_UP);
                }
                
                // 计算考勤工资
                int actualDays = salary.getActualAttendanceDays() != null ? salary.getActualAttendanceDays() : 0;
                int absenceDays = salary.getAbsenceDays() != null ? salary.getAbsenceDays() : 0;
                BigDecimal attendanceDays = new BigDecimal(actualDays - absenceDays);
                
                BigDecimal attendanceSalary = dailySalary.multiply(attendanceDays);
                salary.setAttendanceSalary(attendanceSalary);
                
                // 2. 异常扣款：迟到早退次数×固定扣款
                int lateEarlyCount = salary.getLateEarlyCount() != null ? salary.getLateEarlyCount() : 0;
                BigDecimal lateDeduction = baseInfo.getLateDeduction();
                if (lateDeduction == null) {
                    lateDeduction = new BigDecimal(50); // 默认每次迟到/早退扣款50元
                }
                
                BigDecimal deductionAmount = lateDeduction.multiply(new BigDecimal(lateEarlyCount));
                salary.setDeductionAmount(deductionAmount);
                
                // 3. 请假扣款：超额请假×日工资×系数
                int leaveDays = salary.getLeaveDays() != null ? salary.getLeaveDays() : 0;
                int allowedLeaveDays = 2; // 假设每月允许请假2天不扣款
                int excessLeaveDays = Math.max(0, leaveDays - allowedLeaveDays);
                
                // 使用基本工资信息中的旷工扣款比例
                BigDecimal absenceDeductionRatio = baseInfo.getAbsenceDeductionRatio();
                if (absenceDeductionRatio == null) {
                    absenceDeductionRatio = new BigDecimal("1.5"); // 默认1.5倍
                }
                
                BigDecimal leaveDeduction = dailySalary.multiply(new BigDecimal(excessLeaveDays))
                        .multiply(absenceDeductionRatio);
                salary.setLeaveDeduction(leaveDeduction);
                
                // 4. 加班及出差绩效
                // 4.1 加班费：加班小时×小时工资×加班工资比例
                BigDecimal overtimeHours = salary.getOvertimeHours();
                if (overtimeHours == null) {
                    overtimeHours = BigDecimal.ZERO;
                }
                
                // 使用基本工资信息中的加班工资比例
                BigDecimal overtimeRatio = baseInfo.getOvertimeRatio();
                if (overtimeRatio == null) {
                    overtimeRatio = new BigDecimal("1.5"); // 默认1.5倍
                }
                
                BigDecimal overtimePay = hourSalary.multiply(overtimeHours).multiply(overtimeRatio);
                salary.setOvertimePay(overtimePay);
                
                // 4.2 出差补贴：出差天数×日工资×出差补贴比例
                int businessTripDays = salary.getBusinessTripDays() != null ? salary.getBusinessTripDays() : 0;
                
                // 使用基本工资信息中的出差补贴比例
                BigDecimal businessTripRatio = baseInfo.getBusinessTripRatio();
                if (businessTripRatio == null) {
                    businessTripRatio = new BigDecimal("1.5"); // 默认1.5倍
                }
                
                BigDecimal tripAllowance = dailySalary.multiply(new BigDecimal(businessTripDays))
                        .multiply(businessTripRatio);
                salary.setBusinessTripAllowance(tripAllowance);
                
                // 设置绩效工资，使用基本工资信息中的绩效基数
                BigDecimal performanceBase = baseInfo.getPerformanceBase();
                if (performanceBase != null) {
                    salary.setPerformancePay(performanceBase);
                } else {
                    salary.setPerformancePay(new BigDecimal(0));
                }
                
                // 计算总工资
                BigDecimal totalSalary = attendanceSalary
                        .add(salary.getPerformancePay())
                        .add(overtimePay)
                        .add(tripAllowance)
                        .subtract(deductionAmount)
                        .subtract(leaveDeduction);
                
                salary.setTotalSalary(totalSalary);
                salary.setStatus("0"); // 0表示未结算
                
                // 设置创建信息
                salary.setCreateTime(DateUtils.getNowDate());
                salary.setCreateBy(SecurityUtils.getUsername());
                
                // 保存记录
                salaryMonthlyMapper.insertSalaryMonthly(salary);
                successCount++;
                
            } catch (Exception e) {
                otherFailedEmpIds.add(emp.getEmpInfoId() + "(" + e.getMessage() + ")");
            }
        }
        
        // 构造返回信息，只有在必要的情况下才抛出异常
        StringBuilder message = new StringBuilder();
        
        // 正常生成的记录
        if (successCount > 0) {
            message.append("成功生成").append(successCount).append("条工资记录");
        } else if (existCount == empList.size()) {
            // 所有记录都已存在的情况
            throw new ServiceException(year + "年" + month + "月的工资记录已全部生成，无需重复操作");
        } else if (successCount == 0) {
            // 没有成功生成任何记录
            message.append("未能生成任何工资记录");
        }
        
        // 已存在的记录（不视为错误，仅作提示）
        if (existCount > 0) {
            if (message.length() > 0) message.append("，");
            message.append("跳过").append(existCount).append("条已存在的记录");
        }
        
        // 缺少基本工资信息的员工（视为警告）
        if (!noBaseInfoEmpIds.isEmpty()) {
            if (message.length() > 0) message.append("，");
            message.append("有").append(noBaseInfoEmpIds.size()).append("名员工缺少基本工资信息");
        }
        
        // 其他错误（视为严重错误）
        if (!otherFailedEmpIds.isEmpty()) {
            throw new ServiceException(message.toString() + "，但有" + otherFailedEmpIds.size() + 
                                    "条记录生成失败: " + String.join(", ", otherFailedEmpIds));
        }
        
        // 如果有必要的话，记录警告级别的日志
        if (!noBaseInfoEmpIds.isEmpty()) {
            logger.warn("员工{}缺少基本工资信息", String.join(", ", noBaseInfoEmpIds));
        }
        
        return successCount;
    }
}