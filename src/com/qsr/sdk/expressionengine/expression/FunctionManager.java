package com.qsr.sdk.expressionengine.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionManager {

	private static Map<String, Function> functions = new ConcurrentHashMap<String, Function>();

	public static boolean registerFunction(AbstractFunction function) {
		functions.put(function.getName().toLowerCase(), function);
		return true;
	}

	public static Function getFunction(String name) {
		return functions.get(name.toLowerCase());
	}

}
