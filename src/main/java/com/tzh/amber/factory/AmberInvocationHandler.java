package com.tzh.amber.factory;

import com.tzh.amber.pool.AmberPool;

import java.lang.reflect.InvocationHandler;
import java.sql.Connection;

public abstract class AmberInvocationHandler implements InvocationHandler {

    private AmberPool pool;
    private Connection conn;

    public AmberInvocationHandler(AmberPool pool, Connection conn) {
        this.pool = pool;
        this.conn = conn;
    }

    public AmberPool getPool() {
        return pool;
    }

    public void setPool(AmberPool pool) {
        this.pool = pool;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
