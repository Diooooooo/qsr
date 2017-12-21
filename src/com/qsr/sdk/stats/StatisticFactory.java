package com.qsr.sdk.stats;

import java.util.concurrent.ConcurrentHashMap;

public class StatisticFactory {

	//ConrrentHashMap<>
	static ConcurrentHashMap<Object, Statistic> moniters;

	public static Statistic getMoniter(Object key) {
		return null;
	}

	public static Statistic tryGetMoniter(Object key) {
		return null;
	}
}
