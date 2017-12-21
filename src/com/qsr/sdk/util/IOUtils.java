package com.qsr.sdk.util;


public class IOUtils {

	public static void closeQuietly(AutoCloseable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
			}
		}
	}
}
