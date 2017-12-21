package com.qsr.sdk.component.bytestorage.provider.redis;

import com.qsr.sdk.component.AbstractRedisSupport;
import com.qsr.sdk.component.bytestorage.ByteStorage;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Computer01 on 2016/6/20.
 */
public class RedisByteStorage extends AbstractRedisSupport implements ByteStorage {

    private final long capacity;

    public RedisByteStorage(JedisPool pool, String name, int nbytes, long expireAt) {
        super(pool, name);
        this.capacity = 8 * nbytes;
        this.initKey();
        if (expireAt != -2) {
            // 过期时间传入-1时，使用系统默认
            // 过期时间传入-2时，视为永久保存，不设置过期时间
            this.setExpireAt(expireAt == -1 ? super.getExpireAt() : expireAt);
        }
    }

    public RedisByteStorage(JedisPool pool, String name) throws ApiException {
        super(pool, name);
        try (Jedis jedis = pool.getResource()) {
            this.capacity = jedis.strlen(key).intValue() * 8;
            if (this.capacity == 0) {
                throw new ApiException(ErrorCode.LOAD_FAILED_FROM_DATABASE, "二进制数据不存在，请选择重新创建");
            }
        }
    }

    private void initKey() {
        try (Jedis jedis = super.getJedis()) {
            if (jedis.get(key) == null) {
                jedis.setbit(key, capacity - 1, false); // 初始化，在Redis中建立
            }
        }
    }

    @Override
    public void set(long offset) {
        if (offset >= 0 && offset <= capacity - 1) {
            try (Jedis jedis = super.getJedis()) {
                jedis.setbit(key, offset, true); // setbit操作不重置键的过期时间
            }
        }
    }

    @Override
    public void set(long offset, boolean value) {
        if (offset >= 0 && offset <= capacity - 1) {
            try (Jedis jedis = super.getJedis()) {
                jedis.setbit(key, offset, value); // setbit操作不重置键的过期时间
            }
        }
    }

    @Override
    public boolean get(long offset) {
        if (offset >= 0 && offset <= capacity - 1) {
            try (Jedis jedis = super.getJedis()) {
                return jedis.getbit(key, offset);
            }
        }
        return false;
    }

    @Override
    public long size() {
        return capacity;
    }

    @Override
    public void setExpireAt(long expireAt) {
        try (Jedis jedis = super.getJedis()) {
            jedis.pexpireAt(key, expireAt);
        }
    }

    @Override
    protected String getKeyFromName(String name) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return name + "_" + timestamp;
    }
}
