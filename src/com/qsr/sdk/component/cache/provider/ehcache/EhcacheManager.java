package com.qsr.sdk.component.cache.provider.ehcache;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.util.WorkingResourceUtil;
import net.sf.ehcache.config.CacheConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EhcacheManager extends AbstractComponent implements CacheManager {

	private net.sf.ehcache.CacheManager cacheManager;

	private Map<String, Ehcache> caches = new ConcurrentHashMap<String, Ehcache>();

	EhcacheManager(Provider provider) {
		super(provider);
		cacheManager = net.sf.ehcache.CacheManager.create(WorkingResourceUtil
				.getInputStream("ehcache.xml"));
	}

	@Override
	public Cache getCache(String cacheName) {
		Ehcache result = caches.get(cacheName);
		if (result == null) {
			net.sf.ehcache.Cache cache = cacheManager.getCache(cacheName);
			if (cache != null) {
				result = new Ehcache(cache, null);
			}
		}
		return result;
	}

	protected synchronized net.sf.ehcache.Cache getOrAddUnderlyingCache(
			String cacheName, int capacity, long timeout, TimeUnit timeUnit) {
		net.sf.ehcache.Cache cache = cacheManager.getCache(cacheName);
		CacheConfiguration config;
		if (cache == null) {
			config = new CacheConfiguration(cacheName, capacity);
			cache = new net.sf.ehcache.Cache(config);
			cacheManager.addCache(cache);
		} else {
			config = cache.getCacheConfiguration();
		}
		config.setEternal(timeout <= 0);
		config.setTimeToLiveSeconds(timeUnit.toSeconds(timeout));
		config.setMaxEntriesLocalHeap(capacity);

		return cache;
	}

	@Override
	public Cache addCache(String cacheName, int capacity, long timeout,
			TimeUnit timeUnit, CacheLoader cacheLoader) {
		//net.sf.ehcache.Cache cache = 

		Ehcache result = caches.get(cacheName);
		//

		if (result == null) {
			synchronized (cacheManager) {
				result = caches.get(cacheName);
				//cache = cacheManager.getCache(cacheName);
				if (result == null) {

					net.sf.ehcache.Cache underlyingCache = getOrAddUnderlyingCache(
							cacheName, capacity, timeout, timeUnit);
					result = new Ehcache(underlyingCache, cacheLoader);
					caches.put(cacheName, result);
				}

			}
		}

		//		if (cacheLoader != null) {
		//			cache.registerCacheLoader(new EhcacheLoaderImpl(cacheName
		//					+ ":cacheLoader", cacheLoader));
		//			config.setCopyOnRead(true);
		//		}
		return result;
	}
}
