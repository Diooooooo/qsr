package com.qsr.sdk.component.expression;

public interface Function {

	public String getNamespace();

	public String getName();

	public Object invoke(Object[] param) throws Exception;

}
