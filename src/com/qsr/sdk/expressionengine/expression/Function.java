package com.qsr.sdk.expressionengine.expression;

public interface Function {

	Object invoke(Object[] values);

	String getName();

	int getMinElements();

	int getMaxElements();
}
