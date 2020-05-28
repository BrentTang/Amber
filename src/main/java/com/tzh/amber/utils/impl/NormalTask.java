package com.tzh.amber.utils.impl;

import com.tzh.amber.utils.Task;
import com.tzh.amber.utils.TestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 豪
 * @title: NormalTask
 * @projectName Java
 * @description: TODO
 * @date 2019/6/30 15:02
 */
public class NormalTask extends Task {
    public NormalTask(CountDownLatch startLine, CountDownLatch timer, AtomicInteger success, Integer currentCount) {
        super(startLine, timer, success, currentCount);
    }

    @Override
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/mysqllearn?characterEncoding=utf-8";
            String user = "root";
            String password = "tang";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean executeTask(Connection connection) {
        return TestTemplate.task1(connection);
    }
}
