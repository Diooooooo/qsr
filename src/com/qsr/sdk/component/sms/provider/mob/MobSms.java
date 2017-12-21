package com.qsr.sdk.component.sms.provider.mob;

import com.qsr.sdk.component.ComponentProviderException;
import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.SmsSendProvider;
import com.qsr.sdk.component.sms.VerifyResult;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.JsonUtil;
import com.qsr.sdk.util.Md5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

public class MobSms implements SmsSend {

	private static final String verify_url = "https://120.132.154.117:8443/sms/verify";
	private static final String sendcode_url = "http://sms.sharesdk.cn:5566/sms/sendcode";
	private static final String duid_url = "http://sms.sharesdk.cn:5566/init/duid";
	private static final String token_url = "http://sms.sharesdk.cn:5566/token/get";

	private static final String appkey = "5820cebd87fa";
	private static final String appSecrect = "55064e487c4d2844b28670706914b6b6";
	private static final String zone = "86";
	private static final int version = 10;

	private static final String prefix = "哎呦的验证码：";

	private static final Charset charset = Charset.forName("UTF-8");
	private static final Map<Integer, String> errorMessage = new HashMap<>();

	private static String[] fill = { "00000000", "0000000", "000000", "00000",
			"0000", "000", "00", "0", "" };

	public static final int PROVIDER_ID = 2;

	static {
		errorMessage.put(200, "发送短信成功");
		errorMessage.put(512, "服务器拒绝访问，或者拒绝操作");
		errorMessage.put(513, "求Appkey不存在或被禁用。");
		errorMessage.put(514, "权限不足");
		errorMessage.put(515, "服务器内部错误");
		errorMessage.put(517, "缺少必要的请求参数");
		errorMessage.put(518, "请求中用户的手机号格式不正确");
		errorMessage.put(519, "请求发送验证码次数超出限制");
		errorMessage.put(520, "无效验证码。");
		// errorMessage.put(526, "余额不足");
		Security.addProvider(new BouncyCastleProvider());

	}
	SmsSendProvider provider;

	public MobSms(SmsSendProvider provider) {
		//super(PROVIDER_ID);
		this.provider = provider;
	}

	@Override
	public SmsSendProvider getProvider() {
		return provider;
	}

	public void verifyResponse1(String phoneNumber, String code)
			throws ComponentProviderException {

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("appkey", appkey);
		request.put("zone", zone);
		request.put("phone", phoneNumber);
		request.put("code", code);
		String params = HttpUtil.map2Urlencoded(request, "utf-8");
		try {

			// String content = HttpUtil.post(verify_url, request);
			String content = this.requestData(verify_url, params);

			Map<String, Object> response = JsonUtil.fromJsonToMap(content);
			Object object = response.get("status");
			if (object == null || (!(object instanceof Number))) {
				throw new Exception("无效的回应数据");
			}
			int statusCode = ((Number) object).intValue();

			if (statusCode != 200) {
				throw new ComponentProviderException(this.getClass()
						.getSimpleName(), ErrorCode.ILLEGAL_DATA, "验证失败:"
						+ errorMessage.get(statusCode));
			}
		} catch (ComponentProviderException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("验证短信请求失败", e);
		}
	}

	public static String REQ_METHOD_GET = "GET";
	public static String REQ_METHOD_POST = "POST";
	// public static String REQ_METHOD_HEAD = "HEAD";
	// public static String REQ_METHOD_PUT = "OPTIONS";
	// public static String REQ_METHOD_TRACE = "TRACE";

	// 链接超时时间
	public int conn_timeout = 10000;

	// 读取超时
	public int read_timeout = 10000;

	// 请求方式
	public String method = REQ_METHOD_POST;

	/**
	 * 发起https 请求
	 * 
	 * @param address
	 * @param
	 * @return
	 * @throws Exception
	 */
	private String requestData(String address, String params) throws Exception {

		// set params ;post params
		HttpURLConnection conn = build(address);
		try {
			if (params != null) {
				conn.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(
						conn.getOutputStream());
				out.write(params.getBytes(Charset.forName("UTF-8")));
				out.flush();
				out.close();
			}
			conn.connect();
			// get result
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String result = parsRtn(conn.getInputStream());
				return result;
			} else {
				throw new Exception(conn.getResponseCode() + " "
						+ conn.getResponseMessage());
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private HttpURLConnection build(String address)
			throws NoSuchAlgorithmException, KeyManagementException,
			IOException {

		HttpURLConnection conn = null;
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());

		// ip host verify
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};

		// set ip host verify
		HttpsURLConnection.setDefaultHostnameVerifier(hv);

		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		URL url = new URL(address);
		conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod(method);// POST
		conn.setConnectTimeout(conn_timeout);
		conn.setReadTimeout(read_timeout);

		return conn;
	}

	/**
	 * 获取返回数据
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String parsRtn(InputStream is) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		boolean first = true;
		while ((line = reader.readLine()) != null) {
			if (first) {
				first = false;
			} else {
				buffer.append("\n");
			}
			buffer.append(line);
		}
		return buffer.toString();
	}

	private String getDUID() throws Exception {
		Map<String, Object> request = new HashMap<String, Object>();
		Random rand = new Random();

		request.put("appkey", appkey);
		request.put("apppkg", "");
		request.put("appver", "1");
		request.put("sdkver", version);
		request.put("plat", Integer.valueOf(1));
		request.put("network", "wifi");
		Map<String, String> deviceinfo = new HashMap<String, String>();

		deviceinfo.put("adsid", "");
		int imei = 1000000 + rand.nextInt(999999);
		// imei = fill[imei.length()] + verifyCode;
		deviceinfo.put("imei", "86559402" + imei);

		deviceinfo.put("serialno", "");

		deviceinfo.put("mac", "");

		deviceinfo.put("model", "");

		deviceinfo.put("factory", "");

		deviceinfo.put("carrier", "");

		deviceinfo.put("screensize", "");

		deviceinfo.put("sysver", "");
		deviceinfo.put("androidid", "");

		request.put("deviceinfo", deviceinfo);

		Map<String, Object> response = httpPost(duid_url, request, appSecrect,
				null, false);

		Object result = response.get("result");
		Object duid = null;
		if (result != null && result instanceof Map<?, ?>) {
			duid = ((Map<String, Object>) result).get("duid");
		}
		return duid != null ? duid.toString() : null;
	}

	private String getToken(String duid) throws Exception {
		Random rand = new Random();
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("appkey", appkey);
		request.put("duid", duid);
		request.put("sign", "" + rand.nextLong());
		Map<String, Object> response = httpPost(token_url, request,
				"com.mob.sms" + appSecrect, null, false);
		Object result = response.get("result");
		Object token = null;
		if (result != null && result instanceof Map<?, ?>) {
			token = ((Map<String, Object>) result).get("token");
		}
		return token != null ? token.toString() : null;

	}

	public static byte[] AES128Encode(byte[] key, byte[] data) {

		SecretKeySpec localSecretKeySpec = new SecretKeySpec(key, "AES");
		try {
			Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS7Padding",
					"BC");
			localCipher.init(1, localSecretKeySpec);
			byte[] arrayOfByte2 = new byte[localCipher
					.getOutputSize(data.length)];
			int i = localCipher.update(data, 0, data.length, arrayOfByte2, 0);
			localCipher.doFinal(arrayOfByte2, i);
			return arrayOfByte2;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] AES128Decode(byte[] key, byte[] data) {
		try {
			byte[] arrayOfByte1 = new byte[16];
			System.arraycopy(key, 0, arrayOfByte1, 0, Math.min(key.length, 16));
			SecretKeySpec localSecretKeySpec = new SecretKeySpec(arrayOfByte1,
					"AES");
			Cipher localCipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
			localCipher.init(2, localSecretKeySpec);
			byte[] arrayOfByte2 = new byte[localCipher
					.getOutputSize(data.length)];
			int i = localCipher.update(data, 0, data.length, arrayOfByte2, 0);
			i += localCipher.doFinal(arrayOfByte2, i);
			return arrayOfByte2;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] encode(String string, String key, boolean gzip) {
		// String json = JsonUtil.toJson(map);
		// Map<String, Object> map,
		byte[] data = string.getBytes(charset);

		byte[] md5 = Md5Util.digest(key.getBytes());

		byte[] result = AES128Encode(md5, data);
		if (gzip) {
			try {
				ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

				GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(
						localByteArrayOutputStream);

				localGZIPOutputStream.write(result);
				localGZIPOutputStream.flush();
				localGZIPOutputStream.close();
				result = localByteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return result;
	}

	private String decode(byte[] data, String key) {
		byte[] md5 = Md5Util.digest(key.getBytes());
		byte[] result = AES128Decode(md5, data);
		return new String(result, charset);
	}

	private String CRC32(byte[] bytes) {
		java.util.zip.CRC32 localCRC32 = new java.util.zip.CRC32();
		localCRC32.update(bytes);
		long l = localCRC32.getValue();

		return Long.toHexString(l);
	}

	private Map<String, String> createHeader(byte[] bytes, String token) {
		Map<String, String> header = new LinkedHashMap<String, String>();

		header.put("appkey", this.appkey);
		header.put("token", token != null ? token : "");

		header.put("hash", CRC32(bytes));
		header.put("SMS-VER", "A" + version);

		return header;

	}

	// private Map<String, Object> doRequest(String url, Map<String, Object>
	// map,
	// String key, String token, int intvalue, boolean flag) {
	//
	// return httpPost(url, map, key, token, intvalue, bytes);
	// }

	private Map<String, Object> httpPost(String url, Map<String, Object> map,
			String key, String token, boolean flag) throws Exception {

		try {
			String str = JsonUtil.toJson(map);
			byte[] bytes = encode(str, key, flag);
			Map<String, String> headers = createHeader(bytes, token);
			byte[] response = HttpUtil.post(url, bytes, headers, 5000);
			String json = decode(response, key);
			Map<String, Object> resMap = JsonUtil.fromJsonToMap(json);
			return resMap;
		} catch (Exception e) {
			throw e;
		}

	}

	private String sendVerifyCode(String zonecode, String phoneNumber,
			String duid, String token) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("appkey", appkey);
		map.put("duid", duid);
		map.put("phone", phoneNumber);
		map.put("zone", zonecode);

		Map<String, Object> request = httpPost(sendcode_url, map, duid, token,
				false);

		String vCode = (String) request.get("vCode");
		String smsId = (String) request.get("smsId");
		return vCode;

	}

	//	public static void main(String[] args) {
	//		MobSms mobsms = new MobSms();
	//		try {
	//			// mobsms.sendVerifyCode("13910209677");
	//			String verifycode = "哎呦的验证码：3554";
	//			// System.out.println(mobsms.CRC32(verifycode.getBytes()));
	//			// String duid = mobsms.getDUID();
	//			// String token = mobsms.getToken(duid);
	//			// mobsms.sendVerifyCode("86", "13910209677", duid, token);
	//
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}

	public SendResult send(String phoneNumber) throws ApiException {
		throw new ApiException(ErrorCode.NOT_IMPLEMENTS,
				"暂停服务");
//		try {
//			String duid = getDUID();
//			String token = getToken(duid);
//			String sign = sendVerifyCode("86", phoneNumber, duid, token);
//
//			return new SendResult(phoneNumber, "", sign);
//
//		} catch (Exception e) {
//			throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
//					"发送验证码失败", e);
//		}

	}

	@Override
	public SendResult send(String phoneNumber, String template, Map<String, String> templateParams) throws ApiException {
		throw new ApiException(ErrorCode.NOT_IMPLEMENTS,
				"暂停服务");
	}

	@Override
	public VerifyResult verify(String phoneNumber, String verifyCode) {
		String verifycode = prefix + verifyCode;
		String sign = verifycode;
		try {
			sign = CRC32(verifycode.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new VerifyResult(sign);
	}

}
