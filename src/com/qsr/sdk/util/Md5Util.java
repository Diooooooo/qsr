package com.qsr.sdk.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Md5Util {

	public static String digest(String s) {
		return digest(s, (Charset) null);
	}

	public static byte[] digest(byte[] bytes) {

		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

	public static String digest(String s, Charset charset) {
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes;
			if (charset != null) {
				bytes = s.getBytes(charset);
			} else {
				bytes = s.getBytes();
			}
			digest.update(bytes);
			byte[] result = digest.digest();
			for (int i = 0; i < result.length; i++) {
				String hex = Integer.toHexString(result[i] & 0xff);
				if (hex.length() == 1) {
					sb.append("0");
				}
				sb.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static String digest(String s, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return digest(s, charset);
	}

	public static String sign(Map<?, ?> params, String salt) {

		List<String> keys = new ArrayList<String>();
		for (Object o : params.keySet()) {
			keys.add(o.toString());
		}

		Collections.sort(keys);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);
			if (i > 0) {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}
		if (salt != null) {
			sb.append("&");
			sb.append(salt);
		}
		return digest(sb.toString());

	}

	public static String getFileMd5(File file) throws IOException {
		if (file.exists() == false || file.isFile() == false
				|| file.canRead() == false) {
			throw new IllegalStateException("文件不存在:" + file.getPath());
		}
		InputStream in = null;
		byte[] buffer = new byte[1024];
		int len;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			return StringUtil.toHexString(digest.digest());
//			BigInteger bigInt = new BigInteger(1, digest.digest());
//			return bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}
	}

	public static String createMd5File(File file, File md5File)
			throws IOException {
		String md5Content = getFileMd5(file);
		FileWriter fw = null;
		try {
			fw = new FileWriter(md5File);
			fw.write(md5Content);
			fw.flush();
			IOUtils.closeQuietly(fw);
			fw = null;
			md5File.setLastModified(file.lastModified());
		} finally {
			IOUtils.closeQuietly(fw);
		}
		return md5Content;
	}

	public static String getFileMd5(File file, File md5File) throws IOException {
		if (file.exists() == false || file.isFile() == false
				|| file.canRead() == false) {
			throw new IllegalStateException("文件不可访问:" + file.getPath());
		}
		long lastModified = file.lastModified();
		String md5Content = null;
		if (md5File.exists() == false || file.isFile() == false
				|| file.canRead() == false
				|| md5File.lastModified() != lastModified) {

		} else {
			FileReader fr = null;
			try {
				fr = new FileReader(md5File);
				char[] s = new char[32];
				int len = fr.read(s);
				md5Content = new String(s, 0, len);
			} finally {
				IOUtils.closeQuietly(fr);
			}

		}
		return md5Content;
	}

}
