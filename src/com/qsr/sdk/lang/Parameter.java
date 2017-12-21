package com.qsr.sdk.lang;

import com.qsr.sdk.util.ParameterUtil;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parameter {

	private Map<?, ?> parameterMap;
	protected Charset charset = Charset.forName("ISO-8859-1");

	public Parameter(Map<?, ?> parameterMap) {

		this.parameterMap = parameterMap;
		if (parameterMap == null) {
			throw new IllegalArgumentException("params is null");
		}

	}

	protected Parameter() {
		this.parameterMap = new LinkedHashMap<Object, Object>();
	}

	protected void setParameterMap(Map<?, ?> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public Map<?, ?> getParameterMap() {
		return parameterMap;
	}

	public Object o(String name) throws IllegalArgumentException {
		return ParameterUtil.objectParam(parameterMap, name);
	}

	public Object o(String name, Object defaultValue) {
		return ParameterUtil.objectParam(parameterMap, name, defaultValue);
	}

	public int i(String name) throws IllegalArgumentException {
		return ParameterUtil.integerParam(parameterMap, name);
	}

	public int i(String name, int defaultValue) {
		return ParameterUtil.integerParam(parameterMap, name, defaultValue);
	}

	public long l(String name) throws IllegalArgumentException {
		return ParameterUtil.longParam(parameterMap, name);
	}

	public long l(String name, int defaultValue) {
		return ParameterUtil.longParam(parameterMap, name, defaultValue);
	}

	public boolean b(String name) throws IllegalArgumentException {
		return ParameterUtil.booleanParam(parameterMap, name);

	}

	public boolean b(String name, boolean defaultValue) {
		return ParameterUtil.booleanParam(parameterMap, name, defaultValue);
	}

	public double d(String name) throws IllegalArgumentException {
		return ParameterUtil.doubleParam(parameterMap, name);
	}

	public double d(String name, double defaultValue) {
		return ParameterUtil.doubleParam(parameterMap, name, defaultValue);
	}

	public String s(String name) throws IllegalArgumentException {
		return ParameterUtil.stringParam(parameterMap, name);
	}

	public String s(String name, String defaultValue) {
		return ParameterUtil.stringParam(parameterMap, name, defaultValue);
	}

	public String[] as(String name) {
		return as(name, ",");
	}
	public int[] is(String name) {
		return is(name,",");
	}
	public int[] is(String name, String sp) {
		return ParameterUtil.integerArrayParam(parameterMap, name, sp, 0);
	}

	public String[] as(String name, String sp) {
		return ParameterUtil.stringArrayParam(parameterMap, name, sp, 0);
	}

	@Override
	public String toString() {
		if (parameterMap != null) {
			return parameterMap.toString();
		} else {
			return "{}";
		}

	}

}
