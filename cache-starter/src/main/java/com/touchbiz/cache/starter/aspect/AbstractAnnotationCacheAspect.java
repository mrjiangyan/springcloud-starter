package com.touchbiz.cache.starter.aspect;

import com.touchbiz.cache.starter.SpelParser;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@RefreshScope
abstract class AbstractAnnotationCacheAspect {

    @Value("${enableCache:true}")
    @Getter
    protected Boolean enableCache;

    /**
     * 获取返回类型
     *
     * @param point
     * @return
     */
    protected Class getAnnotatedReturnType(ProceedingJoinPoint point) {
        return getMethod(point).getReturnType();
    }

    /**
     * 获取返回类型
     *
     * @param point
     * @return
     */
    protected Method getMethod(ProceedingJoinPoint point) {
        //获取方法
        return ((MethodSignature) (point.getSignature())).getMethod();
    }

    /**
     * 获取行参作为key
     *
     * @return
     */
    protected String getCacheKey(Method method, String redisKey, String keyPrefix,Object[] args) {
        // 获取行参列表
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        String key = SpelParser.getKey(redisKey, parameterNames, args);
        if (!StringUtils.isEmpty(keyPrefix)) {
            key = keyPrefix + key;
        }
        return key;
    }
}
