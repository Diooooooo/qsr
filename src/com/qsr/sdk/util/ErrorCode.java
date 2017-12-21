package com.qsr.sdk.util;

public class ErrorCode {

	/** 参数非法 */
	public static final int PARAMER_ILLEGAL = 1;

	/** 支付类型不存在 */
	public static final int PAYMENTTYPE_NOT_EXIST = 2;

	/** 支付费用错误 */
	public static final int PAYMENTTYPE_FEE_ERROR = 3;

	/** 订单号生成错误 */
	public static final int ORDERNUMBER_GENERATE_ERROR = 4;

	/** 生成订单错误 */
	public static final int PLACEORDER_ERROR = 5;

	/** 用户已经存在 */
	public static final int USER_ALREADY_EXIST = 6;

	/** 用户不存在 */
	public static final int USER_NOT_EXIST = 7;

	/** 产品不存在(app) */
	public static final int PRODUCT_NOT_EXIST = 8;

	/** 用户创建失败 */
	public static final int USER_REGISTER_FAILED = 10;

	/** 用户登录失败 */
	public static final int USER_LOGIN_FAILED = 11;

	/** 数据校验错误 */
	public static final int DATA_VERIFYDATA_ERROR = 12;

	/** 数据格式错误 */
	public static final int DATA_FORMAT_ERROR = 13;

	/** 订单不存在 */
	public static final int ORDER_NOT_EXIST = 14;

	/** 数据保存错误 */
	public static final int DATA_SAVA_FAILED = 15;

	/** 密码错误 */
	public static final int PASSWORD_ERROR = 16;

	/** 用户名和密码不匹配 */
	public static final int USERNAME_NOT_MATCH_PASSWORD = 17;

	/** session 不存在 */
	public static final int SESSION_NOT_EXIST = 18;

	/** 没有找到指定的数据 */
	public static final int NOT_FOUND_SPEC_DATA = 19;

	/** 第三方系统异常 */
	public static final int THIRD_SERVICE_EXCEPTIOIN = 20;

	/** 系统内部错误 */
	public static final int INTERNAL_EXCEPTION = 21;

	/** 未知的错误 */
	public static final int UNKNOW_ERROR = 21;

	/** 不存在的第三方帐号 */
	public static final int NOT_EXIST_THIRDACOUNT = 23;

	/** 重复的操作 */
	public static final int REPEAT_OPERATION = 24;

	/** 非法的数据 */
	public static final int ILLEGAL_DATA = 25;

	/** 推广码不存在 */
	public static final int PROMOTION_KEY_NOT_EXIST = 26;

	/** 暂时无此功能 */
	public static final int NOT_IMPLEMENTS = 27;

	/** 验证加密错误 */
	public static final int SIGN_ERROE = 28;

	public static final int IO_EXCEPTION = 29;

	/** 非法的状态 */
	public static final int ILLEGAL_STATE = 30;

	/** 无效参数 */
	public static final int ILLEGAL_ARGUMENT = 31;

	/** 超出最大次数 */
	public static final int OUTOF_MAXTIMES = 23;

	/** 不存在的服务提供商 */
	public static final int NOT_EXIST_SERVICE_PROVIDER = 24;

	public static final int OAUTH2_ERROR = 25;

	public static final int  ILLEGAL_CHANNEL=26;

	public static final int REJECT_BY_FILTER=27;

	public static final int FILTER_ERROR=27;

	public static final int ILLEGAL_EXCEPTION = 32;

	public static final int DATE_IS_SAME = 33;

	// 从数据库（mysql或redis）中加载数据失败
	public static final int LOAD_FAILED_FROM_DATABASE = 34;

	//抵达每月提现上线
	public static final int ARRIVE_ON_THE_LINE = 35;

	/** 两次输入密码不一致 **/
    public static int CONFIRM = 36;
}
