package com.qsr.sdk.component.cache.provider.ehcache;

import com.qsr.sdk.component.cache.KeyLock;

public class WriteLock implements KeyLock {

	private net.sf.ehcache.Cache cache;
	private Object key;

	WriteLock(net.sf.ehcache.Cache cache, Object key) {
		this.cache = cache;
		this.key = key;
	}

	@Override
	public void lock() {
		cache.acquireWriteLockOnKey(key);
	}

	@Override
	public void unlock() {
		cache.releaseWriteLockOnKey(key);
	}
}
