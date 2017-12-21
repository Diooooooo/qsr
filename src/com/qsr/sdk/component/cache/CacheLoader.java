package com.qsr.sdk.component.cache;

public interface CacheLoader {

	Object load(Object key) throws Exception;

}
