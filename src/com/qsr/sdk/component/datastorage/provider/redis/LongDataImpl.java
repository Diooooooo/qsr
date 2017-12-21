package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.LongData;
import com.qsr.sdk.component.datastorage.StoreStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

class LongDataImpl extends AbstractData implements LongData {

	static final Long zero = 0l;

	public LongDataImpl(String name, JedisPool pool, StoreStrategy strategy) {
		super(name, pool, strategy);
	}

	@Override
	public Long incr(Object key, long value) {

		Jedis jedis = getJedis();
		try {
			return jedis.hincrBy(name, _k(key), value);
			//return jedis.incrBy(_k(key), value);
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public Long decr(Object key, long value) {
		Jedis jedis = getJedis();
		try {
			return jedis.hincrBy(name, _k(key), -value);

		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public Long getValue(Object key) {
		Jedis jedis = getJedis();
		try {
			String s = jedis.hget(name, _k(key));
			if (s == null) {
				return zero;
			}
			return Long.parseLong(s);
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public void setValue(Object key, long value) {
		Jedis jedis = getJedis();
		try {
			jedis.hset(name, _k(key), "" + value);
		} finally {
			closeRedis(jedis);
		}
	}

}
