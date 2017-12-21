package com.qsr.sdk.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Computer01 on 2016/6/3.
 * 集合交并差运算
 */

public class ListUtil {

    // 交集
    public static <T> List<T> intersect(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.retainAll(ls2);
        return list;
    }

    // 并集
    public static <T> List<T> union(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.addAll(ls2);
        return list;
    }

    // 差集
    public static <T> List<T> diff(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }
}
