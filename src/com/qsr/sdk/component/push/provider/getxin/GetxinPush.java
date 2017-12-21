package com.qsr.sdk.component.push.provider.getxin;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.component.push.PushProvider;
import com.qsr.sdk.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetxinPush extends AbstractComponent implements Push {

	public GetxinPush(PushProvider provider) {
		super(provider);
	}

	final static Logger logger = LoggerFactory.getLogger(GetxinPush.class);

	private static final String appId = "sVXhfHNsUh5WDsreDUSoU";
	private static final String appkey = "7Ey41ZB1s39Tl1JEj95ui4";
	private static final String master = "YuOQK5flFpAvNerxJI48f8";
	private static final String appSecret = "kIuZbeOWbY7MONCrpjV1e4";

	private static final String CID = "";
	private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";

	private static List<String> successResults = new ArrayList<>();
	static {

		successResults.add("successed_online");
		successResults.add("success_offline");
		successResults.add("ok");
	}

	public int pushSingleMessage(String userId, String message, int type)
			throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);

		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("type", type);
		messageMap.put("message", message);
		String jsonMessage = JsonUtil.toJson(messageMap);

		TransmissionTemplate template = createTransmissionTemplate(jsonMessage);
		SingleMessage singleMessage = new SingleMessage();
		singleMessage.setOffline(true);
		singleMessage.setData(template);
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(userId);

		IPushResult ret = push.pushMessageToSingle(singleMessage, target);
		Map<?, ?> resp = ret.getResponse();
		logger.debug("push response,{}", resp);

		String resultStatus = (String) resp.get("result");
		if (successResults.contains(resultStatus)) {
			return 1;
		} else {
			throw new Exception(resultStatus);
		}

	}

	private NotificationTemplate createTemplate(String title, String content) {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		// template.sett
		template.setIsRing(true);
		template.setIsClearable(true);
		template.setIsVibrate(true);
		template.setTitle(title);
		template.setText(content);

		return template;

	}

	private TransmissionTemplate createTransmissionTemplate(String content) {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionContent(content);
		template.setTransmissionType(0);
		// template.sett
		// template.setIsRing(true);
		// template.setIsClearable(true);
		// template.setIsVibrate(true);
		// template.setTitle(title);
		// template.setText(content);

		return template;

	}
	// NotificationTemplate;

}
