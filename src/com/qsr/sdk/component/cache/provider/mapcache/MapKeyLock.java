package com.qsr.sdk.component.cache.provider.mapcache;

import com.qsr.sdk.component.cache.KeyLock;

import java.util.concurrent.locks.Lock;

public class MapKeyLock implements KeyLock {

	private Lock lock;

	public MapKeyLock(Lock lock) {
		this.lock = lock;
	}

	@Override
	public void lock() {
		this.lock.lock();
	}

	@Override
	public void unlock() {
		this.lock.unlock();
	}

}
