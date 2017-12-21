package com.qsr.sdk.component;

import com.qsr.sdk.util.DateUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * Created by Computer01 on 2016/6/21.
 */
public abstract class AbstractRedisSupport {

    protected final JedisPool pool; // Redis连接池

    protected final String name; // 绑定的name。约定：name不带时间戳后缀。

    protected final String key; // 存放在Redis里面的键名称

    public AbstractRedisSupport(JedisPool pool, String name) {
        this.pool = pool;
        this.name = name;
        this.key = getKeyFromName(name);
    }

    protected Jedis getJedis() {
        return pool.getResource();
    }

    protected abstract String getKeyFromName(String name);

    public long getExpireAt() {
        try (Jedis jedis = this.getJedis()) {
            Long remainingMillis = jedis.pttl(key);

            // -1：key是永久保存的；-2：key不存在，这两种情况下，返回默认的过期时间
            if (remainingMillis == -1 || remainingMillis == -2)
                return DateUtil.getDayTime() + TimeUnit.DAYS.toMillis(1);

            return System.currentTimeMillis() + jedis.pttl(key);
        }
    }
}
