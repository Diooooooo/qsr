package com.qsr.sdk.component.payment;

public class PaymentRequest {

	/** 支付子类型 */
	private final String paymentType;

	/** 支付金额 */
	private final int paymentFee;

	/** 客户端Ip */
	private final String clientIp;

	/** CP订单号 */
	private final String cpOrderNumber;

	/** CP定制数据 */
	private final String cpUserData;

	/** CP回调地址 */
	private final String cpNotifyUrl;

	/** 应用产品id */
	private final int appProductId;
	/** 应用推广id */
	private final int appPromotionId;
	/** 应用用户id */
	private final int appUserId;
	// private int appUserName;
	/** 购买物品id */
	private final int purchaseId;
	/** 购买物品名称 */
	private final String purchaseName;
	//
	/** 购买物品规格 */
	private final int purchanseSpec;

	/** 购买物品数量 */
	private final int pucharseCount;

	public PaymentRequest(String paymentType, int paymentFee, String clientIp,
			String cpOrderNumber, String cpUserData, String cpNotifyUrl,
			int appProductId, int appPromotionId, int appUserId,
			int purchaseId, String purchaseName, int purchanseSpec,
			int pucharseCount) {
		super();
		this.paymentType = paymentType;
		this.paymentFee = paymentFee;
		this.clientIp = clientIp;
		this.cpOrderNumber = cpOrderNumber;
		this.cpUserData = cpUserData;
		this.cpNotifyUrl = cpNotifyUrl;
		this.appProductId = appProductId;
		this.appPromotionId = appPromotionId;
		this.appUserId = appUserId;
		this.purchaseId = purchaseId;
		this.purchaseName = purchaseName;
		this.purchanseSpec = purchanseSpec;
		this.pucharseCount = pucharseCount;
	}

	public PaymentRequest(String paymentType, int paymentFee, String clientIp,
			String cpOrderNumber, String cpUserData, String cpNotifyUrl,
			int appProductId, int appPromotionId, int appUserId,
			int purchaseId, String purchaseName, int purchanseSpec) {
		super();
		this.paymentType = paymentType;
		this.paymentFee = paymentFee;
		this.clientIp = clientIp;
		this.cpOrderNumber = cpOrderNumber;
		this.cpUserData = cpUserData;
		this.cpNotifyUrl = cpNotifyUrl;
		this.appProductId = appProductId;
		this.appPromotionId = appPromotionId;
		this.appUserId = appUserId;
		this.purchaseId = purchaseId;
		this.purchaseName = purchaseName;
		this.purchanseSpec = purchanseSpec;
		this.pucharseCount = 1;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public int getPaymentFee() {
		return paymentFee;
	}

	public String getClientIp() {
		return clientIp;
	}

	public String getCpOrderNumber() {
		return cpOrderNumber;
	}

	public String getCpUserData() {
		return cpUserData;
	}

	public String getCpNotifyUrl() {
		return cpNotifyUrl;
	}

	public int getAppProductId() {
		return appProductId;
	}

	public int getAppPromotionId() {
		return appPromotionId;
	}

	public int getAppUserId() {
		return appUserId;
	}

	public int getPurchaseId() {
		return purchaseId;
	}

	public String getPurchaseName() {
		return purchaseName;
	}

	public int getPurchanseSpec() {
		return purchanseSpec;
	}

	public int getPucharseCount() {
		return pucharseCount;
	}

}
