package com.qsr.sdk.component.datastorage;

import java.util.List;

public interface ListData {

	public <T> void add(Object key, T data);

	public <T> void addList(Object key, List<T> list, Class<T> classOfT);

	public <T> List<T> getList(Object key, Class<T> classOfT);

	public <T> List<T> getListByRange(Object key, Class<T> classOfT,
			long start, long end);

	public Long getLength(Object key);
}
