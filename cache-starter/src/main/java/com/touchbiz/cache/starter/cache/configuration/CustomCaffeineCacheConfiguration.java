//package com.touchbiz.cache.starter.cache.configuration;
//
//
//import java.time.Duration;
//
//public class CustomCaffeineCacheConfiguration{
//
//    private final Duration ttl;
//    private final Integer maximumSize;
//    private final Long expireAfterAccess;
//
//    public CustomCaffeineCacheConfiguration(Duration ttl, Integer maximumSize, Long expireAfterAccess){
//        this.ttl=ttl;
//        this.maximumSize=maximumSize;
//        this.expireAfterAccess=expireAfterAccess;
//    }
//
//    public static CustomCaffeineCacheConfiguration  createDefaultCustomCacheConfiguration(){
//        return new CustomCaffeineCacheConfiguration(Duration.ZERO,500,600L);
//    }
//
//}