package com.qsr.sdk.expressionengine.expression.function;


import com.qsr.sdk.expressionengine.Expression;
import com.qsr.sdk.expressionengine.exception.ExpressionException;
import com.qsr.sdk.expressionengine.expression.AbstractFunction;

import java.util.List;

public abstract class AggregationFunction<P, R> extends AbstractFunction<P, R> {
	private final R defaultValue;

	public AggregationFunction(String name, int minElements, int maxElements,
			R defaultValue, Expression[] expressions) {
		super(name, minElements, maxElements, expressions);
		this.defaultValue = defaultValue;
	}

	abstract R invoke(R r, P param);

	@Override
	protected R invoke(List<P> value) throws ExpressionException {
		R result = defaultValue;
		if (value != null) {
			for (P param : value) {
				result = invoke(result, param);
			}
		}
		return result;
	}

}
