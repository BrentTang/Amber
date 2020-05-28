package com.tzh.amber.pool;

import com.tzh.amber.conf.Configuration;
import com.tzh.amber.conf.ConfigurationBean;
import com.tzh.amber.factory.AmberInvocationHandler;
import com.tzh.amber.factory.ProxyFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * @author 豪
 * @title: AbstractAmerPool
 * @projectName Java
 * @description: TODO
 * @date 2019/6/29 11:34
 */
public class AmberPool implements DataSource {

    // 配置上下文
    private ConfigurationBean config;
    // 连接容器
    private BlockingQueue<Connection> queue;
    // 默认使用公平锁
    private ReentrantLock lock;

    //private Integer size;
    private AtomicInteger size;

    private static final String testSQL = "SELECT 1;";

    public AmberPool(String config) {
        ConfigurationBean configurationBean = new Configuration().getConfigurationBean(config);
        initPool(configurationBean);
    }

    public AmberPool(ConfigurationBean config) {
        initPool(config);
    }

    /**
     * 初始化容器
     */
    public void initPool(ConfigurationBean config) {
        try {
            if (!checkConfigBean(config)) {
                throw new IllegalArgumentException("配置出现错误，数据源创建失败！");
            }
            this.config = config;
            queue = new LinkedBlockingQueue();
            lock = new ReentrantLock(true);

            size = new AtomicInteger();
            addConnection(config.getInitPoolSize());
            System.out.println("初始化后，池中连接数：" + queue.size() + "，实际连接：" + size.get());
        } catch (Exception e) {
            throw new IllegalArgumentException("初始化AmberPool失败");
        }
    }

    /**
     * 添加count个数据库连接到容器
     */
    public void addConnection(int count) {
        for (int i = 0; i < count; i++) {
            Connection connection = createProxyConnection();
            if (connection == null) {
                break;
            }
            queue.offer(connection);
        }
    }

    /**
     * 创建代理连接
     * @return
     */
    public Connection createProxyConnection() {
        return ensureConnection(wrapperConnection(createConnection()));
    }

    /**
     * 创建一个普通的数据库连接
     * @return
     */
    public Connection createConnection() {
        try {
            if (size.get() >= config.getMaxPoolSize()) {
                return null;
            }
            Class.forName(config.getDriver());
            System.out.println("*************创建一个连接*************");
            size.incrementAndGet();
            return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接数据库失败！");
        }
    }

    /**
     * 步进创建连接
     */
    public void stepAddConnection() {

        int free = config.getMaxPoolSize() - size.get();
        int count = free >= config.getStepSize() ?
                config.getStepSize() : free;

        addConnection(count);

    }

    /**
     * 获取代理连接
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {

        Connection conn = null;
        while (true) {
            try {
                lock.lock();
                // 获取连接成功
                conn = queue.poll();
                if (conn != null) {
                    //return wrapperConnection(conn);
                    return ensureConnection(conn);
                }
                // 获取连接失败，连接数是否小于最大连接数
                if (size.get() < config.getMaxPoolSize()) {
                    stepAddConnection();
                    conn = queue.poll();
                    //return wrapperConnection(conn);
                    return ensureConnection(conn);
                } else {
                    // 如果大于最大连接数，进行等待
                    conn = queue.poll(config.getMaxWaitTime(), TimeUnit.MILLISECONDS);
                    if (conn != null) {
                        //return wrapperConnection(conn);
                        return ensureConnection(conn);
                    }
                }
            } catch (Exception e) {

            } finally {
                lock.unlock();
                System.out.println("池中连接数：" + queue.size() + "，实际连接：" + size.get());
            }
        }
        //return null;
    }

    @Deprecated
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    /**
     * 确保连接可用
     * @param connection
     * @return
     */
    public Connection ensureConnection(Connection connection) {
        if (connection == null) {
            return createProxyConnection();
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(testSQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return createProxyConnection();
            }
        } catch (Exception e) {

        }
        return connection;
    }

    /**
     * 返回代理连接对象
     * @param connection
     * @return
     */
    public Connection wrapperConnection(Connection connection) {
        // ensureConnection(connection);
        // System.out.println("#########wrapperConnection   connection=" + connection);
        return ProxyFactory.createProxy(Connection.class, new AmberInvocationHandler(this, connection) {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                if (method.getName().equals("close")) {
                    AmberPool pool = this.getPool();
                    BlockingQueue<Connection> queue = pool.getQueue();
                    ConfigurationBean config = pool.getConfig();
                    //lock.lock();
                    try {
                        if (queue.size() < config.getIdleConnect()) {
                            queue.offer((Connection) proxy);
                            System.out.println("归还连接#########   当前池中连接：" + queue.size());
                        } else {
                            System.out.println("#########释放连接");
                            AtomicInteger size = pool.getSize();
                            size.decrementAndGet();
                            return method.invoke(this.getConn(), args);
                        }
                    } finally {
                        //lock.unlock();
                    }
                } else {
                    return method.invoke(this.getConn(), args);
                }
                return null;
            }
        });
    }

    /**
     * 校验配置
     * @param config
     * @return
     */
    public boolean checkConfigBean(ConfigurationBean config) {

        return true;

    }

    public AtomicInteger getSize() {
        return size;
    }

    public void setSize(AtomicInteger size) {
        this.size = size;
    }

    public ConfigurationBean getConfig() {
        return config;
    }

    public void setConfig(ConfigurationBean config) {
        this.config = config;
    }

    public BlockingQueue<Connection> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Connection> queue) {
        this.queue = queue;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
