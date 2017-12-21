package com.qsr.sdk.expressionengine.expression;

import com.qsr.sdk.expressionengine.Expression;
import com.qsr.sdk.expressionengine.exception.ExpressionException;

import java.util.Map;

public class Const implements Expression {

	private final Object value;

	public Const(Object value) {
		super();
		this.value = value;
	}

	@Override
	public Object eval(Map<String, Object> context) throws ExpressionException {

		return value;
	}

}
