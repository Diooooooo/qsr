package com.qsr.sdk.component;

import com.qsr.sdk.component.arraystorage.provider.redis.RedisArrayStorageProvider;
import com.qsr.sdk.component.bytestorage.provider.redis.RedisByteStorageProvider;
import com.qsr.sdk.component.cache.provider.ehcache.EhcacheProvider;
import com.qsr.sdk.component.cache.provider.mapcache.MapCacheProvider;
import com.qsr.sdk.component.filestorage.provider.alioss.AliOssProvider;
import com.qsr.sdk.component.payment.provider.alipay.AliPaymentProvider;
import com.qsr.sdk.component.payment.provider.yhxf.YhxfProvider;
import com.qsr.sdk.component.push.provider.apns4j.ApnsPushProvider;
import com.qsr.sdk.component.push.provider.getxin.GetxinPushProvider;
import com.qsr.sdk.component.ruleengine.provider.drools.DroolsProvider;
import com.qsr.sdk.component.sms.provider.alidayu.DaYuProvider;
import com.qsr.sdk.component.stats.provider.redis.RedisStatsProvider;
import com.qsr.sdk.component.thirdaccount.provider.weixin.WeiXinAccountProvider;
import com.qsr.sdk.component.transfer.provider.alitransfer.AliTransferProvider;
import com.qsr.sdk.util.IOUtils;
import com.qsr.sdk.util.WorkingResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentProviderManager {
	public static int DEFAULT_SERVICE_CONFIG_ID = 1;
	static ConcurrentHashMap<Class<? extends Component>, Map<Integer, Provider>> spis = new ConcurrentHashMap<>();

	public synchronized static <T extends Component> Provider registerProvider(
			Provider provider) {

		Map<Integer, Provider> providers = spis.get(provider.getComponentType());
		if (providers == null) {
			providers = new HashMap<>();
			spis.put(provider.getComponentType(), providers);
		}
		providers.put(provider.getProviderId(), provider);
		return provider;
	}

	public static Provider registerService(Provider provider, int configId,
			Map<String, String> config) {
		provider.registerComponent(configId, config);
		return provider;

	}

	public static <T extends Component> Provider getServiceProvider(
			Class<T> serviceClass, int providerId) {

		Map<Integer, Provider> providers = spis.get(serviceClass);
		if (providers == null) {
			return null;
		}
		return providers.get(providerId);

	}

	public static <T extends Component> T getService(Class<T> serviceClass,
													 int providerId, int configId) {
		Provider provider = getServiceProvider(serviceClass, providerId);
		if (provider == null) {
			return null;
		}
		return (T) provider.getComponent(configId);

	}

	public static <T extends Component> T getService(Class<T> serviceClass,
													 int providerId) {
		return getService(serviceClass, providerId, DEFAULT_SERVICE_CONFIG_ID);
	}

	public static void loadServiceProviders() {

		// 支付服务提供商

		ProviderBuilder.getProviderBuilder(new AliPaymentProvider())
				.registerProvider().registerComponent();
		ProviderBuilder.getProviderBuilder(new YhxfProvider())
				.registerProvider().registerComponent();

		// 第三方帐号提供商

		ProviderBuilder.getProviderBuilder(new WeiXinAccountProvider())
				.registerProvider().registerComponent(1).registerComponent(2)
				.registerComponent(3);

		//		ServiceProviderManager
		//				.addServiceProvider(new WXMobileGameChannelThirdAccount());
		//
		//		ServiceProviderManager.addServiceProvider(new WeiXinAccount());
		// Push服务提供商
		ProviderBuilder.getProviderBuilder(new GetxinPushProvider())
				.registerProvider().registerComponent();

		ProviderBuilder.getProviderBuilder(new ApnsPushProvider())
				.registerProvider().registerComponent();

		// 短信验证提供商

//		ProviderBuilder.getProviderBuilder(new DxtSmsProvider())
//				.registerProvider().registerComponent();
//		ProviderBuilder.getProviderBuilder(new MobSmsProvider())
//				.registerProvider().registerComponent();
		ProviderBuilder.getProviderBuilder(new DaYuProvider())
				.registerProvider().registerComponent();

		// 转账提供商
		ProviderBuilder.getProviderBuilder(new AliTransferProvider())
				.registerProvider().registerComponent();

		// 缓存提供商
		ProviderBuilder.getProviderBuilder(new MapCacheProvider())
				.registerProvider().registerComponent();

		ProviderBuilder.getProviderBuilder(new EhcacheProvider())
				.registerProvider().registerComponent();

		// 存储提供商
		ProviderBuilder.getProviderBuilder(new AliOssProvider())
				.registerProvider()
				.registerComponent(2, loadProperty("oss_2.properties"))
				.registerComponent(3, loadProperty("oss_3.properties"))
				.registerComponent(4, loadProperty("oss_4.properties"));

		ProviderBuilder.getProviderBuilder(new DroolsProvider())
				.registerProvider()
				.registerComponent(1, null);

		Map<Object, Object> redisConfig = loadProperty("redis.properties");

		// 数据统计提供商
		ProviderBuilder.getProviderBuilder(new RedisStatsProvider())
				.registerProvider()
				.registerComponent(5, redisConfig);

		// 数组存储提供商
		ProviderBuilder.getProviderBuilder(new RedisArrayStorageProvider())
				.registerProvider()
				.registerComponent(6, redisConfig);

		// 二进制存储提供商
		ProviderBuilder.getProviderBuilder(new RedisByteStorageProvider())
				.registerProvider()
				.registerComponent(7, redisConfig);

		// 本地消息队列提供商
//		ProviderBuilder.getProviderBuilder(new LocalMsgQueueProvider())
//				.registerProvider()
//				.registerComponent(8, loadProperty("mq_local.properties"));

		// 阿里云消息队列提供商
//		ProviderBuilder.getProviderBuilder(new AliMnsProvider())
//				.registerProvider()
//				.registerComponent(9, loadProperty("alimns.properties"));


		//		ProviderBuilder.getProviderBuilder(new RedisDataStorageProvider())
		//				.registerProvider()
		//				.registerComponent(1, loadProperty("redis.properties"));

	}

	public static Map<Object, Object> loadProperty(String filename) {

		InputStream inputStream = null;

		try {
			Properties properties = new Properties();
			inputStream = WorkingResourceUtil.getInputStream(filename);
			properties.load(inputStream);
			return new HashMap<Object, Object>(properties);
		} catch (IOException e) {
			throw new RuntimeException("打开文件错误," + filename, e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
