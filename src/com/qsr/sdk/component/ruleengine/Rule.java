package com.qsr.sdk.component.ruleengine;

/**
 * Created by yuan on 2016/1/13.
 */
public class Rule {
    private final String name;
    private final String ruleContent;
    private final int priority;
    private final String group;


    public Rule(String name, String ruleContent, int priority, String group) {
        this.name = name;
        this.ruleContent = ruleContent;
        this.priority = priority;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public int getPriority() {
        return priority;
    }

    public String getGroup() {
        return group;
    }
}
