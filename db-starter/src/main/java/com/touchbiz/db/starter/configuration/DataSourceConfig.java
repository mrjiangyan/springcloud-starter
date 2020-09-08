package com.touchbiz.db.starter.configuration;

import lombok.Data;

//@RefreshScope
//@Component
@Data
public class DataSourceConfig {

    //@Value("${db.dbUrl}")
    private String dbUrl;

    //@Value("${db.username}")
    private String username;

    //@Value("${db.password}")
    private String password;

    //@Value("${db.driverClassName:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    //@Value("${db.initialSize:0}")
    private int initialSize;

    //@Value("${db.minIdle:10}")
    private int minIdle;

    //@Value("${db.maxActive:100}")
    private int maxActive;

    //@Value("${db.maxWait:60000}")
    private int maxWait;

}