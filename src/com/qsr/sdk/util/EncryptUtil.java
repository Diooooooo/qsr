package com.qsr.sdk.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	public static byte[] encrypt3DES(byte[] src, SecretKeySpec deskey,
			IvParameterSpec ivParam) {

		try {
			Cipher encrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
			encrypt.init(Cipher.ENCRYPT_MODE, deskey, ivParam);
			return encrypt.doFinal(src, 0, src.length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decrypt3DES(byte[] sourceBuf, SecretKeySpec deskey,
			IvParameterSpec ivParam) {
		try {
			byte[] cipherByte;
			// 获得Cipher实例，使用CBC模式。  
			Cipher decrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
			// 初始化加密实例，定义为解密功能，并传入密钥，偏转向量  
			decrypt.init(Cipher.DECRYPT_MODE, deskey, ivParam);

			cipherByte = decrypt.doFinal(sourceBuf, 0, sourceBuf.length);
			// 返回解密后的字节数组  
			return cipherByte;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
