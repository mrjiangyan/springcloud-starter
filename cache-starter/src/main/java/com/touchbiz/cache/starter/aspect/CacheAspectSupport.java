package com.touchbiz.cache.starter.aspect;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.cache.starter.annotation.MonoCacheable;
import com.touchbiz.cache.starter.annotation.RedisCache;
import com.touchbiz.cache.starter.reactor.InternalCacheConfig;
import com.touchbiz.cache.starter.reactor.ReactorCache;
import com.touchbiz.cache.starter.reactor.SpringMonoCache;
import com.touchbiz.cache.starter.reactor.SpringNonReactorCache;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
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
@Slf4j
class CacheAspectSupport extends AbstractAnnotationCacheAspect {

    private final ConcurrentHashMap<Method, Class<?>> cacheTypeMap;

    /**
     * @EnableCaching 启动类加
     */
    private final IRedisTemplate redisTemplate;

    CacheAspectSupport(final IRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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

        return execute(invoker, returnType, config,annotation);
    }


    Object execute(RedisCache annotation, final CacheOperationInvoker invoker, final Method method, final Object[] args) {
        Assert.notNull(invoker, "CacheOperationInvoker should be not null");
        Assert.notNull(method, "Method should be not null");
        Assert.notEmpty(args, "Method argument should be not empty");

        final String key = getCacheKey(method, annotation.redisKey(),annotation.keyPrefix(),args);

        final Class<?> returnType = getCacheType(method);

        final InternalCacheConfig config = new InternalCacheConfig(key,annotation.isPrintLog(),
                annotation.timeOut(),
                annotation.ignoreError(),
                annotation.errorExpire());

        return execute(invoker, returnType, config,annotation);
    }

    @SuppressWarnings("unchecked")
    private Object execute(final CacheOperationInvoker invoker,final Class<?> type, final InternalCacheConfig config,final Object annonation) {
        Assert.notNull(invoker, "Proceed invoker should be not null");
        Assert.notNull(config, "Cache key should be not null");
        Assert.notNull(type, "Cache type should be not null");

        final ReactorCache cacheResolver = getCacheResolver(type,annonation instanceof MonoCacheable);
        return cacheResolver.find(invoker, config);
    }

    @SuppressWarnings("unchecked")
    private ReactorCache getCacheResolver(final Class<?> type,Boolean isMono) {
        return isMono ? new SpringMonoCache(redisTemplate, type) : new SpringNonReactorCache(redisTemplate, type);
    }


    private Class<?> getCacheType(final Method method) {
        try {
            return cacheTypeMap.computeIfAbsent(method, m -> {
                if(!(m.getGenericReturnType() instanceof ParameterizedType)){
                    return (Class<?>) m.getGenericReturnType();
                }
                final ParameterizedType parameterizedType = (ParameterizedType) m.getGenericReturnType();
                if(!parameterizedType.getRawType().equals(Mono.class) && !parameterizedType.getRawType().equals(Flux.class)){
                    ParameterizedTypeImpl type = (ParameterizedTypeImpl) parameterizedType;
                    return type.getRawType();
                }
                var clazz = parameterizedType.getActualTypeArguments()[0];
                if(clazz instanceof ParameterizedTypeImpl){
                    ParameterizedTypeImpl type = (ParameterizedTypeImpl)clazz;
                    return type.getRawType();
                }
                return (Class<?>) clazz;

            });
        } catch (Exception e) {
            log.error("",e);
            throw new IllegalArgumentException("Invalid return type");
        }

    }

}