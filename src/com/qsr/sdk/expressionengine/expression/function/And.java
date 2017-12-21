package com.qsr.sdk.expressionengine.expression.function;

import com.qsr.sdk.expressionengine.Expression;

public class And extends LogicFunction {

	public And(boolean defaultValue, Expression[] expressions) {
		super("and", 0, 0, defaultValue, expressions);
	}

	@Override
	Boolean invoke(Boolean r, Boolean param) {
		return r && param;
	}

}
