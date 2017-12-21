package com.qsr.sdk.component.ruleengine;

import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 2016/1/13.
 */
public interface RuleSession {
    void addObject(Object object);
    void addObjects(List<Object> objects);
    void setGlobal(String name,Object global);
    void setGlobals(Map<String,Object> globals);
    void executeRules() throws Exception;
    List<Object> getObjects();
    void release();
}
