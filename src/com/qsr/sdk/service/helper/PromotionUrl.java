package com.qsr.sdk.service.helper;


import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.Radix62;

public class PromotionUrl {

	public static String getPromotionUrl(int promotionId) {
		Radix62 radix62 = new Radix62(promotionId);
		return Env.getDownloadUrl() + radix62.toString();
	}

	public static int getIdByRadix62(String param) {
		Radix62 number = Radix62.parseRadix62(param);
		return number.intValue();
	}

	public static String getIosPromotionUrl(int promotionId) {
		Radix62 radix62 = new Radix62(promotionId);
		return Env.getDownloadUrl() + "8/" + radix62.toString();
	}

	public static String getProductUrl(int promotionId) {
		Radix62 radix62 = new Radix62(promotionId);
		return Env.getDownloadUrl() + "p/" + radix62.toString();
	}

	public static String getH5Url(int promotionId) {
		Radix62 radix62 = new Radix62(promotionId);
		return Env.getDownloadUrl() + "h/" + radix62.toString();
	}

	public static String getPmtProductListPageUrl(int promotionId) {
		//Radix62 radix62 = new Radix62(promotionId);
		//Radix62 productR62 = new Radix62(product);
		return Env.getDownloadUrl() + "9/" + promotionId;
	}

}
