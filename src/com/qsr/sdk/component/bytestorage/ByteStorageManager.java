package com.qsr.sdk.component.bytestorage;

import com.qsr.sdk.component.Component;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ByteStorageManager extends Component {

    ByteStorage getOrLoadByteStorage(String name);

    ByteStorage createByteStorage(String name, int nbytes, long expireAt);
}
