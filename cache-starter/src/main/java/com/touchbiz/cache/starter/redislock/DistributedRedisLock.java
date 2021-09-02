package com.touchbiz.cache.starter.redislock;


import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

@Slf4j
public class DistributedRedisLock {

    private final RedisTemplate<Object, Object> redisTemplate;

    public static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    public DistributedRedisLock(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 加锁
     *
     * @param key           锁key
     * @param expire        过期时间
     * @param retryTimes    重试次数
     * @param retryInterval 重试间隔
     * @return true 加锁成功， false 加锁失败
     */
    public boolean lock(String key, long expire, int retryTimes, long retryInterval) {
        boolean result = setRedisLock(key, expire);
        /**
         * 如果获取锁失败，进行重试
         */
        while ((!result) && retryTimes-- > 0) {
            try {
                log.info("lock failed, retrying..." + retryTimes);
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedisLock(key, expire);
        }
        return result;
    }

    /**
     * 释放锁
     *
     * @param key 锁key
     * @return true 释放成功， false 释放失败
     */
    public boolean unLock(String key) {
        /**
         * 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
         * 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
         */
        var bytes = key.getBytes();
        try {
            RedisCallback<Boolean> callback = (connection) -> {
                return connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, bytes, bytes);
            };
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("release lock occured an exception", e);
        } finally {

        }
        return false;
    }

    /**
     * 设置redis锁
     *
     * @param key    锁key
     * @param expire 过期时间
     * @return true 设置成功，false 设置失败
     */
    private boolean setRedisLock(String key, long expire) {
        try {
            RedisCallback<Boolean> callback = (connection) -> {
                var bytes = key.getBytes();
                return connection.set(bytes, bytes, Expiration.milliseconds(expire), RedisStringCommands.SetOption.SET_IF_ABSENT);
            };
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("set redis error", e);
        }
        return false;
    }
}
