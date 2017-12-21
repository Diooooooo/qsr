package com.qsr.sdk.component.ruleengine.provider.drools;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.ruleengine.RuleEngine;
import com.qsr.sdk.component.ruleengine.RuleEngineProvider;

import java.util.Map;

/**
 * Created by yuan on 2016/1/11.
 */
public class DroolsProvider extends AbstractProvider<RuleEngine>
        implements RuleEngineProvider
{


    public DroolsProvider() {
        super(1);
    }

    @Override
    public RuleEngine createComponent(int configId, Map<?,?> config) {
        return new DroolsRuleEngine(this,config);
    }
}
