package com.touchbiz.cache.starter.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangxuezhen
 * @description: 缓存注解
 * @date 2020/10/159:38 上午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonReactorCacheable {
    // 缓存的key 支持EL表达式
    String redisKey();

    // 缓存的key的前缀
    String keyPrefix() default "";

    // 超时时间， 单位TimeUnit.Second 秒
    int timeOut() default 30;

    // 是否打印缓存日志
    boolean isPrintLog() default false;

    /**
     * 是否忽略异常的接口返回数据
     * @return
     */
    boolean ignoreError() default true;

    /**
     * 如果ignoreError = true的情况下，缓存的有效时间。默认是2秒钟
     * @return
     */
    int errorExpire() default 5;
}
