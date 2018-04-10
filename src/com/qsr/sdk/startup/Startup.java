package com.qsr.sdk.startup;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.lang.tuple.Tuple2;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.WorkingResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

public class Startup {

	final static Logger logger = LoggerFactory.getLogger(Startup.class);

	static Stack<Tuple2<String, Runnable>> onstarts = new Stack<>();
	static Stack<Tuple2<String, Runnable>> onstops = new Stack<>();

	public static void registerOnStart(String message, Runnable onstart) {
		onstarts.push(new Tuple2<String, Runnable>(message, onstart));
	}

	public static void registerOnStop(String message, Runnable onstart) {
		onstops.push(new Tuple2<String, Runnable>(message, onstart));
	}

	public static void start() {

		try {

			reloadLogConfig();
			logger.info("startup......");
			Env.load();

			logger.info("start load service providers");
			ComponentProviderManager.loadServiceProviders();

			while (!onstarts.isEmpty()) {
				Tuple2<String, Runnable> tuple = onstarts.pop();
				logger.info(tuple.getElement1());
				tuple.getElement2().run();
			}
			//			logger.info("start init ServiceCacheManager");
			//			ServiceCacheManager.init();

		} catch (Exception e) {
			logger.error("startup exception", e);
			throw new RuntimeException(e);

		}
	}

	public static void stop() {
		logger.info("stop......");

		while (!onstops.isEmpty()) {
			Tuple2<String, Runnable> tuple = onstops.pop();
			logger.info(tuple.getElement1());
			tuple.getElement2().run();
		}
		//		NotifyManager.stop();
	}

	public static void reloadLogConfig() throws Exception {
		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();

		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);

		context.reset();
		configurator.doConfigure(WorkingResourceUtil.getFile("logback.xml"));
	}
}
