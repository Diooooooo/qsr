package com.qsr.sdk.component.keyvalue.provider.mapkeyvalue;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.keyvalue.KeyValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuan on 2016/3/16.
 */
public class MapKeyValue extends AbstractComponent implements KeyValue {

    private Map<Object, Object> map = new HashMap<>();

    public MapKeyValue(Provider provider, Map<?, ?> config) {
        super(provider, config);
    }

    @Override
    public synchronized Object get(Object key) {
        return map.get(key);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public synchronized Object incr(Object key, Object delta) {
        Object value = map.get(key);
        if (value == null) {
            map.put(key, delta);
        } else {
            value=add(value,delta,true);
            map.put(key,value);
        }
        return value;
    }

    @Override
    public synchronized Object decr(Object key, Object delta) {
        Object value = map.get(key);
        if (value == null) {
            map.put(key, delta);
        } else {
            value=add(value,delta,false);
            map.put(key,value);
        }
        return value;
    }

    private Object add(Object value, Object delta, boolean add) {

        Number nValue;
        Number nDelta;
        if (value instanceof Number) {
            nValue = (Number) value;
        } else {
            nValue = Long.parseLong(value.toString());
        }
        if (delta instanceof Number) {
            nDelta = (Number) delta;
        } else {
            nDelta = Long.parseLong(delta.toString());
        }
        if (nValue instanceof Long) {
            nValue = add ? nValue.longValue() + nDelta.longValue() : nValue.longValue() - nDelta.longValue();
        } else if (nValue instanceof Integer) {
            nValue = add ? nValue.intValue() + nDelta.intValue() : nValue.intValue() - nDelta.intValue();
        } else if (nValue instanceof Byte) {
            nValue = add ? nValue.byteValue() + nDelta.byteValue() : nValue.byteValue() - nDelta.byteValue();
        } else if (nValue instanceof Short) {
            nValue = add ? nValue.shortValue() + nDelta.shortValue() : nValue.shortValue() - nDelta.shortValue();
        } else if (nValue instanceof Double) {
            nValue = add ? nValue.doubleValue() + nDelta.doubleValue() : nValue.doubleValue() - nDelta.doubleValue();
        } else if (nValue instanceof Float) {
            nValue = add ? nValue.floatValue() + nDelta.floatValue() : nValue.floatValue() - nDelta.floatValue();
        }
//        else if(nValue instanceof BigInteger){
//            BigInteger bigInteger=(BigInteger) nValue;
//            nValue=add?nValue.longValue()+nDelta.longValue():nValue.longValue()-nDelta.longValue();
//        }else if(nValue instanceof BigDecimal){
//            nValue=add?nValue.longValue()+nDelta.longValue():nValue.longValue()-nDelta.longValue();
//        }

        return nValue;

    }

}
