package com.tzh.amber.loader.impl;

import com.tzh.amber.conf.ConfigurationBean;
import com.tzh.amber.loader.ConfigurationLoader;
import com.tzh.reflect.TZHInvoke;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author 豪
 * @title: PropertiesConfigurationLoader
 * @projectName Java
 * @description: TODO
 * @date 2019/6/25 19:27
 */
public class PropertiesConfigurationLoader extends ConfigurationLoader {

    public static final String PROPERTYPREFIX = "amber.";

    /**
     * 加载properties文件
     * @param in
     * @return
     */
    @Override
    public ConfigurationBean load(InputStream in) {

        Properties p = new Properties();
        try {
            p.load(in);

            return confMapper(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 配置映射
     * @param p
     * @return
     */
    public ConfigurationBean confMapper(Properties p) {

        TZHInvoke invoke = new TZHInvoke(new ConfigurationBean());
        Field[] fields = invoke.getDeclaredFields();
        try {
            for (Field field : fields) {
                Object val = p.get(PROPERTYPREFIX + field.getName());
                if (val == null)
                    continue;
                invoke.setField(field, val);
            }
        } catch (Exception e) {
            throw new RuntimeException("配置读取失败！");
        }
        return (ConfigurationBean) invoke.getInstance();
    }

}
