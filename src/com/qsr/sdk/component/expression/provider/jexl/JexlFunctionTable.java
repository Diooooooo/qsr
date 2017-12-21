package com.qsr.sdk.component.expression.provider.jexl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JexlFunctionTable {

	private final Map<String, JexlFunction> functions;

	public JexlFunctionTable(Map<String, JexlFunction> functions) {
		this.functions = functions;
		if (this.functions == null) {
			functions = Collections.emptyMap();
		}
	}

	public JexlFunctionTable() {
		this(new HashMap<String, JexlFunction>());
	}

	public JexlFunction put(JexlFunction function) {
		return functions.put(function.getName(), function);
	}

	public JexlFunction get(String name) {
		return functions.get(name);
	}
}
