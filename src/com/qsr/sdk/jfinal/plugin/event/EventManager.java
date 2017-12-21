package com.qsr.sdk.jfinal.plugin.event;

public class EventManager {

	public static void postEvent(Event event) {
		if (EventPlugin.getInstance() == null) {

			throw new RuntimeException("请先添加事件处理插件");
		}
		EventPlugin.getInstance().addEvent(event);
	}

}
