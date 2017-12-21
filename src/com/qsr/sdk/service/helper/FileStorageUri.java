package com.qsr.sdk.service.helper;

public class FileStorageUri {

	public static final String filename_idcard_photo1 = "idcard_photo1";
	public static final String filename_idcard_photo2 = "idcard_photo2";

	public static final String filename_product_apkfile = "app";

	public static final String filename_promotion_apkfile = "app";

	public static final String filename_version_apkfile = "app";

	public static final String filename_apkfile = "app";

	public static final String filename_icon1 = "icon1";
	public static final String filename_icon2 = "icon2";
	public static final String filename_icon3 = "icon3";

	public static final String filename_image1 = "image1";
	public static final String filename_image2 = "image2";
	public static final String filename_image3 = "image3";
	public static final String filename_image4 = "image4";
	public static final String filename_image5 = "image5";

	private static String getProductFileUri(int productId, String fileName) {
		return "productfiles/" + productId + "/" + fileName;
	}

	public static String getProductVersionFileUri(int productId,
			String versionName, String fileName) {
		return "productfiles/" + productId + "/" + versionName + "/" + fileName;
	}

	// public static String getProductFile
	public static String getPromotionFileUri(int productId, int promotionId,
			String fileName) {
		return "pmtfiles/" + productId + "/" + promotionId + "/" + fileName;
	}

	//	public static String getPromotionAppUri(int productId, int promotionId) {
	//		return getPromotionFileUri(productId, promotionId, filename_apkfile);
	//	}

	private static String getUserFileUri(int userId, String fileName) {
		return "userfiles/" + userId + "/" + fileName;
	}

	public static String getArticleFilePrefix() {
		return "articlefiles/";
	}

	public static String getPromotionFilePrefix(int productId) {
		return "pmtfiles/" + productId + "/";
	}

	public static String getUserIdcardPhotoUri(int userId, int index) {
		String fileName = (index == 1 ? filename_idcard_photo1
				: filename_idcard_photo2);
		return getUserFileUri(userId, fileName);

	}
}
