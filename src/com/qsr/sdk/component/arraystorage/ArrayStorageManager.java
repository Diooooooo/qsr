package com.qsr.sdk.component.arraystorage;

import com.qsr.sdk.component.Component;

/**
 * Created by Computer01 on 2016/6/20.
 */
public interface ArrayStorageManager extends Component {

    <T> ArrayStorage<T> getOrLoadArrayStorage(String name, Class<T> entityClass);

    // <T> ArrayStorage<T> loadArrayStorage(String name, Class<T> entityClass) throws ApiException;

    <T> ArrayStorage<T> createArrayStorage(String name, Class<T> entityClass, int capacity, long expireAt);
}
