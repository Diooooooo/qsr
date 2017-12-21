package com.qsr.sdk.service.helper;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectLocks {

	private static ConcurrentHashMap<Integer, Object> promotionLocks = new ConcurrentHashMap<>();

	private static ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

	public static Object getPromotionLock(int promotionId) {

		Object lock = promotionLocks.get(promotionId);
		if (lock == null) {
			lock = new Object();
			Object l = promotionLocks.putIfAbsent(promotionId, lock);
			if (l != null) {
				lock = l;
			}
		}
		return lock;
	}

	public static Object getFileLock(String filePath) {

		Object lock = fileLocks.get(filePath);
		if (lock == null) {
			lock = new Object();
			Object l = fileLocks.putIfAbsent(filePath, lock);
			if (l != null) {
				lock = l;
			}
		}
		return lock;
	}
}
