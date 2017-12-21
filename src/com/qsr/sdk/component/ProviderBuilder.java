package com.qsr.sdk.component;

import java.util.Map;

public class ProviderBuilder {

	Provider provider;

	public static ProviderBuilder getProviderBuilder(Provider provider) {
		return new ProviderBuilder(provider);
	}

	public ProviderBuilder(Provider provider) {
		this.provider = provider;
	}

	public ProviderBuilder registerComponent(int configId, Map<?, ?> config) {
		provider.registerComponent(configId, config);
		return this;
	}

	public ProviderBuilder registerComponent(int configId) {
		return registerComponent(configId, null);
	}

	public ProviderBuilder registerComponent() {
		return registerComponent(
				ComponentProviderManager.DEFAULT_SERVICE_CONFIG_ID, null);
	}

	public ProviderBuilder registerProvider() {
		ComponentProviderManager.registerProvider(provider);
		return this;
	}

	public Provider getProvider() {
		return provider;
	}
}
