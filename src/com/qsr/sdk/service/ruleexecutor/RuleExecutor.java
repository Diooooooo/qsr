package com.qsr.sdk.service.ruleexecutor;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.ruleengine.Rule;
import com.qsr.sdk.component.ruleengine.RuleEngine;
import com.qsr.sdk.component.ruleengine.RuleSession;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by yuan on 2016/3/17.
 */
public class RuleExecutor {

    private volatile long last_update_time = 0;
    private volatile long next_check_time = 0;
    private volatile boolean has_filter_rule = false;
    private final String ruleTable;
    private final String name;
    private final com.qsr.sdk.service.ruleexecutor.Logger logger4rule;
    final static Logger logger = LoggerFactory
            .getLogger(RuleExecutor.class);

    private final RuleEngine ruleEngine;

    public RuleExecutor(String name, String ruleTable, String loggerName) {
        this.ruleTable = ruleTable;
        this.name = name;
        ruleEngine = ComponentProviderManager.getService(RuleEngine.class, 1, 1);
        logger4rule = com.qsr.sdk.service.ruleexecutor.LoggerFactory.getLogger(loggerName);

    }

    protected synchronized boolean checkRuleIsUpdated() {
        boolean result = false;
        long now = System.currentTimeMillis();
        if (now > next_check_time) {
            next_check_time = now + Env.getRuleCheckDuration();
            String sql = "SELECT MAX(updatetime) from " + ruleTable;
            Date date = Db.queryDate(sql);
            if (date != null && date.getTime() != last_update_time) {
                last_update_time = date.getTime();
                result = true;
            }
        }
        return result;
    }

    protected void registerRules() throws ApiException {
        String sql = "select * from " + ruleTable + " where enabled=1 and deleted=0 ";
        List<Record> list = Db.find(sql);

        List<Rule> rules = new ArrayList<>();
        for (Record r : list) {
            Map<String, Object> vars = new HashMap<>(r.getColumns());
            String ruleContent = TemplateUtil.process(r.getStr("rule_content"), vars);
            Rule rule = new Rule(r.getStr("name"), ruleContent, r.getInt("priority"), r.getStr("group_name"));
            rules.add(rule);
        }
        if (rules.size() > 0) {
            registerRules(rules);
        }
        has_filter_rule = (rules.size() > 0);
    }

    protected void registerRules(List<Rule> list) throws ApiException {
        try {
            ruleEngine.registerRuleSet(name, list);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "注册过滤规则异常(" + name + ")", e);
        }
    }

    protected List<Object> executeRule(List<Object> objects, Map<String, Object> globals) throws ApiException {
        RuleSession ruleSession = null;
        try {
            ruleSession = ruleEngine.createRuleSession(name);
            ruleSession.addObjects(objects);
            ruleSession.setGlobals(globals);
            ruleSession.executeRules();
            return ruleSession.getObjects();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
                    "执行过滤规则异常(" + name + ")", e);
        } finally {
            if (ruleSession != null) {
                ruleSession.release();
            }
        }
    }

    public List<Object> execute(Object... objects) throws ApiException {
        return execute(null, Arrays.asList(objects));
    }

    public List<Object> execute(Map<String, Object> globals, List<Object> objects) throws ApiException {
        List<Object> ruleObjects = new ArrayList<>();

        Map<String, Object> ruleGlobals = new HashMap<>();
        ruleGlobals.put("logger", logger4rule);
        if (globals != null) {
            ruleGlobals.putAll(globals);
        }
        if (objects != null && objects.size() > 0) {
            for (Object o : objects) {
                if (o instanceof Map<?, ?> && !(o instanceof HashMap<?, ?>)) {
                    //经过测试,drools fact 不能识别Map,只能换成HashMap
                    ruleObjects.add( new HashMap((Map)o));
                } else {
                    ruleObjects.add(o);
                }
            }
        }
        if (checkRuleIsUpdated()) {
            try {
                registerRules();
            } catch (ServiceException e) {
                logger.error("executeRule:" + name, e);
            }
        }
        if (has_filter_rule) {
            return executeRule(ruleObjects, ruleGlobals);
        }
//        return output;
        return Collections.emptyList();
    }
}
