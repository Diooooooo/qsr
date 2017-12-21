package com.qsr.sdk.component.cache.provider.mapcache;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.component.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MapCacheManager extends AbstractComponent implements CacheManager {

	public static final int PROVIDER_ID = 2;
	final static Logger logger = LoggerFactory.getLogger(MapCacheManager.class);

	public MapCacheManager(CacheProvider provider) {
		super(provider);
	}

	private ConcurrentHashMap<String, MapCache> caches = new ConcurrentHashMap<String, MapCache>();

	public Cache getCache(String cacheName) {
		return caches.get(cacheName);
	}

	public Cache addCache(String cacheName, int capacity, long timeout,
			TimeUnit timeUnit, CacheLoader cacheLoader) {
		MapCache cache = new MapCache(capacity, timeUnit.toMillis(timeout),
				cacheLoader);
		MapCache prevCache = caches.putIfAbsent(cacheName, cache);
		return prevCache == null ? cache : prevCache;

	}

}
