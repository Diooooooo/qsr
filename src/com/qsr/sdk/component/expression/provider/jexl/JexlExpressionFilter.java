package com.qsr.sdk.component.expression.provider.jexl;

import com.qsr.sdk.component.expression.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

public class JexlExpressionFilter implements Expression {

	final org.apache.commons.jexl2.Expression expression;

	public JexlExpressionFilter(org.apache.commons.jexl2.Expression expression) {
		super();
		this.expression = expression;
	}

	@Override
	public Object evaluate(Map<String, Object> context) {
		JexlContext jexlContent = new MapContext(context);
		return expression.evaluate(jexlContent);

	}

	@Override
	public int getFilterType() {
		return Expression.TYPE_EXPRESSION;
	}

}
