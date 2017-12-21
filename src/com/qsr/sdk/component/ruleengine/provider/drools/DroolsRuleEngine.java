package com.qsr.sdk.component.ruleengine.provider.drools;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.ruleengine.Rule;
import com.qsr.sdk.component.ruleengine.RuleEngine;
import com.qsr.sdk.component.ruleengine.RuleSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuan on 2016/1/11.
 */
public class DroolsRuleEngine extends AbstractComponent implements RuleEngine {

    // Charset charset=Charset.forName("utf-8");
    // KnowledgeBase knowledgeBase;
    private Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();

    public DroolsRuleEngine(Provider provider, Map<?, ?> config) {
        super(provider, config);


    }

    @Override
    public void registerRuleSet(String name, List<Rule> list) throws Exception {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        for (Rule rule : list) {
            String content = rule.getRuleContent();

//            content=content.replace("${rule_name}",rule.getName())
//            .replace("${priority}",rule.getPriority()+"");
//            if(rule.getGroup()!=null && rule.getGroup().length()>0){
//                content=content.replace("${group_name}",rule.getGroup());
//            }
//

            knowledgeBuilder.add(ResourceFactory.newByteArrayResource(content.getBytes()), ResourceType.DRL);
            if (knowledgeBuilder.hasErrors()) {
                KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
                Iterator<KnowledgeBuilderError> it = errors.iterator();
                StringBuffer sb=new StringBuffer();
                while (it.hasNext()) {
                    KnowledgeBuilderError error = it.next();
                    ResultSeverity severity = error.getSeverity();
                    Resource resource = error.getResource();
                    sb.append("error at rule ").append(rule.getName()).append("\r\n").append(" error:").append(error.getMessage());

                    int[] ls = error.getLines();

                    for (int l : ls) {
                        sb.append("error line: ").append(l).append("\r\n");
                    }
                    sb.append("\r\n\r\n");
                }
                throw new Exception(sb.toString());
            }
        }
        // knowledgeBuilder.getErrors();

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        int s=knowledgeBase.getKnowledgePackages().size();
        knowledgeBase.getKnowledgePackages().clear();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

        knowledgeBases.put(name, knowledgeBase);

    }

    @Override
    public RuleSession createRuleSession(String name) throws Exception {
        KnowledgeBase knowledgeBase = knowledgeBases.get(name);
        if (knowledgeBase == null) {
            throw new Exception("not found rule set for " + name);
        }
        StatefulKnowledgeSession statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        return (RuleSession) new DroolsRuleSession(statefulKnowledgeSession);
    }

//    @Override
//    public Object execute(Object[] params) {
//        StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();
//        Map<String,Object> output=new HashMap<>();
//        session.setGlobal("output",output);
//        session.execute(params);
//        //session.
//        return output;
//    }
}
