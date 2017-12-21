package com.qsr.sdk.expressionengine.expression.function;

import com.qsr.sdk.expressionengine.Expression;

public abstract class ArithmeticFunction extends
		AggregationFunction<Number, Number> {

	public ArithmeticFunction(String name, int minElements, int maxElements,
			Number defaultValue, Expression[] expressions) {
		super(name, minElements, maxElements, defaultValue, expressions);

	}

	@Override
	protected Number cast(Object obj) {
		Number result = null;
		if (obj instanceof Number) {
			result = (Number) obj;
		} else if (obj instanceof String) {
			result = Double.parseDouble((String) obj);
		} else if (obj != null) {
			result = Double.parseDouble(obj.toString());
		}
		return result;
	}

}
