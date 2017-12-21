package com.qsr.sdk.util;

public class FileUtil {

	static final String empty = "";
	static final String sp = ".";

	public static String getFileExtName(String fileName) {
		int index = fileName.lastIndexOf(sp);
		if (index >= 0) {
			return fileName.substring(index + 1);
		} else {
			return empty;
		}

	}
}
