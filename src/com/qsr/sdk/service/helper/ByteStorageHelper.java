package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.bytestorage.ByteStorage;
import com.qsr.sdk.component.bytestorage.ByteStorageManager;
import com.qsr.sdk.component.bytestorage.provider.redis.RedisByteStorageProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.ruleexecutor.Logger;
import com.qsr.sdk.service.ruleexecutor.LoggerFactory;
import com.qsr.sdk.util.ErrorCode;

/**
 * Created by Computer01 on 2016/6/21.
 */
public class ByteStorageHelper {

    private static final int DEFAULT_CONFIG_ID = 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(ByteStorageHelper.class);

    private static ByteStorageManager byteStorageManager
            = ComponentProviderManager.getService(ByteStorageManager.class, RedisByteStorageProvider.PROVIDER_ID, DEFAULT_CONFIG_ID);

    private static void checkManagerStatus() throws ApiException {
        if (byteStorageManager == null)
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "不存在的二进制存储服务");
    }

    public static ByteStorage getOrLoadByteStorage(String name) throws ApiException {
        checkManagerStatus();
        return byteStorageManager.getOrLoadByteStorage(name);
    }

    public static ByteStorage createByteStorage(String name, int nbytes, long expireAt) throws ApiException {
        checkManagerStatus();
        return byteStorageManager.createByteStorage(name, nbytes, expireAt);
    }

    public static void set(String name, long offset) throws ApiException {
        getOrLoadByteStorage(name).set(offset);
    }

    public static void set(String name, long offset, boolean value) throws ApiException {
        getOrLoadByteStorage(name).set(offset, value);
    }

    public static boolean get(String name, long offset) throws ApiException {
        return getOrLoadByteStorage(name).get(offset);
    }

    public static long size(String name) throws ApiException {
        return getOrLoadByteStorage(name).size();
    }

    public static void setExpireAt(String name, long expireAt) throws ApiException {
        getOrLoadByteStorage(name).setExpireAt(expireAt);
    }
}
