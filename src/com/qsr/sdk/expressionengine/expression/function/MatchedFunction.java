package com.qsr.sdk.expressionengine.expression.function;

import com.qsr.sdk.expressionengine.Expression;
import com.qsr.sdk.expressionengine.expression.AbstractFunction;

public abstract class MatchedFunction extends AbstractFunction<Object, Boolean> {

	public MatchedFunction(String name, int minElements, int maxElements,
			Expression[] expressions) {
		super(name, minElements, maxElements, expressions);
	}

	@Override
	protected Object cast(Object obj) {

		return obj;
	}

}
