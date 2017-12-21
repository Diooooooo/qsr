package com.qsr.sdk.component.arraystorage.provider.redis;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.arraystorage.ArrayStorageManager;
import com.qsr.sdk.component.arraystorage.ArrayStorageProvider;

import java.util.Map;

/**
 * Created by Computer01 on 2016/6/20.
 */
public class RedisArrayStorageProvider extends AbstractProvider<ArrayStorageManager> implements ArrayStorageProvider {

    public static final int PROVIDER_ID = 13;

    public RedisArrayStorageProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public ArrayStorageManager createComponent(int configId, Map<?, ?> config) {
        return new RedisArrayStorageManager(this, config);
    }
}
