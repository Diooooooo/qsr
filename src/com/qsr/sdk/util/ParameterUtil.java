package com.qsr.sdk.util;

import java.util.Map;

public class ParameterUtil {

	public static Object objectParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		Object value = null;
		Object object = parameterMap.get(name);

		if (object != null) {
			value = objectValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}

		return value;
	}

	public static Object objectParam(Map<?, ?> parameterMap, String name,
			Object defaultValue) {
		Object object = parameterMap.get(name);
		return o(object, defaultValue);
	}

	public static Integer integer(Object object) {

		Integer value = null;
		if (object instanceof Number) {
			value = ((Number) object).intValue();
		} else if (object != null) {
			String s = object.toString();
			if (s.contains(".")) {
				Double d = Double.parseDouble(s);
				value = d.intValue();
			} else if (s.length() > 0) {
				value = Integer.parseInt(s);
			}
		}
		return value;
	}

	public static int i(Object object) {
		Integer value = null;
		if (object != null) {
			value = integer(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument");
		}
		return value;
	}

	public static int i(Object object, int defaultValue) {
		Integer value = null;
		if (object != null) {
			value = integer(object);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;

	}

	public static int integerParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		Integer value = null;
		Object object = parameterMap.get(name);
		if (object != null) {
			value = integer(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}
		return value;

	}

	public static int integerParam(Map<?, ?> parameterMap, String name,
			int defaultValue) {
		Object object = parameterMap.get(name);
		return i(object, defaultValue);
	}

	public static Long longValue(Object object) {

		Long value = null;
		if (object instanceof Number) {
			value = ((Number) object).longValue();
		} else if (object != null) {
			String s = object.toString();
			if (s.contains(".")) {
				Double d = Double.parseDouble(s);
				value = d.longValue();
			} else if (s.length() > 0) {
				value = Long.parseLong(s);
			}
		}
		return value;
	}

	public static long l(Object object) {
		Long value = null;
		if (object != null) {
			value = longValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument");
		}
		return value;
	}

	public static long l(Object object, long defaultValue) {

		Long value = null;

		if (object != null) {
			value = longValue(object);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static long longParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		Long value = null;
		Object object = parameterMap.get(name);
		if (object != null) {
			value = longValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}
		return value;

	}

	public static long longParam(Map<?, ?> parameterMap, String name,
			int defaultValue) {
		Object object = parameterMap.get(name);
		return l(object, defaultValue);
	}

	public static Boolean bool(Object object) {
		Boolean value = null;
		if (object instanceof Boolean) {
			value = (Boolean) object;
		} else if (object instanceof Number) {
			value = ((Number) object).intValue() > 0;
		} else if (object != null) {
			String s = object.toString();
			if(isNumeric(s)){
				value=(Integer.parseInt(s)>0);
			}
			if (s.length() > 0) {
				value = Boolean.parseBoolean(s);
			}
		}
		return value;
	}

	public static boolean b(Object object) {
		Boolean value = null;
		if (object != null) {
			value = bool(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument");
		}
		return value;
	}

	public static boolean b(Object object, boolean defaultValue) {
		Boolean value = null;
		if (object != null) {
			value = bool(object);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static boolean isNumeric(CharSequence cs) {
		if(cs==null ||cs.length()==0) {
			return false;
		} else {
			int sz = cs.length();

			for(int i = 0; i < sz; ++i) {
				if(!Character.isDigit(cs.charAt(i))) {
					return false;
				}
			}

			return true;
		}
	}
	public static boolean booleanParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		Boolean value = null;
		Object object = parameterMap.get(name);

		if (object != null) {
			value = bool(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}
		return value;

	}

	public static boolean booleanParam(Map<?, ?> parameterMap, String name,
			boolean defaultValue) {
		Object object = parameterMap.get(name);
		return b(object, defaultValue);
	}

	public static Double doubleValue(Object object) {
		Double value = null;
		if (object instanceof Number) {
			value = ((Number) object).doubleValue();
		} else if (object != null) {
			String s = object.toString();
			if (s.length() > 0) {
				value = Double.parseDouble(s);
			}
		}
		return value;
	}

	public static double d(Object object) {
		Double value = null;
		if (object != null) {
			value = doubleValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument");
		}
		return value;

	}

	public static double d(Object object, double defaultValue) {
		Double value = null;
		if (object != null) {
			value = doubleValue(object);
		}
		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	public static double doubleParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		Double value = null;
		Object object = parameterMap.get(name);
		if (object != null) {
			value = doubleValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}
		return value;
	}

	public static double doubleParam(Map<?, ?> parameterMap, String name,
			double defaultValue) {
		Object object = parameterMap.get(name);
		return d(object, defaultValue);
	}

	public static String stringValue(Object object) {
		String value = null;
		if (object instanceof String) {
			value = (String) object;
		} else if (object != null) {
			value = object.toString();
		}
		return value;
	}

	public static String s(Object object) {
		String value = null;
		if (object != null) {
			value = stringValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument");
		}
		return value;
	}

	public static String s(Object object, String defaultValue) {
		String value = null;
		if (object != null) {
			value = stringValue(object);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static Object objectValue(Object object) {
		return object;
	}

	public static Object o(Object object, Object defaultValue) {
		Object value = null;
		if (object != null) {
			value = objectValue(object);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static String stringParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {
		String value = null;
		Object object = parameterMap.get(name);

		if (object != null) {
			value = stringValue(object);
		}
		if (value == null) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}

		return value;
	}

	public static String stringTrimParam(Map<?, ?> parameterMap, String name)
			throws IllegalArgumentException {

		String value = stringParam(parameterMap, name);
		value = value.trim();
		if (value.length() < 1) {
			throw new IllegalArgumentException("illegal argument:" + name);
		}
		return value;
	}

	public static String stringParam(Map<?, ?> parameterMap, String name,
			String defaultValue) {
		Object object = parameterMap.get(name);
		return s(object, defaultValue);
	}

	public static int[] integerArray(Object object, String sp, int limit) {
		if (object == null) {
			return null;
		}
		String svalue = object.toString();
		String[] ss = svalue.split(sp, limit);
		int[] value = new int[ss.length];
		for (int i = 0; i < ss.length; i++) {
			value[i] = i(ss[i]);
		}
		return value;
	}

	public static int[] integerArray(Object object, String sp) {
		return integerArray(object, sp, 0);
	}

	//	public static String[] stringArray(Object object, int limit) {
	//		return stringArray(object, ",", limit);
	//	}

	public static String[] stringArray(Object object, String sp, int limit) {
		if (object == null) {
			return null;
		}
		String svalue = object.toString();
		String[] ss = svalue.split(sp, limit);
		return ss;
	}

	public static String[] stringArrayParam(Map<?, ?> parameterMap,
			String name, String sp, int limit) {
		Object object =objectParam(parameterMap,name);
		return stringArray(object, sp, limit);

	}

	public static int[] integerArrayParam(Map<?, ?> parameterMap, String name,
			String sp, int limit) {
		Object object =objectParam(parameterMap,name);
		return integerArray(object, sp, limit);
	}
}
