package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.StoreStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractData {

	protected final String name;
	protected final JedisPool pool;
	protected final StoreStrategy strategy;

	static final String sp = ":";
	static final String null_string = "<null>";

	public AbstractData(String name, JedisPool pool, StoreStrategy strategy) {
		super();
		this.name = name;
		this.pool = pool;
		this.strategy = strategy;
	}

	protected Jedis getJedis() {
		return pool.getResource();
	}

	protected void closeRedis(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	protected String _k(Object key) {
		StringBuffer sb = new StringBuffer();
		if (key == null) {
			sb.append(null_string);
		} else if (key.getClass().isArray()) {
			int len = Array.getLength(key);
			for (int i = 0; i < len; i++) {
				if (sb.length() > 0) {
					sb.append(sp);
				}
				sb.append(Array.get(key, i));
			}
		} else {
			sb.append(key);
		}
		return sb.toString();
	}

	protected String _k(Object key, String id) {
		return _k(key) + sp + id;
	}

	protected <T> String _v(Object value, Class<T> clazz) {
		if (value == null) {
			return null;
		}
		return DataSerialization.serialize(value, clazz);
	}

	protected String _v(Object value) {
		return _v(value, Object.class);
	}

	protected <T> String[] _va(List<T> list, Class<T> clazz) {
		String[] result = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = _v(list.get(i), clazz);
		}
		return result;
	}

	protected String[] _va(List<Object> list) {
		return _va(list, Object.class);
	}

	protected <T> T _o(String value, Class<T> clazz) {
		if (value == null) {
			return null;
		}
		return DataSerialization.deserialize(value, clazz);
	}

	protected Object _o(String value) {
		return _o(value, Object.class);
	}

	protected <T> List<T> _ol(List<String> list, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		for (String s : list) {
			result.add(_o(s, clazz));
		}
		return result;
	}

	protected List<Object> _ol(List<String> list) {
		return _ol(list, Object.class);
	}

}
