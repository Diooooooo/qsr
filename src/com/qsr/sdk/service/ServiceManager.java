package com.qsr.sdk.service;

import com.qsr.sdk.service.serviceproxy.ServiceProxyFactory;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager {

	static Map<Class<? extends Service>, Service> servers = new HashMap<>();

	public static <T extends Service> T getService(Class<T> clazz) {

		try {

			@SuppressWarnings("unchecked")
			T service = (T) servers.get(clazz);
			if (service == null) {

				synchronized (servers) {
					if (service == null) {
						service = ServiceProxyFactory.createService(clazz);
						servers.put(clazz, service);
					}
				}

			}

			return service;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}
}
