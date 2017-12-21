package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.ObjectData;
import com.qsr.sdk.component.datastorage.StoreStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

class ObjectDataImpl extends AbstractData implements ObjectData {

	public ObjectDataImpl(String name, JedisPool pool, StoreStrategy strategy) {
		super(name, pool, strategy);
	}

	@Override
	public <T> void put(Object key, T value) {
		Jedis jedis = getJedis();
		try {
			String skey = _k(key);
			jedis.set(skey, _v(value, value.getClass()));
			if (strategy != null) {
				jedis.expire(
						skey,
						(int) strategy.getTimeUnit().toSeconds(
								strategy.getMaxTimeRemain()));
			}
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public void remove(Object key) {
		Jedis jedis = getJedis();
		try {
			jedis.del(_k(key));
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public <T> T get(Object key, Class<T> clazz) {
		Jedis jedis = getJedis();
		try {
			return _o(jedis.get(_k(key)), clazz);
		} finally {
			closeRedis(jedis);
		}
	}

}
