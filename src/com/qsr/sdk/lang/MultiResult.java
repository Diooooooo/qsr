package com.qsr.sdk.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuan on 2016/3/22.
 */
public class MultiResult {

    private final Map<Object,Object> results =new HashMap<>();

    public MultiResult(){
    }
    public MultiResult(Object...objects){

        for(Object o:objects){
            if(o instanceof Map.Entry<?,?>){
                results.put(((Map.Entry) o).getKey(),((Map.Entry) o).getValue());
            }else if(o!=null){
                results.put(o.getClass(),o);
            }
        }
    }
    public void addResult(Object key,Object value){
        results.put(key,value);
    }
    public <T> T getResult(Class<T> classOfT){
        return (T) results.get(classOfT);
    }
    public Object getResult(Object key){
        return results.get(key);
    }

    public Map<Object, Object> getResults() {
        return results;
    }
}
