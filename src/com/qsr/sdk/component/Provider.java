package com.qsr.sdk.component;

import java.util.Map;

public interface Provider {

	public int getProviderId();

	public Component getComponent(int configId);

	public Component createComponent(int configId, Map<?, ?> config);

	public Class<? extends Component> getComponentType();

	public String getName();

	public Component registerComponent(int configId, Map<?, ?> config);
}