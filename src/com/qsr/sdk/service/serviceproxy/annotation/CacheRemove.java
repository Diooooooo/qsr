package com.qsr.sdk.service.serviceproxy.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface CacheRemove {

	String name() default "";

	int[] keyIndexes() default { -1 };

	Success success() default Success.Ignore;

	String userKey() default "";
}
