package com.qsr.sdk.service.realtimedata;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.lang.Parameter;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ActivationData {

	public static class Data {

		public Data(long id, String desc, String head_img_url) {
			super();
			this.id = id;
			this.desc = desc;
			this.time = new Date();
			this.head_img_url = head_img_url;
		}

		final long id;
		final String desc;
		final String head_img_url;
		final Date time;

	}

	private static LinkedList<Data> readtime_datalist = new LinkedList<ActivationData.Data>();

	static ConcurrentHashMap<Integer, Long> userLastIds = new ConcurrentHashMap<>();
	private static long begin = 0;
	private static long end = 0;
	private static int max_size = 500;
	private static Date lastDate = new Date();
	private static long count = 0;

	protected static Map<String, Object> record2Map(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return m;
	}

	protected static List<Map<String, Object>> record2Map(List<Record> list) {

		List<Map<String, Object>> result = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Record r : list) {
				result.add(record2Map(r));
			}
		}
		return result;
	}

	protected static Parameter record2Parameter(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return new Parameter(m);
	}

	public static void addActivationData(int userId, int productChannelId, int reward,
			int currencyType) {
		if (userId == 0) {
			return;
		}
		String sql = "select u.nickname,u.head_img_url,chp.product_name,cy.name as currency_name from razor_user_thirdaccount u "
				+ "inner join (select ? as user_id,? as product_id,? as currency_type ) i "
				+ "inner join razor_product_channel chp on chp.product_channel_id=i.product_id "
				+ "inner join razor_user_currency_type cy on cy.currency_type_id=i.currency_type "
				+ "where u.user_id=i.user_id ";
		Parameter p = record2Parameter(Db.findFirst(sql, userId, productChannelId,
				currencyType));
		if (p == null) {
			return;
		}

		String nickName = p.s("nickname");
		String productName = p.s("product_name");
		String currencyName = p.s("currency_name");
		String headImgUrl = p.s("head_img_url");

		String desc = nickName + "," + productName + ",+" + reward;
		synchronized (readtime_datalist) {
			if (readtime_datalist.size() >= max_size) {
				readtime_datalist.removeFirst();
				begin = 0;
			}
			Date today = new Date();
			if (!DateUtils.isSameDay(lastDate, today)) {
				count = 0;
				lastDate = today;
			}
			end++;
			count++;
			readtime_datalist.add(new Data(end, desc, headImgUrl));
			if (begin == 0) {
				begin = readtime_datalist.getFirst().id;
			}
		}
	}

	public static Map<String, Object> getActivationDataList(int channelId, int userId, int size) {

		long lastId = 0;
		Long cacheLastId = userLastIds.get(userId);
		lastId = cacheLastId != null ? cacheLastId : 0;

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		List<Data> list = new ArrayList<>();
		int leftsize = 0;
		long newLastId = 0;
		synchronized (readtime_datalist) {
			int startIndex = 0;
			if (lastId > 0) {
				if (lastId < begin || lastId > end) {
					startIndex = -1 * size;
				} else {
					startIndex = (int) (lastId - begin) + 1;
				}

			} else {
				startIndex = -1 * size;
			}

			int index = startIndex >= 0 ? startIndex : readtime_datalist.size()
					+ startIndex;
			if (index < 0) {
				index = 0;
			}
			int getsize = 0;
			while (index < readtime_datalist.size() && getsize < size) {
				Data data = readtime_datalist.get(index);
				newLastId = data.id;
				list.add(data);
				index++;
				getsize++;
			}
			leftsize = readtime_datalist.size() - index;
		}
		result.put("today_count", count);
		result.put("list_leftsize", leftsize);
		result.put("list", list);
		if (newLastId > 0) {
			userLastIds.put(userId, newLastId);
		}
		return result;

	}
}
