package com.qsr.sdk.service.ruleexecutor;


/**
 * Created by yuan on 2016/3/28.
 */
public class LoggerFactory {

    static class LoggerImpl implements Logger {

        private final org.slf4j.Logger logger;

        LoggerImpl(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        public void trace(String s, Object... objects) {
            if(objects==null) {
                logger.trace(s);
            }else{
                logger.trace(s, objects);
            }
        }

        public void debug(String s, Object... objects) {
            if(objects==null) {
                logger.debug(s);
            }else{
                logger.debug(s, objects);
            }
        }

        public void info(String s, Object... objects) {
            if(objects==null) {
                logger.info(s);
            }else{
                logger.info(s, objects);
            }
        }

        public void warn(String s, Object... objects) {
            if(objects==null) {
                logger.warn(s);
            }else{
                logger.warn(s, objects);
            }
        }

        public void error(String s, Object... objects) {
            if(objects==null) {
                logger.error(s);
            }else{
                logger.error(s, objects);
            }
        }
    }
    public static Logger getLogger(String name){
        return new LoggerImpl(org.slf4j.LoggerFactory.getLogger(name));
    }
    public static Logger getLogger(Class<?> clazz){
        return new LoggerImpl(org.slf4j.LoggerFactory.getLogger(clazz));
    }

}
