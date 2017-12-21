package com.qsr.sdk.component;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractComponent implements Component {

	protected final Provider provider;
	protected final Map<?,?> config;

	public AbstractComponent(Provider provider) {
		super();
		this.provider = provider;
		this.config= Collections.emptyMap();
	}
	public AbstractComponent(Provider provider, Map<?,?> config) {
		super();
		this.provider = provider;
		this.config=config;
	}
	@Override
	public Provider getProvider() {
		return provider;
	}
}
