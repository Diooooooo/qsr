package com.qsr.sdk.component.ruleengine.provider.drools;

import com.qsr.sdk.component.ruleengine.RuleSession;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 2016/1/13.
 */
public class DroolsRuleSession implements RuleSession {



    private final StatefulKnowledgeSession  statefulKnowledgeSession;

    public DroolsRuleSession(StatefulKnowledgeSession statefulKnowledgeSession) {
        this.statefulKnowledgeSession = statefulKnowledgeSession;
    }

    @Override
    public void addObject(Object o) {
        statefulKnowledgeSession.insert(o);
    }

    @Override
    public void addObjects(List<Object> objects) {
        for(Object o:objects){
            addObject(o);
        }
    }

    @Override
    public void setGlobal(String name, Object global) {
        statefulKnowledgeSession.setGlobal(name,global);
    }

    @Override
    public void setGlobals(Map<String, Object> globals) {
        if(globals!=null && globals.size()>0){
            for (Map.Entry<String, Object> entry : globals.entrySet()) {
                setGlobal(entry.getKey(),entry.getValue());
            }
        }
    }

    @Override
    public void executeRules() throws Exception {
        statefulKnowledgeSession.fireAllRules();
 //       statefulKnowledgeSession.fireUntilHalt();
//        StatelessKnowledgeSession s;
//        s.
    }

    @Override
    public List<Object> getObjects() {
        List<Object> result=new ArrayList<>();
//        Collection<FactHandle> factHandles = statefulKnowledgeSession.getFactHandles();
//        for(FactHandle f:factHandles){
//            result.add(((InternalFactHandle)f).getObject());
//        }
        return new ArrayList<>(statefulKnowledgeSession.getObjects());
    }

    @Override
    public void release() {
        statefulKnowledgeSession.dispose();
    }
}
