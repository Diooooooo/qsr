package com.qsr.sdk.service.ruleexecutor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.service.helper.CacheHelper;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by yuan on 2016/3/24.
 */
public class FactLoader {
    private static org.slf4j.Logger logger= LoggerFactory.getLogger(FactLoader.class);

    public List<Map<String,Object>> getFactList(String sql){
        return Db.find(sql).stream().map(m->m.getColumns()).collect(Collectors.toList());
    }
    public List<Map<String,Object>> getFactListByParams(String sql,Object...objects){
        return Db.find(sql,objects).stream().map(m->m.getColumns()).collect(Collectors.toList());
    }
    public Map<String,Object> getFirstFact(String sql){
        Record first = Db.findFirst(sql);
        return first!=null?first.getColumns():null;
    }
    public Map<String,Object> getFirstFactByParams(String sql,Object...objects){
        Record first = Db.findFirst(sql,objects);
        return first!=null?first.getColumns():null;
    }

    protected String getCacheName(String sql){
        return CacheHelper.generateKey(sql);
    }
    public synchronized List<Map<String,Object>> getFactListWithCache(String sql){
        String cacheName= "getFactListWithCache";
        String cacheKey=CacheHelper.generateKey("",sql);
        Cache cache = CacheHelper.getCache(cacheName);
        if(cache!=null){
            cache=CacheHelper.addCache(cacheName,2000,10, TimeUnit.MINUTES,null);
        }
        List<Map<String,Object>> result=null;
        if(cache!=null){
            try {
                result= (List<Map<String, Object>>) cache.get(cacheKey);
                if(result!=null){
                    return result;
                }
            } catch (Exception e) {
                logger.error("getFactListWithCache",e);
            }
        }
        if(result==null) {
            result = getFactList(sql);
            if(cache!=null){
                try {
                    cache.put(cacheKey,result);
                } catch (Exception e) {
                    logger.error("getFactListWithCache",e);
                }
            }
        }
        return result;
    }
    public List<Map<String,Object>> getFactListByParamsWithCache(String sql,Object...objects){
        return null;
    }
    public Map<String,Object> getFirstFactWithCache(String sql){
        return null;
    }
    public Map<String,Object> getFirstFactByParamsWithCache(String sql,Object...objects){
        return null;
    }

}
