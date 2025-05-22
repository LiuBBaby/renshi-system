package com.ruoyi.attendance.domain;

import java.time.LocalDateTime;

/**
 * 考勤评价数据传输对象
 */
public class AttendanceEvaluationDTO {
    private String employeeName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer checkInStatus; // 0:正常 1:迟到 3:缺勤
    private Integer checkOutStatus; // 0:正常 2:早退 3:缺勤
    private String attendanceStatusText; // 考勤状态文本 (e.g., 正常, 迟到, 早退, 缺勤)
    private String evaluation; // 考勤评价文本 (e.g., 优秀, 良好, 一般, 未评价)

    // Getters and Setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Integer getCheckInStatus() { return checkInStatus; }

    public void setCheckInStatus(Integer checkInStatus) { this.checkInStatus = checkInStatus; }

    public Integer getCheckOutStatus() { return checkOutStatus; }

    public void setCheckOutStatus(Integer checkOutStatus) { this.checkOutStatus = checkOutStatus; }

    public String getAttendanceStatusText() {
        return attendanceStatusText;
    }

    public void setAttendanceStatusText(String attendanceStatusText) {
        this.attendanceStatusText = attendanceStatusText;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }
} 