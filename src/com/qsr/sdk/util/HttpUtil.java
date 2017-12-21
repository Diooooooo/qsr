package com.qsr.sdk.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuan
 *
 */
public class HttpUtil {

	private static ContentType default_content_type = ContentType.create(
			"text/plain", Consts.UTF_8);

	public static String postMultiPart(String url, Map<String, Object> request)
			throws IOException {
		// MultipartEntity entity = new MultipartEntity();
		// for (Map.Entry<String, Object> entry : request.entrySet()) {
		// String key = entry.getKey();
		// Object value = entry.getKey();
		// if (value != null)
		// if (value instanceof File) {
		// FileBody fileBody = new FileBody((File) value);
		// entity.addPart(key, fileBody);
		// } else {
		// try {
		// StringBody stringBody = new StringBody(key, "UTF-8");
		// entity.addPart(key, stringBody);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// HttpEntity response = postHttpEntity(url, entity, 30000);
		// String jsonString = EntityUtils.toString(response, "UTF-8");
		// return jsonString;
		return null;
	}

	public static String post(String url, Map<String, Object> request)
			throws IOException {
		return post(url, request, Env.getCharset(), Env.getTimeout());
	}

	private static void close(Closeable close) {
		if (close != null) {
			try {
				close.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static List<NameValuePair> mapToNameValuePairs(Map<?, ?> request) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for (Map.Entry<?, ?> entry : request.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();
			if (value != null) {
				NameValuePair nameValuePairs = new BasicNameValuePair(key,
						value.toString());
				parameters.add(nameValuePairs);
			}
		}
		return parameters;
	}

	public static String map2Urlencoded(Map<?, ?> request, String charset) {
		List<NameValuePair> parameters = mapToNameValuePairs(request);
		String params = URLEncodedUtils.format(parameters, charset);
		return params;
	}

	public static String urlencoded(String url, Map<?, ?> request) {
		return urlencoded(url, request, null);
	}

	public static String urlencoded(String url, Map<?, ?> request,
			Charset charset) {
		return urlencoded(url, request, false, charset);
	}

	public static String urlencoded(String url, Map<?, ?> request,
			boolean fragment, Charset charset) {
		String params = null;
		String result = url;
		if (request != null && request.size() > 0) {
			List<NameValuePair> parameters = mapToNameValuePairs(request);
			params = URLEncodedUtils.format(parameters, charset);
		}
		if (params != null) {
			result = result + (fragment ? "#" : "?") + params;
		}
		return result;
	}

	private static HttpUriRequest createHttpMethod(String method, String url,
			Map<?, ?> request, String charset, int timeout) throws IOException {

		if ("post".equalsIgnoreCase(method)) {
			List<NameValuePair> parameters = mapToNameValuePairs(request);
			HttpEntity entity = new UrlEncodedFormEntity(parameters, charset);
			HttpPost httpPost = new HttpPost(url);
			// httpPost.setHeader("Connection", "close");
			httpPost.setEntity(entity);
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
					.build();
			httpPost.setConfig(config);
			return httpPost;
		} else
		/* if ("get".equalsIgnoreCase(method)) */
		{

			HttpGet httpGet = new HttpGet(urlencoded(url, request,
					Charset.forName(charset)));
			// httpGet.setParams(null);
			return httpGet;
		}

	}

	public static byte[] post(String url, byte[] data,
			Map<String, String> header, int timeout) throws Exception {
		CloseableHttpClient httpClient = createHttpClient(url);
		CloseableHttpResponse response = null;
		try {

			ByteArrayEntity entity = new ByteArrayEntity(data);
			HttpPost httpPost = new HttpPost(url);
			if (header != null && header.size() > 0) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}

			httpPost.setEntity(entity);

			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
					.build();
			httpPost.setConfig(config);
			HttpResponse entityResponse = httpClient.execute(httpPost);
			int statusCode = entityResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = entityResponse.getEntity()
						.getContent();
				return IOUtils.toByteArray(inputStream);
			} else {
				throw new Exception("post failed:" + statusCode);
			}

		} finally {
			close(response);
			close(httpClient);
		}
	}

	public static String post(String url, Map<?, ?> request, Charset charset,
			int timeout) throws IOException {

		List<NameValuePair> parameters = mapToNameValuePairs(request);

		HttpEntity entity = new UrlEncodedFormEntity(parameters, charset);
		HttpPost httpPost = new HttpPost(url);

		HttpEntity respHttpEntity = httpEntityRequest(url, httpPost, entity,
				timeout);

		String result = EntityUtils.toString(respHttpEntity, charset);
		return result;

	}

	public static String post(String url, String content) throws IOException {
		return post(url, content, default_content_type, Env.getCharset(),
				Env.getTimeout());
	}

	public static String post(String url, String content, String mimeType,
			Charset charset, int timeout) throws IOException {
		ContentType contentType = null;
		if (mimeType != null) {
			contentType = ContentType.create(mimeType, charset);
		} else {
			contentType = default_content_type;
		}
		return post(url, content, contentType, charset, timeout);

	}

	public static String post(String url, String content,
			ContentType contentType, Charset charset, int timeout)
			throws IOException {

		HttpEntity entity = new StringEntity(content, contentType);
		HttpPost httpPost = new HttpPost(url);

		HttpEntity respHttpEntity = httpEntityRequest(url, httpPost, entity,
				timeout);

		String result = EntityUtils.toString(respHttpEntity, charset);
		return result;
	}

	protected static HttpEntity httpEntityRequest(String url,
			HttpEntityEnclosingRequestBase entityRequest, HttpEntity entity,
			int timeout) throws IOException {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = createHttpClient(url);
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
					.build();
			entityRequest.setConfig(config);
			entityRequest.setEntity(entity);

			response = httpClient.execute(entityRequest);
			HttpEntity resEntity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
					resEntity);
			return bufferedHttpEntity;
		} finally {
			close(response);
			close(httpClient);
		}
	}

	private static CloseableHttpClient createHttpClient(String url) {
		CloseableHttpClient httpclient = null;
		if (url.startsWith("https")) {

			try {
				/*
				 * KeyStore trustStore = KeyStore.getInstance(KeyStore
				 * .getDefaultType());
				 * 
				 * trustStore.load(null, null);
				 * 
				 * // Trust own CA and all self-signed certs SSLContext
				 * sslcontext = SSLContexts .custom()
				 * .loadTrustMaterial(trustStore, new
				 * TrustSelfSignedStrategy()).build(); // Allow TLSv1 protocol
				 * only SSLConnectionSocketFactory sslsf = new
				 * SSLConnectionSocketFactory( sslcontext, new String[] {
				 * "TLSv1" }, null,
				 * SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				 * httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
				 * .build();
				 */
				SSLContext sslcontext = SSLContexts.custom()
						.loadTrustMaterial(new TrustSelfSignedStrategy())
						.build();
				// Allow TLSv1 protocol only
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext, new String[] { "TLSv1", "TLSv1.1",
								"TLSv1.2", "SSLv3" }, null,
						SSLConnectionSocketFactory.getDefaultHostnameVerifier());
				httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
						.build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		} else {
			httpclient = HttpClients.createDefault();
		}
		return httpclient;

	}

	public static String post(String url, String urlencoded, String charset,
			int timeout) throws IOException {
		CloseableHttpClient httpClient = createHttpClient(url);
		CloseableHttpResponse response = null;
		try {
			HttpEntity entity = new StringEntity(urlencoded,
					"application/x-www-form-urlencoded");
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
					.build();
			httpPost.setConfig(config);
			response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			String string = null;
			string = EntityUtils.toString(resEntity, charset);
			return string;

		} finally {
			close(response);
			close(httpClient);
		}
	}

	public static String get(String url, Map<?, ?> request) throws IOException {
		return get(url, request, "UTF-8", 3000);
	}

	public static void getWithInputStreamToFile(String url, Map<?, ?> request, String charset, int timeout, File file) throws IOException {
		CloseableHttpClient httpClient = createHttpClient(url);
		CloseableHttpResponse response = null;
		try (FileOutputStream fos = new FileOutputStream(file)) {

			HttpGet httpUriRequest = new HttpGet(urlencoded(url, request,
					Charset.forName(charset)));

//			HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
//					.setProxy(proxy)
					.build();
			httpUriRequest.setConfig(config);

			response = httpClient.execute(httpUriRequest);
			HttpEntity resEntity = response.getEntity();
            IOUtils.copy(resEntity.getContent(), fos);
        } finally {
			close(response);
			close(httpClient);
		}
	}

	public static void postWithInputStreamFile(String url, Map<?, ?> request, File file) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(url);
        CloseableHttpResponse response = null;
        try (FileOutputStream fos = new FileOutputStream(file)){
            List<NameValuePair> parameters = mapToNameValuePairs(request);
            HttpEntity entity = new UrlEncodedFormEntity(parameters, Env.getCharset().toString());
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(Env.getTimeout()).setSocketTimeout(Env.getTimeout()).build();
            httpPost.setConfig(config);
            response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            IOUtils.copy(resEntity.getContent(), fos);
        } finally {
            close(response);
            close(httpClient);
        }
	}

	public static String get(String url, Map<?, ?> request, String charset,
			int timeout) throws IOException {

		CloseableHttpClient httpClient = createHttpClient(url);
		CloseableHttpResponse response = null;
		try {

			HttpGet httpUriRequest = new HttpGet(urlencoded(url, request,
					Charset.forName(charset)));

//			HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
//					.setProxy(proxy)
					.build();
			httpUriRequest.setConfig(config);

			response = httpClient.execute(httpUriRequest);
			HttpEntity resEntity = response.getEntity();
			String string = null;
			string = EntityUtils.toString(resEntity, charset);
			return string;

		} finally {
			close(response);
			close(httpClient);
		}

	}

}
