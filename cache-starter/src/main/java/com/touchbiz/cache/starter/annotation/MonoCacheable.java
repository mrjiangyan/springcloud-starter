package com.touchbiz.cache.starter.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that the result of invoking a {@link reactor.core.publisher.Mono}
 * type method can be cached.
 *
 * @author Minkiu Kim
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MonoCacheable {

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
     * 如果ignoreError = true的情况下，缓存的有效时间
     * @return
     */
    int errorExpire() default 5;
}