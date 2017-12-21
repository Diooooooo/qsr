package com.qsr.sdk.component.expression.provider.jexl;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.expression.ExpressionManager;
import com.qsr.sdk.component.expression.ExpressionProvider;

import java.util.Map;

public class JexlFilterProvider extends AbstractProvider<ExpressionManager>
		implements ExpressionProvider {

	public static final int PROVIDER_ID = 2;

	public JexlFilterProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public ExpressionManager createComponent(int configId, Map<?, ?> config) {
		return new JexlFilterManager(this);
	}

}
