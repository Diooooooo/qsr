package com.qsr.sdk.service.helper;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.ruleengine.Rule;
import com.qsr.sdk.component.ruleengine.RuleEngine;
import com.qsr.sdk.component.ruleengine.RuleSession;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 2016/1/13.
 */
public class FilterHelper {

    public static int status=0;
    public static int a=100;

    public static final String device_filter_rules_name="device_filter_rules";
    public static final String product_filter_rules_name="product_filter_rules";
    public static final String transfer_filter_rules_name="transfer_filter_rules";

    public static RuleEngine getDeviceFilterRuleEngine() throws ApiException {
        RuleEngine ruleEngine = ComponentProviderManager.getService(RuleEngine.class, 1,1);
        if (ruleEngine == null) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "不存在的过滤服务");
        }
        return ruleEngine;
    }
    public static void registerFilterRules(String name,List<Rule> list) throws ApiException {
        RuleEngine deviceFilterRuleEngine = getDeviceFilterRuleEngine();
        try {
            deviceFilterRuleEngine.registerRuleSet(name,list);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "注册过滤规则异常("+name+")",e);
        }
    }

    public static List<Object> executeFilterRule(String name,List<Object> objects,Map<String,Object> globals) throws ApiException {
        RuleEngine deviceFilterRuleEngine = getDeviceFilterRuleEngine();
        RuleSession ruleSession=null;
        try {
            ruleSession = deviceFilterRuleEngine.createRuleSession(name);
            ruleSession.addObjects(objects);
            ruleSession.setGlobals(globals);
            ruleSession.executeRules();
            return ruleSession.getObjects();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "执行过滤规则异常("+name+")",e);
        }
        finally {
            if(ruleSession!=null){
                ruleSession.release();
            }
        }
    }
    public static List<Object> executeDeviceFilterRule(List<Object> objects,Map<String,Object> globals) throws ApiException {
        return executeFilterRule(device_filter_rules_name,objects,globals);
    }
    public static void registerDeviceFilterRules(List<Rule> list) throws ApiException {
        registerFilterRules(device_filter_rules_name,list);
    }


    public static void registerProductFilterRules(List<Rule> list) throws ApiException {
        registerFilterRules(product_filter_rules_name,list);
    }

    public static List<Object> executeProductFilterRule(List<Object> objects,Map<String,Object> globals) throws ApiException {
        return executeFilterRule(product_filter_rules_name,objects,globals);
    }

    public static void registerTransferFilterRules(List<Rule> list) throws ApiException {
        registerFilterRules(transfer_filter_rules_name,list);
    }

    public static List<Object> executeTransferFilterRule(List<Object> objects,Map<String,Object> globals) throws ApiException {
        return executeFilterRule(transfer_filter_rules_name,objects,globals);
    }
}
