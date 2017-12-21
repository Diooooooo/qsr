package com.qsr.sdk.util;

import java.util.HashMap;
import java.util.Map;

public class Radix62 extends Number {

	/**   */
	private static final long serialVersionUID = -5106481550555758962L;
	final static int max_legnth = 32;
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };

	final static Map<Character, Integer> digitsMap = new HashMap<Character, Integer>();

	private long value;

	public Radix62(long value) {
		this.value = value;
	}

	public Radix62(String value) {
		this.value = _parseRadix62(value);
	}

	static {
		for (int i = 0; i < digits.length; i++) {
			digitsMap.put(digits[i], i);
		}
	}

	public String toString(int length) {

		boolean fixlength = true;
		if (length <= 0) {
			length = max_legnth;
			fixlength = false;
		}

		char[] s = new char[length];
		int radix = digits.length;
		int charPos = length - 1;
		int mode;
		long l = value;
		while (l >= radix && charPos > 0) {
			mode = (int) (l % radix);
			s[charPos--] = digits[mode];
			l = l / radix;
		}
		mode = (int) (l % radix);
		s[charPos] = digits[mode];
		if (fixlength) {
			for (int i = 0; i < charPos; i++) {
				s[i] = digits[0];
			}
			charPos = 0;
		}
		return new String(s, charPos, length - charPos);
	}

	public String toString() {
		return toString(0);
	}

	public static Radix62 parseRadix62(String s) {
		return new Radix62(s);
	}

	private long _parseRadix62(String s) {
		char[] b = s.toCharArray();
		long result = 0;
		int radix = digits.length;
		Integer digit;
		for (int i = 0; i < b.length; i++) {
			digit = digitsMap.get(b[i]);
			if (digit == null) {
				throw new NumberFormatException("illegal string for radix62: "
						+ s);
			}
			result *= radix;
			result += digit;
		}
		return result;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return (int) value;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return value;
	}
}
