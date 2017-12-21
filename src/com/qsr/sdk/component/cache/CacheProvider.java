package com.qsr.sdk.component.cache;

import com.qsr.sdk.component.Provider;

public interface CacheProvider extends Provider {

	public CacheManager getComponent(int configId);

	public Class<CacheManager> getComponentType();
}