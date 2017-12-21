package com.qsr.sdk.component.cache.provider.ehcache;

import com.qsr.sdk.component.cache.KeyLock;

public class ReadLock implements KeyLock {
	private net.sf.ehcache.Cache cache;
	private Object key;

	ReadLock(net.sf.ehcache.Cache cache, Object key) {
		this.cache = cache;
		this.key = key;
	}

	@Override
	public void lock() {
		cache.acquireReadLockOnKey(key);
	}

	@Override
	public void unlock() {
		cache.releaseReadLockOnKey(key);
	}

}
