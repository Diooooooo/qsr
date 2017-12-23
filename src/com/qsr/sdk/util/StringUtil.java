package com.qsr.sdk.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringUtil {

	public static final String EMPTY_STRING = "";

	public static final String NULL_STRING = null;

	public static boolean isEmptyOrNull(String s) {
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}

	public static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}
		if (s.length() == 0) {
			return true;
		}
		if (s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String getEmptyString(String s) {
		return isEmpty(s) == false ? s : EMPTY_STRING;

	}

	public static String toDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	public static String markString(String s, int left) {

		String result = s;
		int x = left;
		if (s != null && s.length() > 0) {
			int begin = x;
			int length = s.length();
			int index = s.indexOf('@');
			int end = index > 0 ? index - x : length - x;
			if (end > begin) {
				char[] chars = s.toCharArray();
				for (int i = begin; i < end; i++) {
					chars[i] = '*';
				}
				result = new String(chars);
			}
		}
		return result;
	}

	public static String toHexString(byte[] data) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xff);
			if (hex.length() == 1) {
				sb.append("0");
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static <T> String join(String sp, List<T> params) {
		StringBuffer sb = null;

		for (Object p : params) {
			if (sb == null) {
				sb = new StringBuffer();
			} else if (sp != null) {
				sb.append(sp);
			}
			sb.append(p);
		}
		if (sb == null) {
			return null;
		} else {
			return sb.toString();
		}

	}
	public static List<String> split(String s,String sp){
		if (isEmptyOrNull(s))
			return Collections.emptyList();
		return Arrays.asList(s.split(sp));
	}

//	public static List<String> split(String s,String sp){
//		return Arrays.asList(s.split(sp));
//	}
	public static <T> String join(List<T> params) {
		return join(null, params);
	}

	//	public static <T> String joinArray(String sp, T[] params) {
	//		return join(sp, Arrays.asList(params));
	//	}
	//
	//	public static <T> String joinArray(T[] params) {
	//		return join(null, Arrays.asList(params));
	//	}

	public static String joinSp(String sp, Object... params) {
		return join(sp, Arrays.asList(params));
	}

	public static String join(Object... params) {
		return join(null, Arrays.asList(params));
	}

}
