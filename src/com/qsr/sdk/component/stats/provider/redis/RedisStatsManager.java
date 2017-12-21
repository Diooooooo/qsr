package com.qsr.sdk.component.stats.provider.redis;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.stats.Stats;
import com.qsr.sdk.component.stats.StatsManager;
import com.qsr.sdk.util.ParameterUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/16.
 */
public class RedisStatsManager extends AbstractComponent implements StatsManager {

    private final JedisPool pool;

    private static final Map<String, RedisStats> REDIS_STATS_MAP = new HashMap<>(); // RedisStats对象的缓存

    public RedisStatsManager(Provider provider, Map<?, ?> config) {
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

    @Override
    public Stats getStats(String name) {
        RedisStats redisStats = REDIS_STATS_MAP.get(name);

        if (redisStats == null) {
            redisStats = new RedisStats(pool, name, -1, -1); // sizeLimit和expireAt传入-1表示采用RedisStats内置的默认值。
            REDIS_STATS_MAP.put(name, redisStats);
        }

        return redisStats;
    }
}
