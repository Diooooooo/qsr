package com.qsr.sdk.component.ruleengine;

import com.qsr.sdk.component.Component;

import java.util.List;

/**
 * Created by yuan on 2016/1/11.
 */
public interface RuleEngine extends Component {
    //Object execute(Object[] params);
    void registerRuleSet(String name, List<Rule> list) throws Exception;
    RuleSession createRuleSession(String name) throws Exception;

}
