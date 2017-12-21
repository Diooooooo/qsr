package com.qsr.sdk.util;

import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by fc on 2016/4/29.
 */
public class DESUtil {

     private static byte[] desCryt(byte[] data, byte[] key, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] result;
        try {
            Cipher cipher = getCipher(key, mode);
            result = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }catch (NoSuchPaddingException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        } catch (IllegalBlockSizeException e) {
            throw e;
        } catch (BadPaddingException e) {
            throw e;
        }
         return result;
    }

    public static Cipher getDecryptCipherInstance(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return getCipher(Base64.getDecoder().decode(key), Cipher.DECRYPT_MODE);
    }

    public static Cipher getEncryptCipherInstance(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return getCipher(Base64.getDecoder().decode(key), Cipher.ENCRYPT_MODE);
    }

    private static Cipher getCipher(byte[] key, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cp = null;
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DES");
            cp = Cipher.getInstance("DES/ECB/ISO10126Padding");
            cp.init(mode, secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (NoSuchPaddingException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        }
        return cp;
    }

    public static byte[] encrypt(String data, byte[] key, String encoding) throws
            UnsupportedEncodingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException {
        return desCryt(data.getBytes(encoding), key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException {
        return desCryt(data, key, Cipher.DECRYPT_MODE);
    }

    public static String decrypt(String data, String key, String charset) throws
            IOException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            NoSuchPaddingException {
        return new String(decrypt(new BASE64Decoder().decodeBuffer(data), new BASE64Decoder().decodeBuffer(key)), charset);
    }

    public static byte[] encrypt(String data, String key, String encoding) throws
            IOException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            NoSuchPaddingException {
        return encrypt(data, new BASE64Decoder().decodeBuffer(key), encoding);
    }
}
