package com.hello.zhifu.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 配置文件读取
 * 
 */
public class SettingsUtil {

    private final static String configFile = "settings.properties";

    private static SettingsUtil instance;
    private Configuration config;

    private SettingsUtil() throws ConfigurationException {
        config = new PropertiesConfiguration(configFile);
    }

    public static synchronized SettingsUtil getInstance() {
        if (instance == null) {
            try {
                instance = new SettingsUtil();
            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * 读取Integer类型配置
     * 
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        return getInteger(key, 0);
    }

    public Integer getInteger(String key, int defaultValue) {
        return config.getInteger(key, defaultValue);
    }

    /**
     * 读取String类型配置
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return config.getString(key);
    }

}
