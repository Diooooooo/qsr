package com.qsr.sdk.service.serviceproxy;

import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.service.helper.CacheHelper;
import com.qsr.sdk.service.serviceproxy.annotation.CacheClear;
import com.qsr.sdk.util.StringUtil;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CacheClearMethodInterceptor extends
		AbstractMethodInterceptor<CacheClear, CacheClear> {

	CacheClearMethodInterceptor() {
		super(CacheClear.class);

	}

	//	protected Object getKey(Object[] args, int[] argsIndex) {
	//
	//		List<Object> key = new ArrayList<Object>();
	//
	//		if (args != null && args.length > 0 && argsIndex.length > 0) {
	//
	//			if (argsIndex[0] == -1) {
	//				for (Object arg : args) {
	//					key.add(arg);
	//				}
	//			} else {
	//				for (int index : argsIndex) {
	//					key.add(args[index]);
	//				}
	//			}
	//
	//		}
	//
	//		return key;
	//	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = proxy.invokeSuper(obj, args);
		CacheClear cacheclear = targets.get(proxy.getSignature());

		if (cacheclear != null && cacheclear.success().isSuccess(result)) {
			CacheManager cacheManager = CacheHelper.getCacheProvider();

			if (StringUtil.isEmptyOrNull(cacheclear.name())) {
				return result;
			}
			Cache cache = cacheManager.getCache(cacheclear.name());
			if (cache == null) {
				return result;
			}
			cache.clear();

		}

		return result;
	}

	@Override
	protected CacheClear createMethodTarget(Method method, Signature signature,
			CacheClear annotation) {
		return annotation;

	}

}
