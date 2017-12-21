package com.qsr.sdk.util;

import com.qsr.sdk.lang.function.Func1;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

public class MapUtil {

    public static Map<String, String> convertMap(Map<?, ?> input,
                                                 Map<String, String> output) {

        Map<String, String> result = output!=null?output:new HashMap<String, String>();

        if (input != null) {
            for (Entry<?, ?> entry : input.entrySet()) {
                Object value = entry.getValue();
                Object key = entry.getKey();
                if (key!=null && value != null) {
                    result.put(key.toString(), value.toString());
                }

            }
        }
        return result;
    }

    public static Map<String, String> convertMap(Map<?, ?> input) {

        Map<String, String> result = new HashMap<>();

        return convertMap(input, result);
    }

    public static <K, V> Map<K, V> cloneMap(Map<K, V> input) {
        Map<K, V> result = new HashMap<>();
        result.putAll(input);
        return result;

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

    public static String serialize(Map<?, ?> input, Charset charset) {
        List<NameValuePair> parameters = mapToNameValuePairs(input);
        String params = URLEncodedUtils.format(parameters, charset);
        return params;

    }

    public static Map<String, Object> convertMap2(Map<String, ?> input) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (input != null) {
            for (Entry<String, ?> entry : input.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    result.put(entry.getKey(), value);
                }

            }
        }
        return result;
    }

    public static <T> Map<String, T> toMap(String key, T value) {
        Map<String, T> result = new HashMap<String, T>();
        result.put(key, value);
        return result;
    }

    public static <T> Map<String, T> toMap(String key1, T value1, String key2,
                                           T value2) {
        Map<String, T> result = new HashMap<String, T>();
        result.put(key1, value1);
        result.put(key2, value2);
        return result;
    }

    public static <K, V> Map<K, V> list2Map(List<V> list, Func1<V, K> func) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (V v : list) {
            K k = func.call(v);
            result.put(k, v);
        }
        return result;
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map,
                                             Func1<Map.Entry<K, V>, Boolean> func) {
        //java.util.Collections.
        return null;
    }

    public static Map<String, String> str2map(String s, String spEntry, String spKey) {
        Map<String, String> result = new HashMap<>();
        if (s != null && s.length() > 0) {
            String[] entries = s.split(spEntry);
            for (String entry : entries) {
                String[] keyValue = entry.split(spKey, 2);
                String key = keyValue[0];
                String value = null;
                if (keyValue.length > 1) {
                    value = keyValue[1];
                }
                if (key != null) {
                    result.put(key, value);
                }

            }
        }
        return result;
    }

    public static Map<String, String> str2map(String s) {
        return str2map(s, "\n", "=");
    }

    public static String map2str(Map<?, ?> map) {
        return map2str(map, "\n", "=");
    }

    public static String map2str(Map<?, ?> map, String spEntry, String spKey) {
        StringBuffer sb = new StringBuffer();

        if (map != null && map.size() > 0) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    //String value=null;
                    if (sb.length() > 0) {
                        sb.append(spEntry);
                    }
                    sb.append(entry.getKey()).append(spKey);
                    if (entry.getValue() != null) {
                        sb.append(entry.getValue());
                    }
                }
            }
        }
        return sb.toString();
    }
   public static <K,V> void copyByKeys(Map<K,V> src,Map<K,V> dst,List<K> keys) {
       for (K key : keys) {
           if (src.containsKey(key)) {
               dst.put(key, src.get(key));
           }
       }
   }




}
