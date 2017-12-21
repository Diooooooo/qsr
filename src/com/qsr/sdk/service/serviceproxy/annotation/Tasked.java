package com.qsr.sdk.service.serviceproxy.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Tasked {

	String serviceName() default "";

	String actionName() default "";

	int userIdIndex() default 0;

	int actionValueIndex() default 1;

	int taskIdIndex() default -1;

	int times() default 1;

	Success resultSuccess() default Success.Ignore;

}
