package com.ruoyi.framework.security.context;

import com.ruoyi.common.enums.ClientType;

/**
 * 登录上下文持有者，用于存储登录请求的客户端类型
 * 
 * @author ruoyi
 */
public class LoginContextHolder
{
    private static final ThreadLocal<ClientType> clientTypeHolder = new ThreadLocal<>();

    public static ClientType getClientType()
    {
        return clientTypeHolder.get();
    }

    public static void setClientType(ClientType clientType)
    {
        clientTypeHolder.set(clientType);
    }

    public static void clearClientType()
    {
        clientTypeHolder.remove();
    }
} 