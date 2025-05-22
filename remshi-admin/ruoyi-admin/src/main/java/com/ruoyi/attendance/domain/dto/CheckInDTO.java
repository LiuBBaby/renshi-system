package com.ruoyi.attendance.domain.dto;

import java.math.BigDecimal;

/**
 * 员工打卡请求参数DTO
 * 
 * @author ruoyi
 */
public class CheckInDTO {
    /** 打卡类型（checkin-上班打卡，checkout-下班打卡） */
    private String type;
    
    /** 打卡时间 */
    private String time;
    
    /** 打卡日期 */
    private String date;
    
    /** 经度 */
    private BigDecimal longitude;
    
    /** 纬度 */
    private BigDecimal latitude;
    
    /** 地址信息 */
    private String address;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 