package com.ruoyi.common.core.domain.model;

import com.ruoyi.common.enums.ClientType;

/**
 * 用户登录对象
 * 
 * @author ruoyi
 */
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;
    
    /**
     * 客户端类型（pc或minapp）
     */
    private String clientType;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
    
    public String getClientType()
    {
        return clientType;
    }

    public void setClientType(String clientType)
    {
        this.clientType = clientType;
    }
    
    /**
     * 获取枚举类型的客户端类型
     */
    public ClientType getClientTypeEnum()
    {
        return ClientType.fromValue(clientType);
    }
}
