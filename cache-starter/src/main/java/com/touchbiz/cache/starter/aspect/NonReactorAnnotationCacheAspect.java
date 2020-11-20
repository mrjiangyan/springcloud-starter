package com.touchbiz.cache.starter.aspect;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.cache.starter.annotation.NonReactorCacheable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author zhangxuezhen
 * @description: 缓存标签切面处理器
 * @date 2020/10/159:41 上午
 */
@Aspect
@Component
@Slf4j
public class NonReactorAnnotationCacheAspect extends AbstractAnnotationCacheAspect {

    private final CacheAspectSupport reactorCacheAspectSupport;

    NonReactorAnnotationCacheAspect(final IRedisTemplate redisTemplate) {
        this.reactorCacheAspectSupport = new CacheAspectSupport(redisTemplate);
    }

    /**
     * 定义切点
     */

    @Around("@annotation(redisCache)")
    public Object around(ProceedingJoinPoint joinPoint, NonReactorCacheable redisCache) throws Throwable {

        final Method method = getMethod(joinPoint);
        final Object[] args = joinPoint.getArgs().clone();

        if(!getEnableCache()){
            return joinPoint.proceed(args);
        }

        final CacheOperationInvoker aspectJInvoker = () -> {
            try {
                return joinPoint.proceed(args);
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapper(ex);
            }
        };

        try {
            return reactorCacheAspectSupport.execute(redisCache, aspectJInvoker, method, args);
        } catch (CacheOperationInvoker.ThrowableWrapper th) {
            log.error("Failure to proceed reactor cache. method : {}, arguments : {}", method.getName(), args, th.getOriginal());
        } catch (Exception e) {
            log.error("Failure to execute reactor cache aspect support. method : {}, arguments : {}", method.getName(), args, e);
        }

        return joinPoint.proceed(args);
    }


}

