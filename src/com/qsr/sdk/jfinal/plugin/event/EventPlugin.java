package com.qsr.sdk.jfinal.plugin.event;

import com.jfinal.plugin.IPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yuan
 *
 */
public class EventPlugin implements IPlugin {

	// 线程池
	private static ExecutorService threadPool = null;
	Map<Class<? extends Event>, EventHandler> handlers = new HashMap<>();

	static EventPlugin instance;

	static EventPlugin getInstance() {
		return instance;
	}

	public EventPlugin(int nThreads) {

		super();
		threadPool = Executors.newFixedThreadPool(nThreads);
		instance = this;

	}

	@Override
	public boolean start() {

		return true;
	}

	@Override
	public boolean stop() {

		threadPool.shutdown();
		threadPool = null;
		return true;
	}

	public void addHandler(Class<? extends Event> eventClass,
			EventHandler handler) {
		handlers.put(eventClass, handler);
	}

	public void addEvent(final Event event) {

		final EventHandler handler = handlers.get(event.getClass());
		if (handler != null) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					handler.handle(event);
				}
			});
		}
	}
}
