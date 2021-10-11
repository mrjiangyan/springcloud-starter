package com.touchbiz.cache.starter;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedisTemplate {

    RedisTemplate<String, Object> getRedisTemplate();

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    boolean expire(String key, long time);

    /**
     * 根据key获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpire(String key);

    boolean hasKey(String key);

    @SuppressWarnings("unchecked")
    void del(String... key);

    Object get(String key);

    <T> T getObject(String key, Class<T> beanType);

    boolean set(String key, Object value);

    boolean set(String key, Object value, long time);

    <T> boolean setObject(String key, T value, long time);

    long incr(String key, long delta);

    long incr(String key);

    long decr(String key, long delta);

    Object hget(String key, String item);

    Map<Object, Object> hmget(String key);

    boolean hmset(String key, Map<String, Object> map);

    boolean hmset(String key, Map<String, Object> map, long time);

    boolean hset(String key, String item, Object value);

    boolean hset(String key, String item, Object value, long time);

    void hdel(String key, Object... item);

    boolean hHasKey(String key, String item);

    double hincr(String key, String item, double by);

    double hdecr(String key, String item, double by);

    Set<Object> sGet(String key);

    boolean sHasKey(String key, Object value);

    long sSet(String key, Object... values);

    long sSetAndTime(String key, long time, Object... values);

    long sGetSetSize(String key);

    long setRemove(String key, Object... values);

    List<Object> lGet(String key, long start, long end);

    long lGetListSize(String key);

    Object lGetIndex(String key, long index);

    boolean lSet(String key, Object value);

    boolean lSet(String key, Object value, long time);

    boolean lSet(String key, List<Object> value);

    boolean lSet(String key, List<Object> value, long time);

    boolean lUpdateIndex(String key, long index, Object value);

    long lRemove(String key, long count, Object value);

    <T> List<T> getObjectList(String key, Class<T> tClass);

    boolean setObjectList(String key, List<?> list, long time);

    /**
     * 抢占设置
     *
     * @param key
     * @param value
     * @return
     */
    Boolean setNx(String key, Object value);

    /**
     * 抢占设置加时间
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    Boolean setNx(String key, Object value, long timeout);

    /**
     * 先获取，没有并设置
     *
     * @param key
     * @param value
     * @return
     */
    Object getSet(String key, Object value);
}
