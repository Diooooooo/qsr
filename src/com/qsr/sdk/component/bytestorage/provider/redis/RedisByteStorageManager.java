package com.qsr.sdk.component.bytestorage.provider.redis;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.bytestorage.ByteStorage;
import com.qsr.sdk.component.bytestorage.ByteStorageManager;
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
public class RedisByteStorageManager extends AbstractComponent implements ByteStorageManager {

    private final JedisPool pool;

    private static final Map<String, RedisByteStorage> REDIS_BYTE_STORAGE_MAP = new HashMap<>(); // RedisByteStorage对象的缓存

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisByteStorageManager.class);

    public RedisByteStorageManager(Provider provider, Map<?, ?> config) {
        super(provider, config);

        JedisPoolConfig poolconfig = new JedisPoolConfig();
        poolconfig.setMaxTotal(ParameterUtil.integerParam(config, "maxTotal", 500));
        poolconfig.setMaxIdle(ParameterUtil.integerParam(config, "maxIdel", 5));
        poolconfig.setMaxWaitMillis(ParameterUtil.integerParam(config, "maxWait", 100) * 1000);
        poolconfig.setTestOnBorrow(ParameterUtil.booleanParam(config, "testOnBorrow", false));

        pool = new JedisPool(poolconfig, ParameterUtil.stringParam(config,
                "host"), ParameterUtil.integerParam(config, "port", 6379));
    }

    // 从内存的缓存中获取，或者从Redis中加载
    @Override
    public ByteStorage getOrLoadByteStorage(String name) {
        RedisByteStorage redisByteStorage = REDIS_BYTE_STORAGE_MAP.get(name);

        if (redisByteStorage == null) {
            try {
                redisByteStorage = new RedisByteStorage(pool, name);
                REDIS_BYTE_STORAGE_MAP.put(name, redisByteStorage);
            } catch (ApiException e) {
                LOGGER.error("RedisByteStorageManager.getOrLoadByteStorage:Load Failed", e);
            }
        }

        return redisByteStorage;
    }

    // 在内存和Redis中都创建一个新的
    @Override
    public ByteStorage createByteStorage(String name, int nbytes, long expireAt) {
        RedisByteStorage redisByteStorage = new RedisByteStorage(pool, name, nbytes, expireAt);
        REDIS_BYTE_STORAGE_MAP.put(name, redisByteStorage);
        return redisByteStorage;
    }
}
