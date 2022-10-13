package com.touchbiz.db.starter.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 * @Author pangqn
 * @Date  2022-10-13
 *
 */
@Slf4j
//@Component
@Intercepts(
		{
				@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
				@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
		}
)
public class MybatisQueryInterceptor implements Interceptor {

		@Override
		public Object intercept(Invocation invocation) throws Throwable {
			Object[] args = invocation.getArgs();
			MappedStatement ms = (MappedStatement) args[0];
			Object parameterObject = args[1];
			RowBounds rowBounds = (RowBounds) args[2];
			ResultHandler resultHandler = (ResultHandler) args[3];
			Executor executor = (Executor) invocation.getTarget();
			CacheKey cacheKey;
			BoundSql boundSql;
			//由于逻辑关系，只会进入一次
			if(args.length == 4){
				//4 个参数时
				boundSql = ms.getBoundSql(parameterObject);
				cacheKey = executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);
			} else {
				//6 个参数时
				cacheKey = (CacheKey) args[4];
				boundSql = (BoundSql) args[5];
			}
			//TODO 自己要进行的各种处理
			//注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
			return executor.query(ms, parameterObject, rowBounds, resultHandler, cacheKey, boundSql);
		}

		@Override
		public Object plugin(Object target) {
			return Plugin.wrap(target, this);
		}

		@Override
		public void setProperties(Properties properties) {
		}
}
