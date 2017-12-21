package com.qsr.sdk.component.stats.provider.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qsr.sdk.component.AbstractRedisSupport;
import com.qsr.sdk.component.stats.Stats;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/14.
 * 基于Redis的数据统计组件。
 */
public class RedisStats extends AbstractRedisSupport implements Stats {

    private static final Gson GSON = new Gson();

    private static final long DEFAULT_DATA_SIZE_LIMIT = 1000; // 默认的集合长度上限

    public RedisStats(JedisPool pool, String name, int sizeLimit, long expireAt) {
        super(pool, name);
        if (expireAt != -1) {
            this.setExpireAt(expireAt);
        }
        if (sizeLimit != -1) {
            this.setSizeLimit(sizeLimit);
        }
    }

    /**
     * 添加一条指定名称的监控数据
     *
     * @param data
     */
    @Override
    public void addStatsData(Map<String, Object> data) {

        try (Jedis jedis = super.getJedis()) {
            String key = this.getKeyFromName(name); // 加上时间戳后缀
            //data.put("id", id.getAndIncrement());
            Long length = jedis.llen(key); // 对于尚未创建的集合，length == 0

            // long currentMaxIndex = this.getCurrentMaxIndex();
            data.put("id", this.getAndIncreaseCurrentMaxIndex());

            if (length < this.getSizeLimit()) {
                jedis.rpush(key, this.serialize(data)); // 如果集合的长度未达到规定的上限，则直接添加一条数据
            } else {
                jedis.lpop(key); // 删除集合中第一条数据（最老的数据）
                jedis.rpush(key, this.serialize(data)); // 把当前数据添加到集合最后
            }
            // jedis.hset(name + "_attrs", "_cur_max_index", (currentMaxIndex) + ""); // 重新设置最大索引

            if (length == 0) // 如果是新建的集合，则设置这个集合的过期时间
                jedis.pexpireAt(key, super.getExpireAt());
        }
    }

    /**
     * 获取指定名称的监控数据条数（即最大索引）
     * 如果集合不存在，则返回0
     * @return
     */
    @Override
    public long getStatsDataCount() {
        return this.getCurrentMaxIndex();
    }

    /**
     * 获取指定名称的统计数据的一个子集
     *
     * @param startIndex
     * @param count
     * @return
     */
    @Override
    public List<Map<String, Object>> getStatsDataList(long startIndex, int count) {
        if (count <= 0)
            return null;

        try (Jedis jedis = super.getJedis()) {
            String key = this.getKeyFromName(name); // 加上时间戳后缀

            int length = jedis.llen(key).intValue();
            if (length == 0) // 指定的统计数据集合不存在，只能返回null。
                return null;

            // 获取当前最大索引（应用索引）
            long currentMaxIndex = this.getCurrentMaxIndex();
            // 偏移量offset是一条记录的应用索引与redis索引之差。由于应用索引从1开始，所以偏移量最少是1。
            long offset = currentMaxIndex - length + 1 >= 1 ? currentMaxIndex - length + 1 : 1;
            startIndex = startIndex - offset; // 传入的参数是应用索引，需要将它转换为Redis索引
            long startIndex2 = startIndex < 0 ? 0 : startIndex; // 如果起始索引指向已经弃置的数据区，则强制将它置为0

            if (startIndex2 >= length) // 起始索引过界，只能返回null。
                return null;

            List<String> stringList;
            if (startIndex < 0) {
                // 如果startIndex < 0，则取出最新的count条数据（此时传入的参数startIndex无意义）
                startIndex2 = length - count > 0 ? length - count : 0;
                stringList = jedis.lrange(key, startIndex2, length);
            } else {
                // 将count转换为结束索引，并根据集合当前长度进行校正
                long endIndex = startIndex2 + count;
                endIndex = endIndex > length ? length : endIndex;

                stringList = jedis.lrange(key, startIndex2, endIndex - 1);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (String json : stringList) {
                result.add(GSON.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType()));
            }
            return result;
        }
    }

    @Override
    public void setSizeLimit(long sizeLimit) {
        try (Jedis jedis = super.getJedis()) {
            jedis.hset(name + "_attrs", "_size_limit", sizeLimit + "");
        }
    }

    @Override
    public void setExpireAt(long expireAt) {
        try (Jedis jedis = super.getJedis()) {
            String key = this.getKeyFromName(name); // 加上时间戳后缀
            jedis.pexpireAt(key, expireAt);
        }
    }

    private String serialize(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 获取指定名称统计数据的当前最大索引值。多次调用此方法不会对Redis里面的索引值产生影响。
     *
     * @return
     */
    private long getCurrentMaxIndex() {
        try (Jedis jedis = super.getJedis()) {
            String nextIndexStr = jedis.hget(name + "_attrs", "_cur_max_index");

            if (nextIndexStr == null) {
                nextIndexStr = (jedis.llen(this.getKeyFromName(name))) + "";
                jedis.hset(name + "_attrs", "_cur_max_index", nextIndexStr);
            }

            return Long.parseLong(nextIndexStr);
        }
    }

    /**
     * 获取指定名称统计数据的当前最大索引值，并将这个值加1。返回加1之后的结果。
     * hincrby操作是原子性的。
     * @return
     */
    private long getAndIncreaseCurrentMaxIndex() {
        try (Jedis jedis = super.getJedis()) {
            return jedis.hincrBy(name + "_attrs", "_cur_max_index", 1);
        }
    }

    @Override
    protected String getKeyFromName(String name) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return name + "_" + timestamp;
    }

/*    private String getNameWithoutTimestamp(String name) {
        if (StringUtils.isNotBlank(name) && name.matches("^.+_\\d{8}$")) {
            return name.substring(0, name.length() - 9);
        }
        return name;
    }*/

    public long getSizeLimit() {
        try (Jedis jedis = super.getJedis()) {
            String sizeLimit = jedis.hget(name + "_attrs", "_size_limit");
            if (sizeLimit == null) {
                sizeLimit = DEFAULT_DATA_SIZE_LIMIT + "";
                jedis.hset(name + "_attrs", "_size_limit", sizeLimit);
            }
            return Long.parseLong(sizeLimit);
        }
    }
}
