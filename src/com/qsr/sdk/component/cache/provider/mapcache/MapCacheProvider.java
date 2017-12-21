package com.qsr.sdk.component.cache.provider.mapcache;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.component.cache.CacheProvider;

import java.util.Map;

public class MapCacheProvider extends AbstractProvider<CacheManager>
		implements CacheProvider {

	public static final int PROVIDER_ID = 2;

	public MapCacheProvider() {
		super(PROVIDER_ID);

	}

	@Override
	public CacheManager createComponent(int configId, Map<?, ?> config) {
		return new MapCacheManager(this);
	}

}
