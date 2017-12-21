package com.qsr.sdk.component.datastorage.provider.redis;

import com.qsr.sdk.component.datastorage.SortItem;
import com.qsr.sdk.component.datastorage.SortListData;
import com.qsr.sdk.component.datastorage.StoreStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SortListDataImpl extends AbstractData implements SortListData {

	public SortListDataImpl(String name, JedisPool pool, StoreStrategy strategy) {
		super(name, pool, strategy);
	}

	@Override
	public void addData(Object key, SortItem data) {
		Jedis jedis = getJedis();
		try {
			Pipeline pipeline = jedis.pipelined();
			String key1 = _k(key);
			String key2 = _k(key, data.getItemId());
			pipeline.watch(key1);
			pipeline.multi();
			pipeline.zadd(key1, data.getScore(), data.getItemId());
			pipeline.set(key2, _v(data.getData()));
			pipeline.exec();
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public void addList(Object key, List<SortItem> list) {
		Jedis jedis = getJedis();
		try {
			Pipeline pipeline = jedis.pipelined();
			String key1 = _k(key);
			pipeline.multi();
			for (SortItem data : list) {
				String key2 = _k(key, data.getItemId());
				pipeline.zadd(key1, data.getScore(), data.getItemId());
				pipeline.set(key2, _v(data.getData()));
			}
			pipeline.exec();
		} finally {
			closeRedis(jedis);
		}
	}

	@Override
	public List<SortItem> getList(Object key) {
		Jedis jedis = getJedis();
		try {
			String key1 = _k(key);

			List<SortItem> result = new ArrayList<SortItem>();
			Set<Tuple> set = jedis.zrangeWithScores(key1, 0, -1);

			//pipeline.z
			for (Tuple t : set) {
				String itemId = t.getElement();
				String key2 = _k(key, itemId);
				Object value = _o(jedis.get(key2));
				SortItem item = new SortItem(itemId, value, t.getScore());
				result.add(item);
			}

			return result;
		} finally {
			closeRedis(jedis);
		}
	}

}
