package com.touchbiz.db.starter.configuration;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author steven
 */
@Slf4j
@Configuration
public class DataSourceConfiguration {

    @Value("${spring.application.name}")
    private String groupId;

    /**
     * 对配置进行加解密处理
     */
    @Value("${cryptoConfig:true}")
    private Boolean cryptoConfig;

    @Autowired
    private NacosConfigProperties configService;

    private DataSourceConfig config(NacosConfigProperties configService) throws NacosException {
        DataSourceConfig config = new DataSourceConfig();
        String configInfo = configService.configServiceInstance().getConfig("DB_CONFIG", groupId, 3000);

        if(StringUtils.isEmpty(configInfo)){
            throw new RuntimeException("数据库连接字符串没有配置,dataId: DB_CONFIG,groupId: " + groupId);
        }
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(configInfo));
            config.setDbUrl(properties.getProperty("dbUrl"));
            String key = "driverClassName";
            if (properties.containsKey(key)) {
                config.setDriverClassName(properties.getProperty(key));
            }
            key = "initialSize";
            if (properties.containsKey(key)) {
                config.setInitialSize(Integer.parseInt(properties.getProperty(key)));
            }
            key = "maxActive";
            if (properties.containsKey(key)) {
                config.setMaxActive(Integer.parseInt(properties.getProperty(key)));
            }
            key = "maxWait";
            if (properties.containsKey(key)) {
                config.setMaxWait(Integer.parseInt(properties.getProperty(key)));
            }
            key = "minIdle";
            if (properties.containsKey(key)) {
                config.setMinIdle(Integer.parseInt(properties.getProperty(key)));
            }
            key = "password";
            config.setPassword(properties.getProperty(key));
            key = "userName";
            config.setUsername(properties.getProperty(key));
        } catch (IOException e) {
            log.error("DataSourceConfig->error->", e);
        }
        return config;
    }

    /**
     * 配置DataSource
     *
     * @return
     * @throws SQLException
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource druidDataSource() throws Exception {
        DataSourceConfig config = config(configService);
        log.warn("DataSourceConfig:{}", config);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(config.getUsername());
        //对密码进行解密操作
        druidDataSource.setPassword(config.getPassword());
        druidDataSource.setUrl(config.getDbUrl());
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setInitialSize(config.getInitialSize());
        druidDataSource.setMinIdle(config.getMinIdle());
        druidDataSource.setMaxActive(config.getMaxActive());
        druidDataSource.setMaxWait(config.getMaxWait());
        druidDataSource.setUseGlobalDataSourceStat(true);
        druidDataSource.setDriverClassName(config.getDriverClassName());
        druidDataSource.setValidationQuery("SELECT 1 FROM DUAL");
        druidDataSource.setPoolPreparedStatements(true);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
//        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(50);
        Properties properties = new Properties();
        properties.put("druid.stat.mergeSql", "true");
        properties.put("druid.stat.slowSqlMillis", "100");
        druidDataSource.setConnectProperties(properties);
        return druidDataSource;
    }


}