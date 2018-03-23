package com.qsr.sdk.component.payment;

import java.util.Map;

public class Refund {
    private final String type;
    private final String typeValue;
//    private String
//    private String
//    private String
    private Map<String, ?> conf;

    public Refund(String type, String typeValue) {
        this.type = type;
        this.typeValue = typeValue;
    }

    public Map<String, ?> getConf() {
        return conf;
    }

    public void setConf(Map<String, ?> conf) {
        this.conf = conf;
    }
}
