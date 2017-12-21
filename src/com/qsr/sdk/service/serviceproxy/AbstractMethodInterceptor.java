package com.qsr.sdk.service.serviceproxy;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMethodInterceptor<T extends Annotation, P>
		implements AnnotationMethodInterceptor {

	final static Logger logger = LoggerFactory
			.getLogger(AbstractMethodInterceptor.class);

	protected Class<?> supperClass;
	protected Class<?> proxyClass;
	protected Class<T> annotationClass;

	protected Map<Signature, T> annotations = new HashMap<>();
	protected Map<Signature, P> targets = new HashMap<>();

	AbstractMethodInterceptor(Class<T> classOfAnnotation) {
		annotationClass = classOfAnnotation;
	}

	protected abstract P createMethodTarget(Method method, Signature signature,
			T annotation);

	@Override
	public void setSupperClass(Class<?> supperClass) {
		this.supperClass = supperClass;

		processSupperClass(this.supperClass);
		Class<?> currentClass = this.supperClass;

		while (currentClass != null) {
			processSupperClass(currentClass);
			currentClass = currentClass.getSuperclass();
		}
	}

	protected void processSupperClass(Class<?> clazz) {

		for (Method method : clazz.getDeclaredMethods()) {
			Signature signature = ReflectUtils.getSignature(method);

			T annot = method.getAnnotation(annotationClass);
			if (annot != null) {

				P target = createMethodTarget(method, signature, annot);
				if (target != null) {
					logger.debug("proxy class {} method {} ",
							supperClass.getName(), signature.toString());
					annotations.put(signature, annot);
					targets.put(signature, target);
				}
			}
		}

	}

	@Override
	public void setProxyClass(Class<?> proxyClass) {
		this.proxyClass = proxyClass;

		//		for (Map.Entry<Signature, T> entry : annotations.entrySet()) {
		//			Signature signature = entry.getKey();
		//			MethodProxy methodProxy = MethodProxy.find(proxyClass, signature);
		//			if (methodProxy != null) {
		//				methodProxies.put(signature, methodProxy);
		//			}
		//
		//		}
	}

	@Override
	public boolean accept(Method method) {
		Signature signature = ReflectUtils.getSignature(method);
		return annotations.containsKey(signature);
	}

}
