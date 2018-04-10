package com.qsr.sdk.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

public class TokenUtil {

	private static final byte[] SECRET = "@".getBytes();

	private static final String sp = "/";

	private static byte[] getHmacSHA1(byte[] src) {
		try {

			Mac mac = Mac.getInstance("HmacSHA1");

			SecretKeySpec secret = new SecretKeySpec(SECRET, mac.getAlgorithm());
			mac.init(secret);

			return mac.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getHmacSHA1(String s) {

		byte[] src = s.getBytes();

		byte[] dest = getHmacSHA1(src);

		return StringUtil.toHexString(dest);

	}

	public static String generate(Object... o) {
		String s0 = "";

		if (o != null && o.length > 0) {
			s0 = StringUtil.joinSp(sp, o);
		}

		String s1 = StringUtil.joinSp(sp, s0, UUID.randomUUID().toString(),
				System.currentTimeMillis());
		String value1 = getHmacSHA1(s1);
		String value2 = getHmacSHA1(value1);
		return value1 + value2.substring(10, 30);

	}

	public static boolean verifyToken(String token) {
		if (token == null || token.length() != 60) {
			return false;
		}
		String value1 = token.substring(0, 40);
		String value2 = token.substring(40);

		String value2FromValue1 = getHmacSHA1(value1).substring(10, 30);
		return value2FromValue1.equals(value2);

	}
}
