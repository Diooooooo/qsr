package com.qsr.sdk.component.cache;


public interface Cache {

	//public static int LOCK_TYPE_READ = 1;

	//public static int LOCK_TYPE_WRITE = 2;

	//	public abstract void lockRead(Object key);
	//
	//	public abstract void unlockRead(Object key);
	//
	//	public abstract void lockWrite(Object key);
	//
	//	public abstract void unlockWrite(Object key);

	KeyLock getReadLock(Object key);

	KeyLock getWriteLock(Object key);

	//public KeyLock getLock(Object key, int lockType);

	Object get(Object key) throws Exception;

	void put(Object key, Object value) throws Exception;

	Object remove(Object key) throws Exception;

	//	public abstract Long incr(String key, long value);
	//
	//	public abstract Long decr(String key, long value);

	void clear() throws Exception;

}
