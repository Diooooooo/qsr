package com.qsr.sdk.service.serviceproxy;

import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.service.helper.CacheHelper;
import com.qsr.sdk.service.serviceproxy.annotation.CacheRemove;
import com.qsr.sdk.util.StringUtil;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheRemoveMethodInterceptor extends
		AbstractMethodInterceptor<CacheRemove, Cache> {

	CacheRemoveMethodInterceptor() {
		super(CacheRemove.class);

	}

	protected Object getKey(Object[] args, CacheRemove cacheRemove,
			Signature signature) {

		List<Object> key = new ArrayList<Object>();

		int[] argsIndex = cacheRemove.keyIndexes();

		if (!StringUtil.isEmptyOrNull(cacheRemove.userKey())) {
			key.add(cacheRemove.userKey());
		}

		if (args != null && args.length > 0 && argsIndex.length > 0) {

			if (argsIndex[0] == -1) {
				for (Object arg : args) {
					key.add(arg);
				}
			} else {
				for (int index : argsIndex) {
					key.add(args[index]);
				}
			}

		}

		return key;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = proxy.invokeSuper(obj, args);
		CacheRemove cacheRemove = annotations.get(proxy.getSignature());

		if (cacheRemove != null && cacheRemove.success().isSuccess(result)) {
			CacheManager cacheManager = CacheHelper.getCacheProvider();

			if (StringUtil.isEmptyOrNull(cacheRemove.name())) {
				return result;
			}
			Cache cache = cacheManager.getCache(cacheRemove.name());
			if (cache == null) {
				return result;
			}
			Object key = getKey(args, cacheRemove, proxy.getSignature());
			cache.remove(key);

		}

		return result;
	}

	@Override
	protected Cache createMethodTarget(Method method, Signature signature, CacheRemove annotation) {
		CacheManager cacheManager = CacheHelper.getCacheProvider();
		String cacheName = StringUtil.isEmptyOrNull(annotation.name()) ? method
				.getClass().getName() + "@" + signature.toString() :annotation
				.name();

		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			cache = cacheManager.getCache(annotation.name());
//			cache = cacheManager.addCache(cacheName, annotation.capacity(),
//					.timeout(), cached.timeUnit(), null);
		}
		return cache;
	}

}
