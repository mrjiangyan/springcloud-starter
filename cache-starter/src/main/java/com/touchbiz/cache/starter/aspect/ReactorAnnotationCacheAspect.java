package com.touchbiz.cache.starter.aspect;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.cache.starter.annotation.MonoCacheable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Base class for reactor caching aspects,
 * such as the {@link org.springframework.cache.aspectj.AnnotationCacheAspect}.
 *
 * @author Minkiu Kim
 */
@Slf4j
@Aspect
@Component
class ReactorAnnotationCacheAspect extends AbstractAnnotationCacheAspect{

    private final CacheAspectSupport reactorCacheAspectSupport;

    ReactorAnnotationCacheAspect(final IRedisTemplate redisTemplate) {
        this.reactorCacheAspectSupport = new CacheAspectSupport(redisTemplate);
    }

    @Around("@annotation(redisCache) && " +
            "executionOfAnyPublicMonoMethod()")
    final Object around(final ProceedingJoinPoint joinPoint, MonoCacheable redisCache) throws Throwable {
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


    @Pointcut(value = "execution(reactor.core.publisher.Mono *(..))")
    private void executionOfAnyPublicMonoMethod() {
    }
}
