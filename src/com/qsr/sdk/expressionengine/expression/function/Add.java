package com.qsr.sdk.expressionengine.expression.function;

import com.qsr.sdk.expressionengine.Expression;

public class Add extends ArithmeticFunction {

	public Add(double defaultValue, Expression[] expressions) {
		super("add", 2, -1, defaultValue, expressions);
	}

	@Override
	Number invoke(Number r, Number param) {

		return r.doubleValue() + param.doubleValue();
	}

}
