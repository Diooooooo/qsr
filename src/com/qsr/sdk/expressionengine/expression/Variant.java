package com.qsr.sdk.expressionengine.expression;

import com.qsr.sdk.expressionengine.Expression;

import java.util.Map;

public class Variant implements Expression {

	private final String variantName;

	public Variant(String variantName) {
		super();
		this.variantName = variantName;
	}

	@Override
	public Object eval(Map<String, Object> context) {
		return context.get(variantName);
	}

}
