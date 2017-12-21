package com.qsr.sdk.component.datastorage;

import java.util.concurrent.TimeUnit;

public class StoreStrategy {

	private final long maxItemRemain;
	private final long maxTimeRemain;
	private final TimeUnit timeUnit;

	public StoreStrategy(long maxItemRemain, long maxTimeRemain,
			TimeUnit timeUnit) {
		super();
		this.maxItemRemain = maxItemRemain;
		this.maxTimeRemain = maxTimeRemain;
		this.timeUnit = timeUnit;
	}

	public long getMaxItemRemain() {
		return maxItemRemain;
	}

	public long getMaxTimeRemain() {
		return maxTimeRemain;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

}
