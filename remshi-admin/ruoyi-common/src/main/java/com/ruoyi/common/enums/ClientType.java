package com.ruoyi.common.enums;

/**
 * 客户端类型
 * 
 * @author ruoyi
 */
public enum ClientType
{
    /**
     * PC端
     */
    PC("pc"),

    /**
     * 小程序端
     */
    MINAPP("minapp");

    private final String value;

    ClientType(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
    
    /**
     * 根据值获取枚举
     */
    public static ClientType fromValue(String value)
    {
        if (value == null)
        {
            return PC; // 默认为PC端
        }
        
        for (ClientType type : ClientType.values())
        {
            if (type.value.equalsIgnoreCase(value))
            {
                return type;
            }
        }
        
        return PC; // 默认为PC端
    }
} 