package com.touchbiz.cache.starter.redislock;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * 锁key
     */
    String key() default "";

    /**
     * key前缀
     */
    String prefix() default "";

    /**
     * 过期时间，单位毫秒
     */
    long expire() default 15000;

    /**
     * 重试次数
     */
    int retryTimes() default 0;

    /**
     * 重试间隔,单位毫秒
     */
    int retryInterval() default 1000;

    /**
     * 绑定类型(作用于key的生成)
     */
    BindType bindType() default BindType.DEFAULT;

    /**
     * 绑定参数索引，从0开始，与 bindType.ARGS_INDEX 组合使用
     */
    int[] bindArgsIndex() default 0;

    /**
     * 对象参数属性 示例：ClassName.field, 与bingType.OBJECT_PROPERTIES 组合使用
     */
    String[] properties() default "";

    /**
     * 失败策略
     */
    ErrorStrategy errorStrategy() default ErrorStrategy.THROW_EXCEPTION;

    /**
     * 参数key绑定类型
     */
    enum BindType {
        /**
         * 默认，将所有参数toString
         */
        DEFAULT,
        /**
         * 参数索引，从0开始
         */
        ARGS_INDEX,
        /**
         * 对象属性
         */
        OBJECT_PROPERTIES
    }


    /**
     * 获取锁失败策略
     */
    enum ErrorStrategy {
        /**
         * 抛异常
         */
        THROW_EXCEPTION,

        /**
         * 返回NULL
         */
        RETURN_NULL
    }
}
