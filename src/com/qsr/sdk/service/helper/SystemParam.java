package com.qsr.sdk.service.helper;


public class SystemParam {

	//static Map<String, String> map = null;

	//	static final String cacheKey = "com.e9w.skywalker.sdk.tool.SystemParam";
	//	static final long timeout = 5 * 60;
	//
	//	private static synchronized Map<String, String> getParams() {
	//
	//		Map<String, String> params = CacheUtil.get(cacheKey);
	//		if (params == null) {
	//			params = new HashMap<String, String>();
	//			String sql = "select param_key,param_value from razor_system_param where deleted=0 and enabled=1 ";
	//			List<Record> list = Db.find(sql);
	//
	//			for (Record record : list) {
	//				params.put(record.getStr("param_key"),
	//						record.getStr("param_value"));
	//			}
	//
	//			CacheUtil.put(cacheKey, params, Env.getCacheTimeout());
	//		}
	//		return params;
	//
	//	}
	//
	//	public static String getWeiXinPageUrl() {
	//		Map<String, String> params = getParams();
	//		return ParameterUtil.stringParam(params, "apk_wxdownload_url", null);
	//	}
	//
	//	public static long getIosDownloadTimeOut() {
	//		Map<String, String> params = getParams();
	//		return ParameterUtil.longParam(params, "ios_download_timeout", 20);
	//	}
	//
	//	public static String getDownloadUrl() {
	//		Map<String, String> params = getParams();
	//		return ParameterUtil.stringParam(params, "apk_wxdownload_url",
	//				"http://19w.me/");
	//	}

}
