package com.qsr.sdk.component.cache.provider.ehcache;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.component.cache.CacheProvider;

import java.util.Map;

public class EhcacheProvider extends AbstractProvider<CacheManager> implements
        CacheProvider {

	public static final int PROVIDER_ID = 3;

	public EhcacheProvider() {
		super(PROVIDER_ID);

	}

	@Override
	public CacheManager createComponent(int configId, Map<?, ?> config) {

		return new EhcacheManager(this);
	}

}
