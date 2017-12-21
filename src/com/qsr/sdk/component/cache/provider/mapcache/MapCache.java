package com.qsr.sdk.component.cache.provider.mapcache;

import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.KeyLock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MapCache implements Cache {

	private LruHashMap<Object, CacheObject> cache;
	//private ReadWriteLock locks[];
	ReadWriteLock lock = new ReentrantReadWriteLock();
	private long timeout;
	private CacheLoader cacheLoader = null;

	public MapCache(int capacity, long timeout, CacheLoader cacheLoader) {
		this.timeout = timeout;
		cache = new LruHashMap<Object, CacheObject>(capacity);
		//		int lockCount = (capacity / 100) + 1;
		//		if (lockCount <= 0) {
		//			lockCount = 10;
		//		}
		//		locks = new ReadWriteLock[lockCount];
		//		for (int i = 0; i < locks.length; i++) {
		//			locks[i] = new ReentrantReadWriteLock();
		//		}
		this.cacheLoader = cacheLoader;

	}

	protected ReadWriteLock getLock(Object key) {
		//		int hashCode = key.hashCode() & 0xffff;
		//		int index = hashCode % locks.length;
		//		if (index >= locks.length || locks.length == 0) {
		//			MapCacheManager.logger.error(
		//					"cache,index:{} greater than length:{}", index,
		//					locks.length);
		//		}
		//		return locks[index];
		return lock;
	}

	@Override
	public KeyLock getReadLock(Object key) {

		return new MapKeyLock(getLock(key).readLock());
	}

	@Override
	public KeyLock getWriteLock(Object key) {
		return new MapKeyLock(getLock(key).writeLock());
	}

	public void lockRead(Object key) {
		getLock(key).readLock().lock();
	}

	public void unlockRead(Object key) {
		getLock(key).readLock().unlock();

	}

	public void lockWrite(Object key) {
		getLock(key).writeLock().lock();

	}

	public void unlockWrite(Object key) {
		getLock(key).writeLock().unlock();
	}

	@Override
	public Object get(Object key) throws Exception {

		Object value = null;

		lockRead(key);
		try {
			CacheObject cacheObject = cache.get(key);
			if ((cacheObject != null) && (cacheObject.isTimeout() == false)) {
				value = cacheObject.value;
			}

		} finally {
			unlockRead(key);
		}
		if (value == null && cacheLoader != null) {
			lockWrite(key);
			try {

				CacheObject cacheObject = cache.get(key);
				if ((cacheObject != null) && (cacheObject.isTimeout() == false)) {
					value = cacheObject.value;
				}
				if (value == null) {
					value = cacheLoader.load(key);
					if (value != null) {
						cache.put(key, new CacheObject(key, value, timeout));
					}
				}

			} finally {
				unlockWrite(key);
			}
		}
		return value;
	}

	@Override
	public void put(Object key, Object value) {
		lockWrite(key);
		try {
			cache.put(key, new CacheObject(key, value, timeout));
		} finally {
			unlockWrite(key);
		}
	}

	@Override
	public Object remove(Object key) {
		lockWrite(key);
		try {
			CacheObject cacheObject = cache.remove(key);
			if (cacheObject == null) {
				return null;
			}
			return cacheObject.value;
		} finally {
			unlockWrite(key);
		}

	}

	@Override
	public void clear() {
		// unsafe
		cache.clear();
	}

	void setCacheLoader(CacheLoader cacheLoader) {
		this.cacheLoader = cacheLoader;
	}

}
