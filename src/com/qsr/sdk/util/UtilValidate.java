package com.qsr.sdk.util;

import java.util.Collection;
import java.util.Map;

public class UtilValidate {

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        } else {
            return obj.equals(obj2);
        }
    }

    public static boolean isEmpty(String s) {
        return (s == null) || s.length() == 0;
    }

    public static <E> boolean isEmpty(Collection<E> c) {
        return (c == null) || c.isEmpty();
    }

    public static <K,E> boolean isEmpty(Map<K,E> m) {
        return (m == null) || m.isEmpty();
    }

    public static <K,E> boolean isNotEmpty(Map<K,E> m) {
        return (m != null) && !m.isEmpty();
    }
    
    public static boolean isNotEmpty(String s) {
        return (s != null) && s.length() > 0;
    }

    public static <E> boolean isNotEmpty(Collection<E> c) {
        return (c != null) && !c.isEmpty();
    }

    public static boolean isString(Object obj) {
        return ((obj != null) && (obj instanceof String));
    }
    
    public static boolean isEmpty(Object o) {
    	if (o == null) return true;
        return false;
    }



}
