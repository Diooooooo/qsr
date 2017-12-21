package com.qsr.sdk.component.expression;

import java.util.Map;

public interface Expression {

	public static int TYPE_EXPRESSION = 1;
	public static int TYPE_SCRIPT = 1;

	Object evaluate(Map<String, Object> context);

	int getFilterType();

}
