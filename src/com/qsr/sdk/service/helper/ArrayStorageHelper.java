package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.arraystorage.ArrayStorage;
import com.qsr.sdk.component.arraystorage.ArrayStorageManager;
import com.qsr.sdk.component.arraystorage.provider.redis.RedisArrayStorageProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.ruleexecutor.Logger;
import com.qsr.sdk.service.ruleexecutor.LoggerFactory;
import com.qsr.sdk.util.ErrorCode;

/**
 * Created by Computer01 on 2016/6/21.
 */
public class ArrayStorageHelper {

    private static final int DEFAULT_CONFIG_ID = 6;

    private static final Logger logger = LoggerFactory.getLogger(ArrayStorageHelper.class);

    private static ArrayStorageManager arrayStorageManager
            = ComponentProviderManager.getService(ArrayStorageManager.class, RedisArrayStorageProvider.PROVIDER_ID, DEFAULT_CONFIG_ID);

    private static void checkManagerStatus() throws ApiException {
        if (arrayStorageManager == null)
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "不存在的数组存储服务");
    }

    public static <T> ArrayStorage<T> createArrayStorage(String name, Class<T> entityClass, int capacity, long expireAt) throws ApiException {
        checkManagerStatus();
        return arrayStorageManager.createArrayStorage(name, entityClass, capacity, expireAt);
    }

    public static <T> ArrayStorage<T> getOrLoadArrayStorage(String name, Class<T> entityClass) throws ApiException {
        checkManagerStatus();
        return arrayStorageManager.getOrLoadArrayStorage(name, entityClass);
    }

    public static <T> T get(String name, Class<T> entityClass, int index) throws ApiException {
        return getOrLoadArrayStorage(name, entityClass).get(index);
    }

    public static <T> T set(String name, Class<T> entityClass, int index, T entity) throws ApiException {
        return getOrLoadArrayStorage(name, entityClass).set(index, entity);
    }

    public static <T> int length(String name, Class<T> entityClass) throws ApiException {
        return getOrLoadArrayStorage(name, entityClass).length();
    }

    public static <T> void setExpireAt(String name, Class<T> entityClass, long expireAt) throws ApiException {
        getOrLoadArrayStorage(name, entityClass).setExpireAt(expireAt);
    }
}
