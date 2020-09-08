package com.touchbiz.cache.starter.redislock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Aspect
public class DistributedRedisLockAspect {

    private DistributedRedisLock distributedRedisLock;

    public DistributedRedisLockAspect(DistributedRedisLock distributedRedisLock) {
        this.distributedRedisLock = distributedRedisLock;
    }

    @Pointcut("@annotation(com.touchbiz.cache.starter.redislock.RedisLock)")
    private void redisLockPoint() {
    }

    @Around("redisLockPoint() && @annotation(redisLock)")
    public Object around(ProceedingJoinPoint pjp, RedisLock redisLock) throws Throwable {
        String key = redisLock.key();
        if (StringUtils.isBlank(key)) {
            Object[] args = pjp.getArgs();
            if (redisLock.bindType().equals(RedisLock.BindType.DEFAULT)) {
                key = StringUtils.join(args);
            } else if (redisLock.bindType().equals(RedisLock.BindType.ARGS_INDEX)) {
                key = getArgsKey(redisLock, args);
            } else if (redisLock.bindType().equals(RedisLock.BindType.OBJECT_PROPERTIES)) {
                key = getObjectPropertiesKey(redisLock, args);
            }
        }
        Assert.hasText(key, "key does not exist");
        String prefix = redisLock.prefix();
        boolean lock = distributedRedisLock.lock(prefix + key, redisLock.expire(), redisLock.retryTimes(), redisLock.retryInterval());
        if (!lock) {
            log.error("get lock failed : " + key);
            if (redisLock.errorStrategy().equals(RedisLock.ErrorStrategy.THROW_EXCEPTION)) {
                throw new RedisLockException("Get redis lock failed");
            }
            return null;
        }
        log.info("get lock success : {}", key);
        try {
            return pjp.proceed();
        } finally {
            boolean result = distributedRedisLock.unLock(prefix + key);
            log.info("release lock : {} {}", prefix + key, result ? " success" : " failed");
        }
    }

    /**
     * 通过绑定的args生成key
     *
     * @param redisLock redisLock注解
     * @param args      所有参数
     * @return key
     */
    private String getArgsKey(RedisLock redisLock, Object[] args) {
        int[] index = redisLock.bindArgsIndex();
        Assert.notEmpty(Collections.singletonList(index), "ArgsIndex is empty");

        int len = index.length;
        Object[] indexArgs = new Object[index.length];
        for (int i = 0; i < len; i++) {
            indexArgs[i] = args[index[i]];
        }
        return StringUtils.join(indexArgs);
    }

    /**
     * 通过绑定的对象属性生成key
     *
     * @param redisLock redisLock注解
     * @param args      所有参数
     * @return key
     */
    private String getObjectPropertiesKey(RedisLock redisLock, Object[] args) throws NoSuchFieldException, IllegalAccessException {

        String[] properties = redisLock.properties();
        List<Object> keylist = new ArrayList<>(properties.length);

        // 可以通过className获取args的位置
        Map<String, Integer> classNamesArgsIndex = getClassNameArgsIndex(args);
        // 可以通过className获取Class类型
        Map<String, Class<?>> classNameClass = getClassNameClass(args);

        for (String ppts : properties) {
            String[] classProperties = StringUtils.split(ppts, ".");
            String className = classProperties[0];
            String propertiesName = classProperties[1];
            Object argObject = args[classNamesArgsIndex.get(className)];

            Class<?> clazz = classNameClass.get(className);
            Field field = clazz.getDeclaredField(propertiesName);
            field.setAccessible(true);
            Object object = field.get(argObject);
            keylist.add(object);
        }
        return StringUtils.join(keylist.toArray());
    }

    /**
     * 获取类名和参数位置的对应关系
     *
     * @param args 所有参数
     * @return Map<类名, 参数位置>
     */
    private Map<String, Integer> getClassNameArgsIndex(Object[] args) {
        int len = args.length;
        Map<String, Integer> nameIndex = new HashMap<>();
        for (int i = 0; i < len; i++) {
            String name = StringUtils.substringAfterLast(args[i].getClass().toString(), ".");
            nameIndex.put(name, i);
        }
        return nameIndex;
    }

    /**
     * 获取类名和类的对应关系
     *
     * @param args 所有参数
     * @return Map<类名, 类>
     */
    private Map<String, Class<?>> getClassNameClass(Object[] args) {
        int len = args.length;
        Map<String, Class<?>> nameClass = new HashMap<>();
        for (Object arg : args) {
            Class<?> clazz = arg.getClass();
            String name = StringUtils.substringAfterLast(clazz.toString(), ".");
            nameClass.put(name, clazz);
        }
        return nameClass;
    }
}