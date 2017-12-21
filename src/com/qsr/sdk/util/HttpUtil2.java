package com.qsr.sdk.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yuan on 2016/1/26.
 */
public class HttpUtil2 {

    static Charset default_charset=Charset.forName("UTF-8");

    private static void addParams(RequestBuilder requestBuilder, Map<?, ?> params) throws Exception {
        if (params != null && params.size() > 0) {
//            URIBuilder uriBuilder=new URIBuilder(requestBuilder.getUri());
//            uriBuilder.setCharset(requestBuilder.getCharset());
            params.entrySet().stream()
                    .filter((a) -> a.getKey() != null && a.getValue() != null)
                    .forEach(c -> requestBuilder.addParameter(c.getKey().toString(), c.getValue().toString()));
//            requestBuilder.setUri(uriBuilder.build());
        }
    }

    private static void addFormFields(RequestBuilder requestBuilder, Map<?, ?> params) throws Exception {
        if (params != null && params.size() > 0) {
            List<NameValuePair> collect = params.entrySet().stream()
                    .filter((a) -> a.getKey() != null && a.getValue() != null)
                    .map(a -> (NameValuePair) new BasicNameValuePair(a.getKey().toString(), a.getValue().toString()))
                    .collect(Collectors.toList());
            UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(collect,requestBuilder.getCharset());
            requestBuilder.setEntity(urlEncodedFormEntity);
        }
    }

    private static void addHeaders(RequestBuilder requestBuilder, Map<?, ?> headers) {
        if (headers != null && headers.size() > 0) {
            headers.entrySet().stream()
                    .filter(a -> a.getKey() != null && a.getValue() != null)
                    .forEach(c -> requestBuilder.addHeader(c.getKey().toString(), c.getValue().toString()));
        }
    }

    //    private static CloseableHttpResponse execute(HttpUriRequest httpUriRequest ) throws IOException {
//        CloseableHttpClient httpClient	=	HttpClients.createDefault();
//        return httpClient.execute(httpUriRequest);
//    }
    public static String get4String(String url, Map<?, ?> params, Map<?, ?> headers, Charset charset, int timeout) throws Exception {

        RequestBuilder requestBuilder = RequestBuilder.get(url);
        requestBuilder.setCharset(charset);

        addParams(requestBuilder, params);

        addHeaders(requestBuilder, headers);

        if (timeout >= 0) {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout).setSocketTimeout(timeout)
                    .build();
            requestBuilder.setConfig(config);
        }

        HttpUriRequest httpUriRequest = requestBuilder.build();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = httpClient.execute(httpUriRequest);
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new Exception("get failed:" + statusCode);
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity(), charset);
        } finally {
            IOUtils.closeQuietly(httpClient);
            IOUtils.closeQuietly(closeableHttpResponse);
        }
    }
    public static String postForm4String(String url, Map<?, ?> params, Map<?, ?> headers,Map<?,?> fields,  Charset charset, int timeout) throws Exception {
        RequestBuilder requestBuilder = RequestBuilder.post(url);
        requestBuilder.setCharset(charset);
        addParams(requestBuilder, params);
        addHeaders(requestBuilder, headers);


        addFormFields(requestBuilder,fields);

        if (timeout >= 0) {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout).setSocketTimeout(timeout)
                    .build();
            requestBuilder.setConfig(config);
        }

        HttpUriRequest httpUriRequest = requestBuilder.build();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;
        try {

            closeableHttpResponse = httpClient.execute(httpUriRequest);
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new Exception("post failed:" + statusCode);
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity(), charset);
        } finally {
            IOUtils.closeQuietly(httpClient);
            IOUtils.closeQuietly(closeableHttpResponse);
        }
    }
    public static  String postForm(String url,Map<?,?> fields) throws Exception {
        return postForm4String(url,null,null,fields,default_charset,3000);
    }
    public static  String postForm(String url,Map<?,?> headers,Map<?,?> fields) throws Exception {
        return postForm4String(url,null,headers,fields,default_charset,3000);
    }
    public static  String postForm(String url,Map<?,?> params,Map<?,?> headers,Map<?,?> fields) throws Exception {
        return postForm4String(url,params,headers,fields,default_charset,3000);
    }
    public static String get4String(String url, Map<?, ?> params, Map<?, ?> header) throws Exception {
        return get4String(url, params, header, default_charset, 3000);
    }
    public static String get4String(String url, Map<?, ?> params) throws Exception {
        return get4String(url, params, null, default_charset, 3000);
    }
}
