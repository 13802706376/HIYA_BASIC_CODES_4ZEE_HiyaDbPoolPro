package com.hiya.ee.dbpool.common;

import java.sql.Connection;
import java.util.LinkedList;

public class HiyaConnectionPool
{

    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public HiyaConnectionPool(int initialSize)
    {
        if (initialSize > 0)
        {
            for (int i = 0; i < initialSize; i++)
            {
                pool.addLast(HiyaConnectionDriver.createConection());
            }
        }
    }

    public void releaseConnection(Connection connection)
    {
        if (connection != null)
        {
            synchronized (pool)
            {
                // 连接释放后 要进行通知 这样其他消费者能够感知池中已经归还了一个连接
                pool.addLast(connection);
                // pool.notifyAll();//all
                pool.notify();// all
            }
        }
    }

    public Connection fetchConnection(long mills) throws InterruptedException
    {
        synchronized (pool)
        {
            // 超时
            if (mills <= 0)
            {
                while (pool.isEmpty())
                {
                    pool.wait();
                }
                return pool.removeFirst();
            } else
            {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0)
                {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty())
                {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}