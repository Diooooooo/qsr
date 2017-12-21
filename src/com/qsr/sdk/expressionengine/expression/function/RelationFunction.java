package com.qsr.sdk.expressionengine.expression.function;


import com.qsr.sdk.expressionengine.Expression;
import com.qsr.sdk.expressionengine.expression.AbstractFunction;

public abstract class RelationFunction extends
		AbstractFunction<Number, Boolean> {

	public RelationFunction(String name, int minElements, int maxElements,
			Expression[] expressions) {
		super(name, minElements, maxElements, expressions);
		// TODO Auto-generated constructor stub
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
