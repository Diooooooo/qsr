package com.qsr.sdk.component.cache.provider.ehcache;

import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheLoader;
import com.qsr.sdk.component.cache.KeyLock;
import net.sf.ehcache.Element;

public class Ehcache implements Cache {

	private net.sf.ehcache.Cache cache;
	private CacheLoader cacheLoader = null;

	Ehcache(net.sf.ehcache.Cache cache, CacheLoader cacheLoader) {
		this.cache = cache;
		this.cacheLoader = cacheLoader;
	}

	@Override
	public KeyLock getReadLock(Object key) {
		return new ReadLock(cache, key);
	}

	@Override
	public KeyLock getWriteLock(Object key) {
		return new WriteLock(cache, key);
	}

	@Override
	public Object get(Object key) throws Exception {
		//Object value = null;
		Element element = cache.get(key);

		if (element == null && cacheLoader != null) {

			KeyLock lock = getWriteLock(key);
			try {
				lock.lock();
				element = cache.get(key);

				if (element == null) {
					Object value = cacheLoader.load(key);
					if (value != null) {
						element = new Element(key, value);
						cache.put(element);
					}
				}

			} finally {
				lock.unlock();
			}
		}
		return element != null ? element.getObjectValue() : null;
		//return cache.get(key);
	}

	@Override
	public void put(Object key, Object value) throws Exception {
		cache.put(new Element(key, value));
	}

	@Override
	public Object remove(Object key) throws Exception {
		return cache.remove(key);
	}


	@Override
	public void clear() throws Exception {
		cache.removeAll();
	}

}
