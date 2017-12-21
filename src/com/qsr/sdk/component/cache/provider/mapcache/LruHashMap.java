package com.qsr.sdk.component.cache.provider.mapcache;

import java.util.LinkedHashMap;
import java.util.Map;

class LruHashMap<K, V> extends LinkedHashMap<K, V> {

	/**   */
	private static final long serialVersionUID = -7396325812035408567L;

	private final int MAX_CACHE_SIZE;

	public LruHashMap(int cacheSize) {
		super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
		MAX_CACHE_SIZE = cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > MAX_CACHE_SIZE;
	}

}
