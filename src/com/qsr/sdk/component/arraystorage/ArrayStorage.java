package com.qsr.sdk.component.arraystorage;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ArrayStorage<T> {

    T get(int index);

    T set(int index, T entity);

    int length();

    void setExpireAt(long expireAt);
}