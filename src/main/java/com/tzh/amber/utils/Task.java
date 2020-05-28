package com.tzh.amber.utils;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 豪
 * @title: Task
 * @projectName Java
 * @description: TODO
 * @date 2019/6/30 14:25
 */
public abstract class Task implements Runnable{

    private CountDownLatch startLine;
    private CountDownLatch timer;
    private AtomicInteger success;
    private Integer currentCount;

    public Task(CountDownLatch startLine, CountDownLatch timer, AtomicInteger success, Integer currentCount) {
        this.startLine = startLine;
        this.timer = timer;
        this.success = success;
        this.currentCount = currentCount;
    }

    public abstract Connection getConnection();

    public abstract boolean executeTask(Connection connection);

    @Override
    public void run() {
        try {
            startLine.countDown();
            Connection conn = getConnection();
            System.out.println(Thread.currentThread().getName() + "：任务开始！");
            boolean query = executeTask(conn);
            if (query) {
                success.incrementAndGet();
                System.out.println(Thread.currentThread().getName() + "：任务成功完成！");
            } else {
                System.out.println(Thread.currentThread().getName() + "：任务失败！");
            }

            conn.close();
            timer.countDown();
        } catch (Exception e) {

        }
    }

    public CountDownLatch getStartLine() {
        return startLine;
    }

    public void setStartLine(CountDownLatch startLine) {
        this.startLine = startLine;
    }

    public CountDownLatch getTimer() {
        return timer;
    }

    public void setTimer(CountDownLatch timer) {
        this.timer = timer;
    }

    public AtomicInteger getSuccess() {
        return success;
    }

    public void setSuccess(AtomicInteger success) {
        this.success = success;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }
}
