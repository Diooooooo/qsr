package com.qsr.sdk.component.bytestorage;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ByteStorage {

    void set(long offset); // 指定偏移位设置为true

    void set(long offset, boolean value); // 指定偏移位设置为指定值

    boolean get(long offset); // 获取指定偏移位上的值

    long size(); // 获取占用空间的位数（二进制数组的长度）

    void setExpireAt(long expireAt); // 设置过期时间
}
