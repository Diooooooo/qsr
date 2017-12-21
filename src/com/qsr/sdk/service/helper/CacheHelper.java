package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.Md5Util;

import java.util.concurrent.TimeUnit;

public class CacheHelper {

	public static final String default_cache = "default";
	private static final String sp = "/";

	//	private static Map<String, FetchValue> fetchValues = new ConcurrentHashMap<String, FetchValue>();
	//
	//	public static abstract class FetchValue {
	//
	//		public FetchValue() {
	//
	//			// this.seconds = seconds;
	//			// this.params = params;
	//
	//		}
	//
	//		public abstract String getKey();
	//
	//		public abstract Object fetchValue();
	//
	//		public abstract long getTimeout();
	//
	//		public abstract void init();
	//
	//	}

	//	public static Object put(String cacheName, FetchValue fetchValue) {
	//		fetchValue.init();
	//		fetchValues.put(fetchValue.getKey(), fetchValue);
	//		return fetchAndPutValue(cacheName, fetchValue.getKey());
	//
	//	}

	private static CacheManager cacheManager;

	static {
		cacheManager = ComponentProviderManager.getService(CacheManager.class,
				Env.getCacheProviderId());
	}

	public static CacheManager getCacheProvider() {
		return cacheManager;
	}

	public static Cache addCache(String cacheName, int capacity, long timeout,
								 TimeUnit timeUnit, CacheLoader cacheLoader) {
		return cacheManager.addCache(cacheName, capacity, timeout, timeUnit,
				cacheLoader);
	}

	//	public static void clear(String cacheName) {
	//		Cache cache = getCache(cacheName);
	//		if (cache != null) {
	//			cache.clear();
	//		}
	//	}

	public static Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	//	public static void put(String cacheName, String key, Object value) {
	//		Cache cache = getCache(cacheName);
	//		if (cache != null) {
	//			cache.put(key, value);
	//		}
	//
	//	}

	//	@SuppressWarnings("unchecked")
	//	public static <T> T get(String cacheName, Object key) {
	//		Cache cache = getCache(cacheName);
	//		if (cache != null) {
	//			return (T) cache.get(key);
	//		}
	//		return null;
	//	}

	//	private static Object fetchAndPutValue(String cacheName, String key) {
	//		Cache cache = getCache(cacheName);
	//		Object value = null;
	//		if (cache != null) {
	//			cache.lockWrite(key);
	//			try {
	//				FetchValue fetchValue = fetchValues.get(key);
	//				if (fetchValue != null) {
	//					value = fetchValue.fetchValue();
	//					if (value != null) {
	//						put(cacheName, key, value);
	//					}
	//				}
	//			} finally {
	//				cache.unlockWrite(key);
	//			}
	//		}
	//		return value;
	//	}

	//	public static Object getx(String cacheName, String key) {
	//		Cache cache = get(cacheName, key);
	//		Object value = null;
	//		if (cache != null) {
	//			value = cache.get(key);
	//			if (value == null) {
	//				cache.lockWrite(key);
	//				try {
	//					value = fetchAndPutValue(cacheName, key);
	//				} finally {
	//					cache.unlockWrite(key);
	//				}
	//			}
	//
	//		}
	//		return value;
	//	}

	//	@SuppressWarnings("unchecked")
	//	public <T> T getByParams(String cacheName, String method, Object... params) {
	//		String key = generateKey(method, params);
	//		return (T) get(cacheName, key);
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	public <T> T getxByParams(String cacheName, String method, Object... params) {
	//		String key = generateKey(method, params);
	//		return (T) getx(cacheName, key);
	//	}
	//
	//	public static void remove(String cacheName, Object key) {
	//		Cache cache = getCache(cacheName);
	//		if (cache != null) {
	//			cache.remove(key);
	//		}
	//	}

	//	public static void putByParams(String cacheName, Object value,
	//			String method, Object... params) {
	//		String key = generateKey(method, params);
	//		put(cacheName, key, value);
	//	}

	public static String generateKey(String method, Object... params) {

		StringBuffer sb = new StringBuffer();
		sb.append(method).append(sp);
		for (Object p : params) {
			sb.append(p).append(sp);
		}
		return Md5Util.digest(sb.toString());
	}

	//	public static Long incr(String cacheName, String key, long incrValue) {
	//		Cache cache = getCache(cacheName);
	//		Long value = 0l;
	//		if (cache != null && cache.get(key) != null) {
	//			cache.lockWrite(key);
	//			try {
	//				Long l = (Long) cache.get(key);
	//				value = value + incrValue;
	//				cache.put(key, l);
	//			} finally {
	//				cache.unlockWrite(key);
	//			}
	//
	//		}
	//		return value;
	//	}

	//	public static Long incrx(String cacheName, String key, long incrValue) {
	//		Cache cache = getCache(cacheName);
	//		Long value = 0l;
	//		if (cache != null) {
	//			value = (Long) cache.get(key);
	//			if (value == null) {
	//				fetchAndPutValue(cacheName, key);
	//			} else {
	//				incr(cacheName, key, incrValue);
	//			}
	//
	//		}
	//		return value;
	//
	//	}

	//	public static Long incrByParams(String cacheName, long value,
	//			String method, Object... params) {
	//		String key = generateKey(method, params);
	//		return incr(cacheName, key, value);
	//	}
	//
	//	public static Long incrxByParams(String cacheName, long value,
	//			String method, Object... params) {
	//		String key = generateKey(method, params);
	//		return incrx(cacheName, key, value);
	//	}

}
