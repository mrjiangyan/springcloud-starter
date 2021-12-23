package com.touchbiz.db.starter.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author steven
 */
@Slf4j
@Configuration
@ConditionalOnClass(DruidDataSource.class)
public class MySqlDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.dataSource")
    public MySqlDataSourceConfig config() {
        MySqlDataSourceConfig config = new MySqlDataSourceConfig();
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
        MySqlDataSourceConfig config = config();
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