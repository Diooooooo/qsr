package com.qsr.sdk.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class GenericsUtil {

	//	private static abstract class Dummy<T> {
	//
	//		Type type;
	//		Class<T> serviceType;
	//
	//		public Dummy() {
	//			//this.type = (Class<T>) getClass();
	//			//			Type superClass = getClass().getGenericSuperclass();
	//			//			type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
	//			//			serviceType = (Class<T>) type;
	//		}
	//
	//	}
	//	public static Class<?> getSuperClassGenricType(Class<?> clazz) {
	//		return getSuperClassGenricType(clazz, 0);
	//	}

	public static Class<?> getClass(Type type, int index) {
		if (type instanceof ParameterizedType) {
			return getGenericClass((ParameterizedType) type, index);
		} else if (type instanceof TypeVariable<?>) {
			return getClass(((TypeVariable<?>) type).getBounds()[0], 0);
		} else {
			return (Class<?>) type;
		}
	}

	public static Class<?> getGenericClass(ParameterizedType parameterizedType,
			int index) {

		Class<?> result = Object.class;
		Type[] types = parameterizedType.getActualTypeArguments();

		Type type = types[index];

		if (type instanceof ParameterizedType) {
			result = (Class<?>) ((ParameterizedType) type).getRawType();
		} else if (type instanceof GenericArrayType) {
			result = (Class<?>) ((GenericArrayType) type)
					.getGenericComponentType();
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> varType = (TypeVariable<?>) type;
			Type boundType = varType.getBounds()[0];
			result = getClass(boundType, 0);
		} else {
			result = (Class<?>) type;
		}
		return result;

	}

	static abstract class Dummy<T> {

		public Dummy() {
			Type gps = this.getClass().getGenericSuperclass();
			System.out.println(GenericsUtil.getClass(gps, 0));
		}
	}

	public static <T> Class<T> getClassType() {
		Dummy<T> gc = new Dummy<T>() {
		};

		Type[] gis = gc.getClass().getGenericInterfaces(); // 接口的泛型信息     
		Type gps = gc.getClass().getGenericSuperclass(); // 父类的泛型信息     
		TypeVariable[] gtr = gc.getClass().getTypeParameters(); // 当前接口的参数信息     
		System.out.println("============== getGenericInterfaces");
		for (Type t : gis) {
			System.out.println(t + " : " + getClass(t, 0));
		}
		System.out.println("============== getGenericSuperclass");
		System.out.println(getClass(gps, 0));
		System.out.println("============== getTypeParameters");
		for (TypeVariable t : gtr) {
			StringBuilder stb = new StringBuilder();
			for (Type tp : t.getBounds()) {
				stb.append(tp + " : ");
			}

			System.out.println(t + " : " + t.getName() + " : " + stb);
		}
		return null;
	}
}
