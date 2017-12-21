package com.qsr.sdk.service.serviceproxy.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Asyned {

	public int minThreadCount() default 1;

	public int maxThreadcount() default 10;

	public String name() default "";

}
