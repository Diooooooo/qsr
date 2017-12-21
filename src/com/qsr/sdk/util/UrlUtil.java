package com.qsr.sdk.util;

public class UrlUtil {

	public static String getUrl(String url, Object path) {

		String result = url;
		if (!result.endsWith("/")) {
			result = result + "/";
		}

		if (path != null) {
			String s = path.toString();
			if (s.startsWith("/")) {
				s = s.substring(1);
			}
			result = result + s;
		}

		return result;

	}
}
