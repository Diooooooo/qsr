package com.qsr.sdk.component;

import com.qsr.sdk.util.GenericsUtil;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProvider<T extends Component> implements Provider {

	protected Map<Integer, T> services = new HashMap<Integer, T>();

	protected Map<Integer,Map<?,?>> serviceConfigs=new HashMap<>();

	protected final int providerId;

	protected Class<T> serviceType;

	@SuppressWarnings("unchecked")
	public AbstractProvider(int providerId) {
		super();
		this.providerId = providerId;
		Type superclass = this.getClass().getGenericSuperclass();
		serviceType = (Class<T>) GenericsUtil.getClass(superclass, 0);
	}

	@Override
	public int getProviderId() {
		return providerId;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public abstract T createComponent(int configId, Map<?, ?> config);

	@Override
	public synchronized T registerComponent(int configId, Map<?, ?> config) {
		if(config==null){
			config= Collections.emptyMap();
		}
		if(serviceConfigs.containsKey(configId)){
			Map<?, ?> map = serviceConfigs.get(configId);
			if(map.equals(config)){
				return null;
			}
		}
		T service = createComponent(configId, config);
		services.put(configId, service);
		serviceConfigs.put(configId,config);

		return service;

	}

	@Override
	public synchronized T getComponent(int configId) {
		return services.get(configId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getComponentType() {

		return serviceType;
	}
}
