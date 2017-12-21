package com.qsr.sdk.component.cache;

import com.qsr.sdk.component.Component;

import java.util.concurrent.TimeUnit;

public interface CacheManager extends Component {

	public Cache getCache(String cacheName);

	public Cache addCache(String cacheName, int capacity, long timeout,
			TimeUnit timeUnit, CacheLoader cacheLoader);
}
