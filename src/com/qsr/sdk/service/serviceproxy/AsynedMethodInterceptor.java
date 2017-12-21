package com.qsr.sdk.service.serviceproxy;

import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.Asyned;
import com.qsr.sdk.startup.Startup;
import com.qsr.sdk.util.StringUtil;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsynedMethodInterceptor extends
        AbstractMethodInterceptor<Asyned, ExecutorService> {

    public AsynedMethodInterceptor() {
        super(Asyned.class);
    }

    static class ThreadFactoryImpl implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ThreadFactoryImpl(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = name + poolNumber.getAndIncrement();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    final static String NULL_STRING = "<null>";
    final static Logger logger = LoggerFactory
            .getLogger(AsynedMethodInterceptor.class);

    private static Map<String, ExecutorService> threadPools = new ConcurrentHashMap<>();

    @Override
    protected ExecutorService createMethodTarget(Method method,
                                                 Signature signature, Asyned annotation) {
        String name;
        if (StringUtil.isEmptyOrNull(annotation.name())) {
            name = signature.toString();
        } else {
            name = annotation.name();
        }
        ExecutorService executorService = threadPools.get(name);
        if (executorService == null) {

            executorService = new ThreadPoolExecutor(
                    annotation.minThreadCount(), annotation.maxThreadcount(),
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new ThreadFactoryImpl(
                    name));
            final ExecutorService s = executorService;
            threadPools.put(name, executorService);
            Startup.registerOnStop("shutdown for thread pools :" + name,
                    new Runnable() {
                        @Override
                        public void run() {
                            s.shutdown();
                        }
                    });

        }

        return executorService;
    }

    @Override
    public Object intercept(final Object obj, Method method,
                            final Object[] args, final MethodProxy proxy) throws Throwable {
        Object result = null;
        ExecutorService executorService = targets.get(proxy.getSignature());
        if (executorService != null) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                         proxy.invokeSuper(obj, args);

                    } catch (ServiceException e) {

                        logger.warn("exception at servicename={},message={},args={}",
                                e.getServiceName(), e.getMessage(),
                                args == null ? NULL_STRING : args.toString());

                    } catch (Throwable e) {

                        String param = args == null ? NULL_STRING : args
                                .toString();

                        logger.error("async call method " + proxy.getSignature()
                                        + ",params=" + param, e);

                    }
                }
            });

        } else {
            result = proxy.invokeSuper(obj, args);
        }
        return result;
    }

}
