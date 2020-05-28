package com.tzh.amber.utils.impl;

import com.tzh.amber.conf.ConfigurationBean;
import com.tzh.amber.utils.Task;
import com.tzh.amber.utils.TestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 豪
 * @title: PoolTask
 * @projectName Java
 * @description: TODO
 * @date 2019/6/30 14:34
 */
public class PoolTask extends Task {

    private DataSource pool;

    public PoolTask(CountDownLatch startLine, CountDownLatch timer, AtomicInteger success, Integer currentCount) {
        super(startLine, timer, success, currentCount);
    }

    public PoolTask(CountDownLatch startLine, CountDownLatch timer, AtomicInteger success, Integer currentCount, DataSource pool) {
        super(startLine, timer, success, currentCount);
        this.pool = pool;
    }

    public DataSource getPool() {
        return pool;
    }

    public void setPool(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean executeTask(Connection connection) {
        return TestTemplate.task1(connection);
    }
}
