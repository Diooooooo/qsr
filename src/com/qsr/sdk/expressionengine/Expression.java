package com.qsr.sdk.expressionengine;

import com.qsr.sdk.expressionengine.exception.ExpressionException;

import java.util.Map;

public interface Expression {

	public Object eval(Map<String, Object> context) throws ExpressionException;
}
