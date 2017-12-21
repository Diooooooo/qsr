package com.qsr.sdk.service.serviceproxy.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface CacheClear {

	String name() default "";

	Success success() default Success.Ignore;
}
