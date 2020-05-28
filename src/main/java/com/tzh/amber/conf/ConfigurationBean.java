package com.tzh.amber.conf;

/**
 * @author 豪
 * @title: ConfigurationBean
 * @projectName Java
 * @description: TODO
 * @date 2019/6/25 18:51
 */
public class ConfigurationBean {

    private String driver;
    private String url;
    private String username;
    private String password;

    private Integer initPoolSize = 10;
    private Integer maxPoolSize = 20;
    private Integer idleConnect = 16;
    private Long maxWaitTime = 1000l;
    private Integer stepSize = 3;

    public Integer getInitPoolSize() {
        return initPoolSize;
    }

    public void setInitPoolSize(Integer initPoolSize) {
        this.initPoolSize = initPoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getIdleConnect() {
        return idleConnect;
    }

    public void setIdleConnect(Integer idleConnect) {
        this.idleConnect = idleConnect;
    }

    public Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getStepSize() {
        return stepSize;
    }

    public void setStepSize(Integer stepSize) {
        this.stepSize = stepSize;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ConfigurationBean{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", initPoolSize=" + initPoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", idleConnect=" + idleConnect +
                ", maxWaitTime=" + maxWaitTime +
                ", stepSize=" + stepSize +
                '}';
    }
}
