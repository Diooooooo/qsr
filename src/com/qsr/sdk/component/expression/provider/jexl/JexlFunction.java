package com.qsr.sdk.component.expression.provider.jexl;

import com.qsr.sdk.component.expression.Function;
import org.apache.commons.jexl2.introspection.JexlMethod;

public class JexlFunction implements JexlMethod {

	private final Function function;

	public JexlFunction(Function function) {
		this.function = function;
	}

	public String getName() {
		return this.getName();
	}

	@Override
	public Object invoke(Object obj, Object[] params) throws Exception {
		return function.invoke(params);
	}

	@Override
	public Object tryInvoke(String name, Object obj, Object[] params) {
		try {
			return function.invoke(params);
		} catch (Exception xany) {
			// ignore and fail by returning this
		}
		return function;

	}

	@Override
	public boolean tryFailed(Object rval) {
		return rval == this;
	}

	@Override
	public boolean isCacheable() {
		return true;
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

}
