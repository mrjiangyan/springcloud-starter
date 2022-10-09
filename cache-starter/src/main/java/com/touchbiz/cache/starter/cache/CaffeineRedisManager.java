//package com.touchbiz.cache.starter.cache;
//
//import java.util.stream.Collectors;
//
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//
//import java.util.Collection;
//
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.util.ObjectUtils;
//
//public class CaffeineRedisManager implements CacheManager {
//
//    private final CaffeineCacheManager caffeineCacheManager;
//
//    private final RedisCacheManager redisCacheManager;
//
//    private final static CaffeineRedisCacheContainer customCaffeineRedisCacheContainer
//            = new CaffeineRedisCacheContainer();
//
//    public CaffeineRedisManager(CaffeineCacheManager caffeineCacheManager, RedisCacheManager redisCacheManager) {
//        this.caffeineCacheManager = caffeineCacheManager;
//        this.redisCacheManager = redisCacheManager;
//    }
//
//    @Override
//    public Cache getCache(String name) {
//        Cache cache = this.customCaffeineRedisCacheContainer.getCacheContainer().get(name);
//        if (cache == null) {
//            synchronized (this.customCaffeineRedisCacheContainer) {
//                cache = this.customCaffeineRedisCacheContainer.getCacheContainer().get(name);
//                if (cache == null) {
//                    cache = createCache(name);
//                    this.customCaffeineRedisCacheContainer.getCacheContainer().put(name, cache);
//                }
//            }
//        }
//        return cache;
//    }
//
//    public Cache createCache(String name) {
//        if (!ObjectUtils.isEmpty(caffeineCacheManager) && !ObjectUtils.isEmpty(redisCacheManager)) {
//            return new CaffeineRedisCache(name, caffeineCacheManager.getCache(name), redisCacheManager.getCache(name));
//        } else if (ObjectUtils.isEmpty(caffeineCacheManager)) {
//            return new CaffeineRedisCache(name, null, redisCacheManager.getCache(name));
//        } else if (ObjectUtils.isEmpty(redisCacheManager)) {
//            return new CaffeineRedisCache(name, caffeineCacheManager.getCache(name), null);
//        } else {
//            return new CaffeineRedisCache(name, null, null);
//        }
//    }
//
//    @Override
//    public Collection<String> getCacheNames() {
//        return customCaffeineRedisCacheContainer
//                .getCacheContainer()
//                .entrySet()
//                .stream()
//                .collect(Collectors.mapping(item -> item.getKey(), Collectors.toList()));
//    }
//}
