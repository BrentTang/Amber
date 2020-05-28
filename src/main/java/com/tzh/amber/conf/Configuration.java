package com.tzh.amber.conf;

import com.tzh.amber.conf.ConfigurationBean;
import com.tzh.amber.loader.ConfigurationLoader;
import com.tzh.amber.loader.impl.PropertiesConfigurationLoader;
import com.tzh.amber.loader.impl.XMLConfigurationLoader;

/**
 * @author 豪
 * @title: Configuration
 * @projectName Java
 * @description: TODO
 * @date 2019/6/25 21:43
 */
public class Configuration {

    //private static final String PROPERTIES = ".properties";
    private static final String XML = ".xml";

    public ConfigurationBean getConfigurationBean(String confPath) {

        ConfigurationLoader loader = null;
        if (confPath != null && confPath.endsWith(XML)) {
            // 加载xml
            loader = new XMLConfigurationLoader();
        } else {
            loader = new PropertiesConfigurationLoader();
        }
        return loader.load(confPath);
    }

}
