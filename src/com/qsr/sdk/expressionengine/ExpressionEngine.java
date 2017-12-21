package com.qsr.sdk.expressionengine;

import com.qsr.sdk.expressionengine.exception.ExpressionException;

import java.util.Map;

public class ExpressionEngine {

	//boolean filter();
	public Object evalExpression(Expression expression,
			Map<String, Object> context) throws ExpressionException {

		return expression.eval(context);

	}
}
