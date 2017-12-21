package com.qsr.sdk.component.datastorage;

public interface ObjectData {

	public <T> T get(Object key, Class<T> classOfT);

	public <T> void put(Object key, T value);

	public void remove(Object key);

}
