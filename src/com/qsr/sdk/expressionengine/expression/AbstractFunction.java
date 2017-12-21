package com.qsr.sdk.expressionengine.expression;

import com.qsr.sdk.expressionengine.Expression;
import com.qsr.sdk.expressionengine.exception.ExpressionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractFunction<P, R> implements Function {

	private final Expression[] expressions;

	private final String name;
	private final int minElements;
	private final int maxElements;

	public AbstractFunction(String name, int minElements, int maxElements,
			Expression[] expressions) {
		super();
		this.name = name;
		this.minElements = minElements;
		this.maxElements = maxElements;
		this.expressions = expressions;
	}

	public Object eval(Map<String, Object> context) throws ExpressionException {

		Object result = null;
		if (expressions != null || expressions.length != 0) {
			Object[] values = new Object[expressions.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = expressions[i].eval(context);
			}
			result = invoke(values);
		}
		return result;
	}

	protected List<P> cast(Object[] values) {
		List<P> result = null;
		if (values != null) {
			result = new ArrayList<P>();
			for (int i = 0; i < values.length; i++) {
				result.add(cast(values[i]));
			}
		}
		return result;
	}

	protected abstract P cast(Object obj);

	protected abstract R invoke(List<P> value) throws ExpressionException;

	public Object invoke(Object[] values) throws ExpressionException {
		return invoke(cast(values));
	}

	public Expression[] getExpressions() {
		return expressions;
	}

	public String getName() {
		return name;
	}

	public int getMinElements() {
		return minElements;
	}

	public int getMaxElements() {
		return maxElements;
	}

}
