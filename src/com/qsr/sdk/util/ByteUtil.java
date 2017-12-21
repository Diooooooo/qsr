package com.qsr.sdk.util;

public class ByteUtil {

	public static byte[] getBytes(long l) {
		byte[] result = new byte[8];
		long t = l;
		for (int i = 0; i < result.length; i++) {
			result[7 - i] = (byte) (t & 0x0ff);
			t = t >> 8;
		}
		return result;
	}
}
