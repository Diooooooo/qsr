package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.StoreStrategy;

public class Metadata {

	private final String name;
	private final StoreStrategy storeStrategy;

	public Metadata(String name, StoreStrategy storeStrategy) {
		super();
		this.name = name;
		this.storeStrategy = storeStrategy;
	}

	public String getName() {
		return name;
	}

	public StoreStrategy getStoreStrategy() {
		return storeStrategy;
	}

}
