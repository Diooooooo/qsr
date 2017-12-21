package com.qsr.sdk.expressionengine.expression.function;


import com.qsr.sdk.expressionengine.Expression;

public abstract class LogicFunction extends
		AggregationFunction<Boolean, Boolean> {

	public LogicFunction(String name, int minElements, int maxElements,
			Boolean defaultValue, Expression[] expressions) {
		super(name, minElements, maxElements, defaultValue, expressions);
	}

	@Override
	protected Boolean cast(Object obj) {
		Boolean result = null;
		if (obj instanceof Boolean) {
			result = (Boolean) obj;
		} else if (obj instanceof Number) {
			Number n = (Number) obj;
			result = n.longValue() > 0;
		} else if (obj instanceof String) {
			result = Boolean.parseBoolean((String) obj);
		} else if (obj != null) {
			result = Boolean.parseBoolean(obj.toString());
		}
		return result;
	}

}
