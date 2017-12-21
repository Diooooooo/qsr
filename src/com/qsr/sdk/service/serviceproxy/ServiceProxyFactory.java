package com.qsr.sdk.service.serviceproxy;

import net.sf.cglib.proxy.Enhancer;

public class ServiceProxyFactory {

	@SuppressWarnings("unchecked")
	public static <T> T createService(Class<T> clazz) {

		MethodInterceptorHolder holder = new MethodInterceptorHolder();

		holder.add(new CacheAddMethodInterceptor());
		holder.add(new CacheRemoveMethodInterceptor());
		holder.add(new CacheClearMethodInterceptor());
		holder.add(new AsynedMethodInterceptor());
		holder.setSupperClass(clazz);

		Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setCallbacks(holder.getCallbacks());
		e.setCallbackFilter(holder);

		Object object = e.create();
		holder.setProxyClass(object.getClass());

		return (T) object;

	}
}
