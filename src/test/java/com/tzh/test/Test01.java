package com.tzh.test;

import com.tzh.amber.conf.Configuration;
import com.tzh.amber.conf.ConfigurationBean;
import com.tzh.amber.loader.impl.PropertiesConfigurationLoader;
import com.tzh.amber.pool.AmberPool;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 豪
 * @title: Test01
 * @projectName Java
 * @description: TODO
 * @date 2019/6/25 8:51
 */
public class Test01 {

    @Test
    public void test01() {
        /*ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(0, 99);*/
        //2,147,483,647
        //System.out.println(Integer.MAX_VALUE);
        System.out.println(this.getClass().getResource(""));
    }

    @Test
    public void Test02() {
        ConfigurationBean configurationBean = new Configuration().getConfigurationBean("classpath:amber.properties");
        System.out.println(configurationBean);
    }

    /*public void testPool() {
        ConfigurationBean config = new Configuration().getConfigurationBean("classpath:amber.properties");
        final DataSource pool = new AmberPool(config);
        int currenCount = 5;
        final CountDownLatch countDown = new CountDownLatch(currenCount);
        try {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        countDown.countDown();
                        Connection conn = pool.getConnection();
                        boolean query = query(conn);
                        if (query)
                            System.out.println(Thread.currentThread().getName() + "：query成功！");

                        conn.close();
                    } catch (Exception e) {

                    }
                }
            };

            for (int i = 0; i < currenCount; i++) {
                new Thread(runnable).start();
            }

            countDown.await();

            System.out.println("----test结束-----");

        } catch (Exception e) {

        }
    }*/

    private static final String testSQL = "SELECT sleep(1);";
    public static boolean query(Connection conn) {
        try {
            PreparedStatement preparedStatement =
                    conn.prepareStatement(testSQL);
            ResultSet resultSet =
                    preparedStatement.executeQuery();
            //Thread.sleep(600);
            if (resultSet.next()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询失败");
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
            String user = "root";
            String password = "tang";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {

    }

    @Test
    public void testPool() {
        ConfigurationBean config = new Configuration().getConfigurationBean("classpath:amber.properties");
        final DataSource pool = new AmberPool(config);

        int currenCount = 1000;
        final CountDownLatch countDown = new CountDownLatch(currenCount);
        final CountDownLatch time = new CountDownLatch(currenCount);
        final AtomicInteger success = new AtomicInteger();
        try {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        countDown.countDown();
                        Connection conn = pool.getConnection();
                        System.out.println(Thread.currentThread().getName() + "：开始query！");
                        boolean query = query(conn);
                        if (query) {
                            success.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + "：query成功！");
                        }

                        conn.close();
                        time.countDown();
                    } catch (Exception e) {

                    }
                }
            };

            for (int i = 0; i < currenCount; i++) {
                new Thread(runnable).start();
            }

            countDown.await();
            long start = System.currentTimeMillis();

            time.await();
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
            System.out.println("成功率：" + (double) success.get() / (double) currenCount * 100 + "%");
            System.out.println("最后池中数量：" + ((AmberPool) pool).getQueue().size());
            System.out.println("计数器：" + ((AmberPool) pool).getSize().get());
        } catch (Exception e) {

        }
    }

    @Test
    public void testConn() {
        //耗时：1953
        //耗时：1748
        //耗时：1972
        int currenCount = 200;
        final CountDownLatch countDown = new CountDownLatch(currenCount);
        final CountDownLatch time = new CountDownLatch(currenCount);
        final AtomicInteger success = new AtomicInteger();
        try {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        countDown.countDown();
                        Connection conn = getConnection();
                        System.out.println(Thread.currentThread().getName() + "：开始query！");
                        boolean query = query(conn);
                        if (query)
                            System.out.println(Thread.currentThread().getName() + "：query成功！");

                        conn.close();
                        time.countDown();
                    } catch (Exception e) {

                    }
                }
            };

            for (int i = 0; i < currenCount; i++) {
                new Thread(runnable).start();
            }

            countDown.await();
            long start = System.currentTimeMillis();

            time.await();
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {

        }
    }

    /*public static void main(String[] args) {


    }*/

}
