package com.qsr.sdk.component.expression;

import com.qsr.sdk.component.Provider;

public interface ExpressionProvider extends Provider {

	//	@Override
	//	public Class<? extends ServiceProvider> getSpiClass() {
	//
	//		return FilterProvider.class;
	//	}

	ExpressionManager getComponent(int configId);

}
