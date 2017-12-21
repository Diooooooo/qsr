package com.qsr.sdk.component.keyvalue;

import com.qsr.sdk.component.Provider;

/**
 * Created by yuan on 2016/3/14.
 */
public interface KeyValueProvider extends Provider {

    KeyValue getComponent(int configId);

    Class<KeyValue> getComponentType();

}
