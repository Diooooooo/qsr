package com.qsr.sdk.component.keyvalue;

import com.qsr.sdk.component.Component;

/**
 * Created by yuan on 2016/3/14.
 */
public interface KeyValue extends Component {
    Object get(Object key);
    Object put(Object key,Object value);
    Object incr(Object key,Object delta);
    Object decr(Object key,Object delta);
}
