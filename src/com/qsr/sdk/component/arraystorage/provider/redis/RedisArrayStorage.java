package com.qsr.sdk.component.arraystorage.provider.redis;

import com.google.gson.Gson;
import com.qsr.sdk.component.AbstractRedisSupport;
import com.qsr.sdk.component.arraystorage.ArrayStorage;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Computer01 on 2016/6/20.
 */
public class RedisArrayStorage<T> extends AbstractRedisSupport implements ArrayStorage<T> {

    private final int capacity;

    private final Class<T> entityClass; // 获取ArrayStorage<T>上的泛型

    private static final Gson GSON = new Gson();


    public RedisArrayStorage(JedisPool pool, String name, Class<T> entityClass, int capacity, long expireAt) {
        super(pool, name);
        this.capacity = capacity;
        this.entityClass = entityClass;
        this.initKey();
        if (expireAt != -2) {
            // 过期时间传入-1时，使用系统默认
            // 过期时间传入-2时，视为永久保存，不设置过期时间
            this.setExpireAt(expireAt == -1 ? super.getExpireAt() : expireAt);
        }
    }

    public RedisArrayStorage(JedisPool pool, String name, Class<T> entityClass) throws ApiException {
        super(pool, name);
        this.entityClass = entityClass;
        try (Jedis jedis = pool.getResource()) {
            this.capacity = jedis.hlen(key).intValue();
            if (this.capacity == 0) {
                throw new ApiException(ErrorCode.LOAD_FAILED_FROM_DATABASE, "数组不存在，请选择重新创建");
            }
        }
    }

    private void initKey() {
        try (Jedis jedis = this.getJedis()) {
            if (jedis.hlen(key) == 0) {
                for (int i = 0; i < capacity; i++) {
                    jedis.hset(key, i + "", "");
                }
            }
        }
    }

    @Override
    protected String getKeyFromName(String name) {
        /*String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return name + "_" + timestamp;*/
        return name; // TODO 目前RedisArrayStorage只有GeoIpService在用，它需要的是永久保存，因此这里先写死，保存的key不加时间戳后缀。
    }

    @Override
    public T get(int index) {
        try (Jedis jedis = this.getJedis()) {
            String json = jedis.hget(key, index + "");
            if (StringUtils.isNotBlank(json)) {
                return GSON.fromJson(json, entityClass);
            }
            return null;
        }
    }

    @Override
    public T set(int index, T entity) {
        T oldEntity = this.get(index);
        try (Jedis jedis = this.getJedis()) {
            jedis.hset(key, index + "", GSON.toJson(entity));
        }
        return oldEntity;
    }

    @Override
    public int length() {
        return capacity;
    }

    @Override
    public void setExpireAt(long expireAt) {
        try (Jedis jedis = super.getJedis()) {
            jedis.pexpireAt(key, expireAt);
        }
    }
}
