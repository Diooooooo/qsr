package com.qsr.sdk.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class DataSecret {
	private static String keyCode = "=@.p3!-3";// ��Կ��������ģ�ֻҪ�ܳ�����8���ֽھ���

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**   
	* @Description 加密操作
	* @author wujp
	* @date 2014-4-11 下午2:44:16 
	* 
	*/ 
	public static String encryptDES(String encryptString) throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(keyCode.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes("utf-8"));
		return Base64.encode(encryptedData);

	}

	/**   
	* @Description 解密
	* @author wujp
	* @date 2014-4-11 下午2:43:37 
	* 
	*/ 
	public static String decryptDES(String decryptString) throws Exception {
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(keyCode.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);
		return new String(decryptedData, "utf-8");
	}

	/**
	 * ��������ת����16����
	 * 
	 * @param buf
	 * 
	 * @return String
	 */

	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

}