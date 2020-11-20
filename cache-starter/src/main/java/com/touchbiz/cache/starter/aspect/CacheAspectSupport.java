package com.touchbiz.cache.starter.aspect;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.cache.starter.annotation.MonoCacheable;
import com.touchbiz.cache.starter.annotation.NonReactorCacheable;
import com.touchbiz.cache.starter.reactor.InternalCacheConfig;
import com.touchbiz.cache.starter.reactor.ReactorCache;
import com.touchbiz.cache.starter.reactor.SpringMonoCache;
import com.touchbiz.cache.starter.reactor.SpringNonReactorCache;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for reactor caching supports,
 * such as the {@link org.springframework.cache.interceptor.CacheAspectSupport}.
 *
 * @author Minkiu Kim
 */
class CacheAspectSupport extends AbstractAnnotationCacheAspect {

    //    private final ConcurrentHashMap<Cache, ReactorCache> cacheResolverMap;
    private final ConcurrentHashMap<Method, Class<?>> cacheTypeMap;

    /**
     * @EnableCaching 启动类加
     */
    private final IRedisTemplate redisTemplate;

    CacheAspectSupport(final IRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
//        this.cacheResolverMap = new ConcurrentHashMap<>();
        this.cacheTypeMap = new ConcurrentHashMap<>();
    }

    Object execute(MonoCacheable annotation, final CacheOperationInvoker invoker, final Method method, final Object[] args) {
        Assert.notNull(invoker, "CacheOperationInvoker should be not null");
        Assert.notNull(method, "Method should be not null");
        Assert.notEmpty(args, "Method argument should be not empty");

        final String key = getCacheKey(method, annotation.redisKey(),annotation.keyPrefix(),args);

        final Class<?> returnType = getCacheType(method);

        final InternalCacheConfig config = new InternalCacheConfig(key,annotation.isPrintLog(),
                annotation.timeOut(),
                annotation.ignoreError(),
                annotation.errorExpire());

        return execute(invoker.invoke(), returnType, config,annotation);
    }


    Object execute(NonReactorCacheable annotation, final CacheOperationInvoker invoker, final Method method, final Object[] args) {
        Assert.notNull(invoker, "CacheOperationInvoker should be not null");
        Assert.notNull(method, "Method should be not null");
        Assert.notEmpty(args, "Method argument should be not empty");

        final String key = getCacheKey(method, annotation.redisKey(),annotation.keyPrefix(),args);

        final Class<?> returnType = getCacheType(method);

        final InternalCacheConfig config = new InternalCacheConfig(key,annotation.isPrintLog(),
                annotation.timeOut(),
                annotation.ignoreError(),
                annotation.errorExpire());

        return execute(invoker.invoke(), returnType, config,annotation);
    }

    @SuppressWarnings("unchecked")
    private Object execute(final Object proceed,final Class<?> type, final InternalCacheConfig config,final Object annonation) {
        Assert.notNull(proceed, "Proceed object should be not null");
        Assert.notNull(config, "Cache key should be not null");
        Assert.notNull(type, "Cache type should be not null");

        final ReactorCache cacheResolver = getCacheResolver(type,annonation instanceof MonoCacheable);
        return cacheResolver.find(annonation instanceof MonoCacheable ? proceed: Mono.just(proceed), config);
    }

    @SuppressWarnings("unchecked")
    private ReactorCache getCacheResolver(final Class<?> type,Boolean isMono) {
        if(isMono){
            return new SpringMonoCache(redisTemplate, type);
        }
        return new SpringNonReactorCache(redisTemplate, type);
    }


    private Class<?> getCacheType(final Method method) {
        try {
            return cacheTypeMap.computeIfAbsent(method, m -> {
                final ParameterizedType parameterizedType = (ParameterizedType) m.getGenericReturnType();
                if(parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedTypeImpl){
                    ParameterizedTypeImpl type = (ParameterizedTypeImpl) parameterizedType.getActualTypeArguments()[0];
                    return type.getRawType();
                }
                if(parameterizedType instanceof ParameterizedTypeImpl){
                    ParameterizedTypeImpl type = (ParameterizedTypeImpl) parameterizedType;
                    return type.getRawType();
                }
                ParameterizedTypeImpl type = (ParameterizedTypeImpl) parameterizedType.getActualTypeArguments()[0];
                return type.getRawType();
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid return type");
        }

    }

}