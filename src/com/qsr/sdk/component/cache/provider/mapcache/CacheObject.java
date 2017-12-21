package com.qsr.sdk.component.cache.provider.mapcache;

class CacheObject {
	public CacheObject(Object key, Object value, long timeout) {
		expiredtime = System.currentTimeMillis() + timeout;
		this.value = value;
		this.key = key;

	}

	public boolean isTimeout() {
		return System.currentTimeMillis() > expiredtime;
	}

	Object key;
	long timeout;
	long expiredtime;
	Object value;
}
