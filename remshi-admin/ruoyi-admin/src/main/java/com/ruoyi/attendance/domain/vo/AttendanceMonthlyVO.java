package com.ruoyi.attendance.domain.vo;

import java.util.List;
import java.util.Map;

import com.ruoyi.attendance.domain.AttendanceInfo;

/**
 * 月度考勤数据视图对象
 * 
 * @author ruoyi
 */
public class AttendanceMonthlyVO {
    /** 年份 */
    private Integer year;
    
    /** 月份 */
    private Integer month;
    
    /** 统计数据 */
    private StatisticsVO statistics;
    
    /** 日历数据 */
    private List<CalendarDayVO> calendarData;
    
    /** 考勤记录 */
    private List<AttendanceInfo> records;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public StatisticsVO getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsVO statistics) {
        this.statistics = statistics;
    }

    public List<CalendarDayVO> getCalendarData() {
        return calendarData;
    }

    public void setCalendarData(List<CalendarDayVO> calendarData) {
        this.calendarData = calendarData;
    }

    public List<AttendanceInfo> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceInfo> records) {
        this.records = records;
    }
    
    /**
     * 统计数据视图对象
     */
    public static class StatisticsVO {
        /** 正常出勤天数 */
        private Integer normalDays;
        
        /** 迟到天数 */
        private Integer lateDays;
        
        /** 早退天数 */
        private Integer earlyDays;
        
        /** 缺勤天数 */
        private Integer absenceDays;
        
        /** 加班天数 */
        private Integer overtimeDays;

        public Integer getNormalDays() {
            return normalDays;
        }

        public void setNormalDays(Integer normalDays) {
            this.normalDays = normalDays;
        }

        public Integer getLateDays() {
            return lateDays;
        }

        public void setLateDays(Integer lateDays) {
            this.lateDays = lateDays;
        }

        public Integer getEarlyDays() {
            return earlyDays;
        }

        public void setEarlyDays(Integer earlyDays) {
            this.earlyDays = earlyDays;
        }

        public Integer getAbsenceDays() {
            return absenceDays;
        }

        public void setAbsenceDays(Integer absenceDays) {
            this.absenceDays = absenceDays;
        }

        public Integer getOvertimeDays() {
            return overtimeDays;
        }

        public void setOvertimeDays(Integer overtimeDays) {
            this.overtimeDays = overtimeDays;
        }
    }
    
    /**
     * 日历天数据视图对象
     */
    public static class CalendarDayVO {
        /** 日期，格式为: yyyy-MM-dd */
        private String date;
        
        /** 是否为当前月 */
        private Boolean isCurrentMonth;
        
        /** 日号 */
        private Integer day;
        
        /** 状态码 */
        private Integer status;
        
        /** 状态文本 */
        private String statusText;
        
        /** 考勤记录ID */
        private Long recordId;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Boolean getIsCurrentMonth() {
            return isCurrentMonth;
        }

        public void setIsCurrentMonth(Boolean isCurrentMonth) {
            this.isCurrentMonth = isCurrentMonth;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }
    }
}
