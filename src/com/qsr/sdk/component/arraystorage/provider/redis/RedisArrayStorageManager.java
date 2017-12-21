package com.qsr.sdk.component.arraystorage.provider.redis;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.arraystorage.ArrayStorage;
import com.qsr.sdk.component.arraystorage.ArrayStorageManager;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/20.
 */
public class RedisArrayStorageManager extends AbstractComponent implements ArrayStorageManager {

    private final JedisPool pool;

    private static final Map<String, RedisArrayStorage> REDIS_ARRAY_STORAGE_MAP = new HashMap<>(); // RedisArrayStorage对象的缓存

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisArrayStorageManager.class);

    public RedisArrayStorageManager(Provider provider, Map<?, ?> config) {
        super(provider, config);

        JedisPoolConfig poolconfig = new JedisPoolConfig();
        poolconfig.setMaxTotal(ParameterUtil.integerParam(config, "maxTotal",
                500));

        poolconfig.setMaxIdle(ParameterUtil.integerParam(config, "maxIdel", 5));
        poolconfig.setMaxWaitMillis(ParameterUtil.integerParam(config,
                "maxWait", 100) * 1000);

        poolconfig.setTestOnBorrow(ParameterUtil.booleanParam(config,
                "testOnBorrow", false));
        pool = new JedisPool(poolconfig, ParameterUtil.stringParam(config,
                "host"), ParameterUtil.integerParam(config, "port", 6379));
    }

    // 从内存的缓存中获取，或者从Redis中加载
    @Override
    public <T> ArrayStorage<T> getOrLoadArrayStorage(String name, Class<T> entityClass) {
        RedisArrayStorage<T> redisArrayStorage = REDIS_ARRAY_STORAGE_MAP.get(name);
        if (redisArrayStorage == null) {
            try {
                redisArrayStorage = new RedisArrayStorage<>(pool, name, entityClass);
                REDIS_ARRAY_STORAGE_MAP.put(name, redisArrayStorage);
            } catch (ApiException e) {
                LOGGER.error("RedisArrayStorageManager.getOrLoadArrayStorage:Load Failed", e);
            }
        }
        return redisArrayStorage;
    }

    // 从Redis中加载
/*    @Override
    public <T> ArrayStorage<T> loadArrayStorage(String name, Class<T> entityClass) throws ApiException {
        RedisArrayStorage<T> redisArrayStorage = new RedisArrayStorage<>(pool, name);
        REDIS_ARRAY_STORAGE_MAP.put(name, redisArrayStorage);
        return redisArrayStorage;
    }*/

    // 在内存和Redis中都创建一个新的
    @Override
    public <T> ArrayStorage<T> createArrayStorage(String name, Class<T> entityClass, int capacity, long expireAt) {
        RedisArrayStorage<T> redisArrayStorage = new RedisArrayStorage<>(pool, name, entityClass, capacity, expireAt);
        REDIS_ARRAY_STORAGE_MAP.put(name, redisArrayStorage);
        return redisArrayStorage;
    }
}
