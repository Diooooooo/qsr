package com.qsr.sdk.component.thirdaccount.provider.weixin;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.thirdaccount.Account;
import com.qsr.sdk.component.thirdaccount.AccountInfo;
import com.qsr.sdk.component.thirdaccount.AccountProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.JsonUtil;
import com.qsr.sdk.util.MapUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WeiXinAccount extends AbstractComponent implements Account {

	int configId;

	public WeiXinAccount(AccountProvider provider, int configId) {
		super(provider);
		this.configId = configId;
	}

	public static final String domain = "wechat";
	public static final int PROVIDER_ID = 1;

	static final Pattern emoji = Pattern
			.compile(
					"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
					Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
	static final String empty = "";

	@Override
	public AccountInfo getAccountInfo(String userId, String token)
			throws Exception {

		// https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
		try {
			String url = "http://api.weixin.qq.com/sns/userinfo";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("access_token", token);
			parameters.put("openid", userId);

			String response = HttpUtil.get(url, parameters);
			Map<String, String> map = MapUtil.convertMap(JsonUtil
					.fromJsonToMap(response));
			String newUserId = map.get("unionid");
			if (newUserId == null) {
				throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
						"获取第三方帐号信息异常");
			}

			String nickName = map.get("nickname");
			if (nickName != null) {
				nickName = emoji.matcher(nickName).replaceAll(empty);
			}
			String headImgUrl = map.get("headimgurl");

			return new AccountInfo(PROVIDER_ID, domain, newUserId, nickName,
					headImgUrl);
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"获取第三方帐号信息异常", e);
		}

	}

	@Override
	public int getConfigId() {
		return configId;
	}

}
