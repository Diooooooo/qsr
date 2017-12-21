package com.qsr.sdk.component.cache.provider.memcached;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.component.cache.CacheProvider;

import java.util.concurrent.TimeUnit;

public class Memcached extends AbstractComponent implements CacheManager {

	public Memcached(CacheProvider provider) {
		super(provider);
	}

	public static final int PROVIDER_ID = 1;

	@Override
	public Cache getCache(String cacheName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache addCache(String cacheName, int capacity, long timeout,
			TimeUnit timeUnit, CacheLoader cacheLoader) {
		// TODO Auto-generated method stub
		return null;

	}

}
