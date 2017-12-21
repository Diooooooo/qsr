package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.datastorage.DataStorage;
import com.qsr.sdk.component.datastorage.DataStorageProvider;

import java.util.Map;

public class RedisDataStorageProvider extends AbstractProvider<DataStorage>
		implements DataStorageProvider {
	public static final int PROVIDER_ID = 1;

	public RedisDataStorageProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public DataStorage createComponent(int configId, Map<?, ?> config) {

		return new RedisDataStorage(this, config);
	}

}
