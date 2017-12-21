package com.qsr.sdk.component.arraystorage;

import com.qsr.sdk.component.Provider;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ArrayStorageProvider extends Provider {

    ArrayStorageManager getComponent(int configId);

    Class<ArrayStorageManager> getComponentType();
}
