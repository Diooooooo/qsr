package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.util.SerializationUtil;

public class DataSerialization {

	public static <T> String serialize(Object value, Class<T> clazz) {
		return SerializationUtil.serialize(value, clazz);

	}

	public static <T> T deserialize(String value, Class<T> clazz) {
		return SerializationUtil.deserialize(value, clazz);
	}
}
