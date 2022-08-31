package com.touchbiz.db.starter.annotation;

import java.lang.annotation.*;

/**
 * 动态table切换
 *
 * @author :zyf
 * @date:2020-04-25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicTable {
    /**
     * 需要动态解析的表名
     * @return
     */
    String value();
}
