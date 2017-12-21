package com.qsr.sdk.service.serviceproxy.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface CacheAdd {

	String name() default "";

	int capacity() default 1000;

	long timeout() default 600;

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	int[] keyIndexes() default { -1 };

	String userKey() default "";
}
