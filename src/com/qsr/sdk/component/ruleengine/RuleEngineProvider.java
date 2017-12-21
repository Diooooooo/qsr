package com.qsr.sdk.component.ruleengine;

import com.qsr.sdk.component.Provider;

/**
 * Created by yuan on 2016/1/11.
 */
public interface RuleEngineProvider extends Provider {

    public RuleEngine getComponent(int configId);
}
