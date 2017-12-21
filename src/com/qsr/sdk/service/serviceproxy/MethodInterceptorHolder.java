package com.qsr.sdk.service.serviceproxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodInterceptorHolder implements CallbackFilter {

	List<Callback> callbacks = new ArrayList<>();
	private static final Callback[] EMPTY = {};

	public MethodInterceptorHolder() {
		callbacks.add(NoOp.INSTANCE);
	}

	public void add(AnnotationMethodInterceptor interceptor) {

		callbacks.add(interceptor);
	}

	public Callback[] getCallbacks() {
		return callbacks.toArray(EMPTY);
	}

	public int getCallbackIndex(Method method) {

		for (int i = 1; i < callbacks.size(); i++) {
			AnnotationMethodInterceptor interceptor = (AnnotationMethodInterceptor) callbacks
					.get(i);
			if (interceptor.accept(method)) {
				return i;
			}
		}
		return 0;
	}

	public void setSupperClass(Class<?> supperClass) {
		for (int i = 1; i < callbacks.size(); i++) {
			AnnotationMethodInterceptor interceptor = (AnnotationMethodInterceptor) callbacks
					.get(i);
			interceptor.setSupperClass(supperClass);

		}
	}

	public void setProxyClass(Class<?> proxyClass) {
		for (int i = 1; i < callbacks.size(); i++) {
			AnnotationMethodInterceptor interceptor = (AnnotationMethodInterceptor) callbacks
					.get(i);
			interceptor.setProxyClass(proxyClass);
		}
	}

	@Override
	public int accept(Method method) {
		for (int i = 1; i < callbacks.size(); i++) {
			AnnotationMethodInterceptor interceptor = (AnnotationMethodInterceptor) callbacks
					.get(i);
			if (interceptor.accept(method)) {
				return i;
			}
		}
		return 0;
	}

}
