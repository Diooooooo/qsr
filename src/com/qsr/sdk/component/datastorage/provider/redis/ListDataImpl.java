package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.ListData;
import com.qsr.sdk.component.datastorage.StoreStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

class ListDataImpl extends AbstractData implements ListData {

	public ListDataImpl(String name, JedisPool pool, StoreStrategy strategy) {
		super(name, pool, strategy);
	}

	@Override
	public void add(Object key, Object data) {
		Jedis jedis = getJedis();
		try {
			jedis.rpush(_k(key), _v(data));
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public <T> void addList(Object key, List<T> list, Class<T> classOfT) {
		Jedis jedis = getJedis();
		try {
			jedis.rpush(_k(key), _va(list, classOfT));
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public <T> List<T> getList(Object key, Class<T> classOfT) {
		return getListByRange(key, classOfT, 0, -1);
	}

	@Override
	public Long getLength(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.llen(_k(key));
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public <T> List<T> getListByRange(Object key, Class<T> classOfT,
			long start, long end) {
		Jedis jedis = getJedis();
		try {
			return _ol(jedis.lrange(_k(key), start, end), classOfT);
		} finally {
			closeRedis(jedis);
		}
	}

}
