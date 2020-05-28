package com.tzh.amber.utils;

import com.tzh.amber.pool.AmberPool;
import com.tzh.amber.utils.impl.NormalTask;
import com.tzh.amber.utils.impl.PoolTask;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 豪
 * @title: PoolTest
 * @projectName Java
 * @description: TODO
 * @date 2019/6/30 14:24
 */
public class TestTemplate {

    private CountDownLatch startLine;
    private CountDownLatch timer;
    private AtomicInteger success = new AtomicInteger();
    private Integer currentCount;

    private DataSource pool;

    public void initCountDownLatch(Integer currentCount) {
        startLine = new CountDownLatch(currentCount);
        timer = new CountDownLatch(currentCount);
    }

    public void initPool() {
        pool = new AmberPool("classpath:amber.properties");
    }

    public void testPool(int currentCount) {
        this.currentCount = new Integer(currentCount);
        initCountDownLatch(currentCount);
        initPool();

        for (int i = 0; i < currentCount; i++) {
            new Thread(new PoolTask(this.startLine, this.timer, this.success, this.currentCount, this.pool)).start();
        }

        try {
            startLine.await();
            long start = System.currentTimeMillis();

            timer.await();
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
            System.out.println("成功率：" + (double) success.get() / (double) this.currentCount * 100 + "%");
            System.out.println("最后池中数量：" + ((AmberPool) pool).getQueue().size());
            System.out.println("计数器：" + ((AmberPool) pool).getSize().get());
        } catch (Exception e) {

        }
    }

    public void testConnection(int currentCount) {
        this.currentCount = new Integer(currentCount);
        initCountDownLatch(currentCount);

        for (int i = 0; i < currentCount; i++) {
            new Thread(new NormalTask(this.startLine, this.timer, this.success, this.currentCount)).start();
        }

        try {
            startLine.await();
            long start = System.currentTimeMillis();

            timer.await();
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
            System.out.println("成功率：" + (double) success.get() / (double) this.currentCount * 100 + "%");
        } catch (Exception e) {

        }
    }

    @Test
    public void test01() {
        //testPool(100);
        testConnection(110);
    }

    private static final String testSQL = "SELECT sleep(1);";
    public static boolean task1(Connection connection) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(testSQL);
            ResultSet resultSet =
                    preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询失败");
        }
    }

}
