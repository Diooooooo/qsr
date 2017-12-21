package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.datastorage.*;
import com.qsr.sdk.util.ParameterUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

class RedisDataStorage extends AbstractComponent implements DataStorage {

	private final JedisPool pool;

	private static final String datakeys = "metadata";

	public RedisDataStorage(Provider provider, Map<?, ?> config) {

		super(provider);

		JedisPoolConfig poolconfig = new JedisPoolConfig();
		poolconfig.setMaxTotal(ParameterUtil.integerParam(config, "maxTotal",
				500));

		poolconfig.setMaxIdle(ParameterUtil.integerParam(config, "maxIdel", 5));
		poolconfig.setMaxWaitMillis(ParameterUtil.integerParam(config,
				"maxWait", 100) * 1000);

		poolconfig.setTestOnBorrow(ParameterUtil.booleanParam(config,
				"testOnBorrow", false));
		pool = new JedisPool(poolconfig, ParameterUtil.stringParam(config,
				"host"), ParameterUtil.integerParam(config, "port", 6379));

	}

	protected Jedis getJedis() {
		return pool.getResource();
	}

	@Override
	public ObjectData getObjectData(String name) {
		return null;
	}

	@Override
	public ObjectData addObjectData(String name, StoreStrategy strategy) {
		return null;
	}

	@Override
	public LongData getLongData(String name) {
		Jedis jedis = getJedis();
		String metaDataString = jedis.hget(datakeys, name);
		if (metaDataString == null) {
			return null;
		}

		Metadata metadata = DataSerialization.deserialize(metaDataString,
				Metadata.class);

		return new LongDataImpl(metadata.getName(), pool,
				metadata.getStoreStrategy());
	}

	@Override
	public LongData addLongData(String name, StoreStrategy strategy) {
		Jedis jedis = getJedis();
		String metaDataString = jedis.hget(datakeys, name);
		Metadata metadata;
		LongData result;
		if (metaDataString == null) {
			metadata = new Metadata("LongData:" + name, strategy);
			jedis.hset(datakeys, name,
					DataSerialization.serialize(metadata, Metadata.class));

		} else {
			metadata = DataSerialization.deserialize(metaDataString,
					Metadata.class);
		}

		result = new LongDataImpl(metadata.getName(), pool,
				metadata.getStoreStrategy());
		return result;
	}

	@Override
	public ListData getListData(String name) {
		return null;
	}

	@Override
	public ListData addListData(String name, StoreStrategy strategy) {
		return null;
	}

	@Override
	public SortListData getSortListData(String name) {
		return null;
	}

	@Override
	public SortListData addSortListData(String name, StoreStrategy strategy) {
		return null;
	}

}
