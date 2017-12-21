package com.qsr.sdk.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WorkingResourceUtil {

	private static String working;
	static {

		Properties p = new Properties();
		try {
			p.load(currentClassLoader().getResourceAsStream(
					"working.properties"));
			working = p.getProperty("working", "product");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static String getWorkingFileName(String fileName) {
		return working + "/" + fileName;
	}

	private static ClassLoader currentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static InputStream getInputStream(String fileName) {
		return currentClassLoader().getResourceAsStream(
				getWorkingFileName(fileName));
	}

	public static File getFile(String fileName) {
		URL url = currentClassLoader()
				.getResource(getWorkingFileName(fileName));
		return new File(url.getFile());
	}

	public static Map<String, String> loadPropertFileWithWorking(String fileName) {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = currentClassLoader().getResourceAsStream(
				fileName);

		try {
			Properties p = new Properties();
			p.load(inputStream);
			map.putAll((Map) p);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return map;

	}

	public static Map<String, String> loadPropertFile(String fileName) {
		fileName = getWorkingFileName(fileName);
		return loadPropertFileWithWorking(fileName);
	}
}
