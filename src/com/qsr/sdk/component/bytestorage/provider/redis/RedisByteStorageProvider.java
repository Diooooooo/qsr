package com.qsr.sdk.component.bytestorage.provider.redis;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.bytestorage.ByteStorageManager;
import com.qsr.sdk.component.bytestorage.ByteStorageProvider;

import java.util.Map;

/**
 * Created by Computer01 on 2016/6/20.
 */
public class RedisByteStorageProvider extends AbstractProvider<ByteStorageManager> implements ByteStorageProvider {

    public static final int PROVIDER_ID = 14;

    public RedisByteStorageProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public ByteStorageManager createComponent(int configId, Map<?, ?> config) {
        return new RedisByteStorageManager(this, config);
    }
}
