package com.tzh.amber.loader;

import com.tzh.amber.conf.ConfigurationBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class ConfigurationLoader {

    public static final String CLASSPATHURLPREFIX = "file:/";
    public static final String DEFAULTCONFPATH = "conf/amber.properties";
    public static final String CLASSPATHPREFIX = "classpath:";
    public static final String CLASSPATH =
            ConfigurationLoader.class
                    .getClassLoader()
                    .getResource("")
                    .toString().substring(CLASSPATHURLPREFIX.length());

    /**
     * 模板方法，获取配置Bean
     * @param confPath
     * @return
     */
    public ConfigurationBean load(String confPath) {
        String path = parsePath(confPath);
        return load(read(path));
    }

    /**
     * 解析路径
     * @param confPath
     * @return
     */
    public String parsePath(String confPath) {

        if (!(confPath != null && confPath.trim().length() > 0))
            return DEFAULTCONFPATH;

        if (confPath.substring(0, CLASSPATHPREFIX.length()).equalsIgnoreCase(CLASSPATHPREFIX)) {
            return CLASSPATH + confPath.substring(CLASSPATHPREFIX.length());
        } else {
            return confPath;
        }
    }

    /**
     * 读取配置文件
     * @param confPath
     * @return
     */
    public InputStream read(String confPath) {

        try {
            return new FileInputStream(confPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("找不到：" + confPath);
        }
    }

    /**
     * 实现类取加载
     * @param in
     * @return
     */
    public abstract ConfigurationBean load(InputStream in);

}
