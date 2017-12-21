package com.qsr.sdk.util;

/**
 * Created by fc on 2016/3/10.
 */
public class Constants {

    /** 短信类型 - 注册短信 */
    public final static String SMS_TYPE_REGISTER = "sms_register";
    /** 短信类型 - 登录短信 */
    public final static String SMS_TYPE_LOGIN = "sms_login";
    /** 短信类型 - 重置密码短信 */
    public final static String SMS_TYPE_RESET_PWD = "sms_reset_pwd";
    /** 短信类型 - 初始密码短信 */
    public final static String SMS_TYPE_INIT_PWD = "sms_init_pwd";

    /** 开放用户类型 - 手机号 */
    public final static int OPENUSER_TYPE_PHONE = 3;

    /** 证件类型 - 默认身份证 */
    public final static int IDCARD_TYPE_DEFAULT = 1;

    /** 开放用户来源类型 - 自注册 */
    public final static int OPENUSER_SOURCE_TYPE_SELF = 1;
    /** 开放用户来源类型 - 应用推广 */
    public final static int OPENUSER_SOURCE_TYPE_PROMOTION = 2;
    /** 开放用户来源类型 - 二手机 */
    public final static int OPENUSER_SOURCE_TYPE_2SHOUJI = 3;

    public final static String BIND_MSG_INFO = "信息提交成功，静待审核（一般三个工作日）即可...";

}
