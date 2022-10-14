package com.touchbiz.db.starter.mybatis;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Statement;
import java.util.Properties;


@Intercepts({
        @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }),
        @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
        @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class })})
public class SqlLogInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(SqlLogInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        DruidPooledPreparedStatement statement = ((DruidPooledPreparedStatement) invocation.getArgs()[0]);
        PreparedStatementProxyImpl preparedStatement = (PreparedStatementProxyImpl) statement.getRawPreparedStatement();
        String sql = preparedStatement.getRawObject().toString().replace("com.mysql.cj.jdbc.ClientPreparedStatement:", "");

        sql = sql.replaceAll("\\n","");

        log.info("sql: {}", sql);

        //执行结果
        Object returnValue = invocation.proceed();
        return returnValue;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
