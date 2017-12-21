package com.qsr.sdk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationUtil {
	protected static Gson createGson() {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		return gson;
	}

	public static String toJson(Object obj) {
		Gson gson = createGson();
		return gson.toJson(obj);
	}

	public static <T> String serialize(Object object, Class<T> clazz) {
		Gson gson = createGson();
		return gson.toJson(object);

	}

	public static <T> T deserialize(String str, Class<T> clazz) {
		Gson gson = createGson();
		return gson.fromJson(str, clazz);
	}
	//	public static Object deserialize(String str) {
	//		Gson gson = createGson();
	//		return gson.fromJson(str, Object.class);
	//	}
}
