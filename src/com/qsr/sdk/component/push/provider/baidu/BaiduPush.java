package com.qsr.sdk.component.push.provider.baidu;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaiduPush extends AbstractComponent implements Push {

	public BaiduPush(PushProvider provider) {
		super(provider);
	}

	final static Logger logger = LoggerFactory.getLogger(BaiduPush.class);

	// 1. 设置developer平台的ApiKey/SecretKey
	static String apiKey = "NsDbilpAT7c6FVGHSeKgmnkO";
	static String secretKey = "HbWtNsfxSeZG6bi6TfmP7A8v0DcdpwfC";
	static ChannelKeyPair pair;

	static {
		// 2. 创建BaiduChannelClient对象实例
		pair = new ChannelKeyPair(apiKey, secretKey);
	}

	public int pushSingleMessage(String userId, String message, int type)
			throws ApiException {

		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				// System.out.println(event.getMessage());
				logger.debug("push handle {}", event.getMessage());

			}
		});
		// 4. 创建请求类对象
		// 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
		PushUnicastMessageRequest request = new PushUnicastMessageRequest();
		// device_type => 1: web 2: pc 3:android // 2. 创建BaiduChannelClient对象实例

		request.setDeviceType(3);
		String ss[] = userId.split("@", 2);
		String channelId;
		if (ss.length == 2) {
			channelId = ss[1];
			userId = ss[0];
		} else {
			channelId = "0";
		}

		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("type", type);
		messageMap.put("message", message);
		String jsonMessage = JsonUtil.toJson(messageMap);

		Long lchannelId = Long.parseLong(channelId);
		request.setChannelId(lchannelId);
		request.setUserId(userId);

		request.setMessage(jsonMessage);

		try {
			PushUnicastMessageResponse response = channelClient
					.pushUnicastMessage(request);
			return response.getSuccessAmount();

		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			throw new ApiException(ErrorCode.INTERNAL_EXCEPTION, "推送客户端异常："
					+ e.getMessage(), e);

		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"request_id: " + e.getRequestId() + ", error_code:"
							+ e.getErrorCode() + ", error_message:"
							+ e.getErrorMsg(), e);

		}

	}

}
