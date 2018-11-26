package com.hiya.ee.dbpool.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
public class HiyaConnectionDriver
{

    static class ConnectionHandler implements InvocationHandler
    {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            if (method.getName().equals("commit"))
            {
                Thread.sleep(1000);
            }
            return null;
        }
    }

    // 创建一个connection的代理
    public static Connection createConection()
    {
        return (Connection) Proxy.newProxyInstance(HiyaConnectionDriver.class.getClassLoader(), new Class<?>[]
        { Connection.class }, new ConnectionHandler());
    }
}