package com.qsr.sdk.service.serviceproxy;

import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;

public interface AnnotationMethodInterceptor extends MethodInterceptor {

	public abstract void setSupperClass(Class<?> supperClass);

	public abstract void setProxyClass(Class<?> proxyClass);

	public abstract boolean accept(Method method);

}