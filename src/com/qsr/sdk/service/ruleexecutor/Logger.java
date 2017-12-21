package com.qsr.sdk.service.ruleexecutor;

/**
 * Created by yuan on 2016/3/10.
 */
public interface Logger {

//    public static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    void trace(String s, Object... objects) ;
    void debug(String s, Object... objects) ;
    void info(String s, Object... objects) ;
    void warn(String s, Object... objects) ;
    void error(String s, Object... objects) ;
}
