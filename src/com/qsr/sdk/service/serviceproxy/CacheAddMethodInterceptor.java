package com.qsr.sdk.service.serviceproxy;

import com.qsr.sdk.component.cache.Cache;
import com.qsr.sdk.component.cache.CacheManager;
import com.qsr.sdk.service.helper.CacheHelper;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.StringUtil;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheAddMethodInterceptor extends
		AbstractMethodInterceptor<CacheAdd, Cache> {

	public CacheAddMethodInterceptor() {
		super(CacheAdd.class);
	}

	protected Object getKey(Object[] args, CacheAdd cached, Signature signature) {

		int[] argsIndex = cached.keyIndexes();
		List<Object> key = new ArrayList<Object>();

		if (!StringUtil.isEmptyOrNull(cached.userKey())) {
			key.add(cached.userKey());
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
		CacheAdd cached = annotations.get(proxy.getSignature());
		Cache cache = targets.get(proxy.getSignature());
		Object result = null;

		Object key = getKey(args, cached, proxy.getSignature());

		if (cache != null) {

			result = cache.get(key);
		}

		if (result == null) {
			result = proxy.invokeSuper(obj, args);

			if (result != null && cache != null) {
				cache.put(key, result);
			}

		}

		return result;

	}

	@Override
	protected Cache createMethodTarget(Method method, Signature signature,
			CacheAdd cached) {

		CacheManager cacheManager = CacheHelper.getCacheProvider();
		String cacheName = StringUtil.isEmptyOrNull(cached.name()) ? method
				.getClass().getName() + "@" + signature.toString() : cached
				.name();

		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			cache = cacheManager.addCache(cacheName, cached.capacity(),
					cached.timeout(), cached.timeUnit(), null);
		}
		return cache;
	}

}
