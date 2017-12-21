package com.qsr.sdk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtil {
	protected static Gson createGson() {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		return gson;
	}

	public static String toJson(Object obj) {
		Gson gson = createGson();
		return gson.toJson(obj);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		Gson gson = createGson();
		return gson.fromJson(json, clazz);
	}

	private static void processMap(Map<String, Object> map) {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object o = processObject(entry.getValue());
			if (o != null) {
				entry.setValue(o);
			}
		}
	}

	private static void processList(List<Object> list) {

		for (int i = 0; i < list.size(); i++) {
			Object o = processObject(list.get(i));
			if (o != null) {
				list.set(i, o);
			}

		}

	}

	private static Object processObject(Object object) {
		if (object instanceof Map<?, ?>) {
			processMap((Map<String, Object>) object);
		} else if (object instanceof List<?>) {
			processList((List<Object>) object);
		} else if (object instanceof Double) {

			Double value = (Double) object;
			if (value < Long.MAX_VALUE && value > Long.MIN_VALUE
					&& value.toString().indexOf('.') == -1) {
				return (Long) value.longValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromJsonToMap(String json) {
		Gson gson = createGson();
		Object result = gson.fromJson(json, Object.class);
		if (result instanceof Map<?, ?> == false) {
			return Collections.emptyMap();
		}
		processMap((Map<String, Object>) result);

		return (Map<String, Object>) result;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> fromJsonToList(String json) {
		Gson gson = createGson();
		Object result = gson.fromJson(json, Object.class);
		if (result instanceof List<?> == false) {
			return Collections.emptyList();
		}
		processList((List<Object>) result);

		return (List<Object>) result;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Map<String, T>> fromJsonToMapList(String json) {

		return (List<Map<String, T>>) (Object) fromJsonToList(json);
	}
}
