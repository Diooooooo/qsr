package com.qsr.sdk.component.bytestorage;

import com.qsr.sdk.component.Provider;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ByteStorageProvider extends Provider {

    ByteStorageManager getComponent(int configId);

    Class<ByteStorageManager> getComponentType();
}
