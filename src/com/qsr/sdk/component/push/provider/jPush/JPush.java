package com.qsr.sdk.component.push.provider.jPush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.push.Push;
import com.qsr.sdk.lang.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JPush extends AbstractComponent implements Push {

    private static String APP_ID;
    private static String APP_KEY;
    private static String PUSH_CHANNEL;
    private static String MASTER_SECRET;
    private static final Logger logger = LoggerFactory.getLogger(JPush.class);


    JPush(Provider provider, Map<?, ?> config) {
        super(provider);
        try {
            Parameter p = new Parameter(config);
            APP_ID = p.s("app_id");
            APP_KEY = p.s("app_key");
            MASTER_SECRET = p.s("master_secert");
            PUSH_CHANNEL = p.s("push_channel");
        } catch (Throwable t){
            throw new RuntimeException(t);
        }
    }

    @Override
    public int pushSingleMessage(String userId, String message, int type) throws Exception {
        try {
            JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
            PushPayload payload = null;
            switch (type) {
                case 1 :
                    payload = buildSignUserPayload(userId, message);
                    break;
                case 2 :
                    payload = buildALreadyLoginPayloadAllPlatform(message);
                    break;
                case 3:
                    payload = buildAlreadyLoginpayloadAndroidPlatform(message);
                    break;
                case 4:
                    payload = buildAlreadyLoginPayloadIosPlatform(message);
                    break;
                case 5:
                    payload = buildNoLoginPayloadAllPlatform(message);
                    break;
                case 6:
                    payload = buildNoLoginpayloadAndroidPlatform(message);
                    break;
                case 7:
                    payload = buildNoLoginPayloadIosPlatform(message);
                    break;
                case 8:
                    payload = buildAndroidPayloadPlatform(message);
                    break;
                case 9:
                    payload = buildIosPayloadPlatform(message);
                    break;
                default:
                    break;

            }
            PushResult pr = jpushClient.sendPush(payload);
            return pr.statusCode;
        } catch (APIConnectionException e) {
            logger.error("Connection error, should retry later", e);
            throw new RuntimeException(e);
        } catch (APIRequestException e) {
            logger.error("Should review the error, and fix the request, exception = {}", e);
            logger.info("HTTP Status: {}" + e.getStatus());
            logger.info("Error Code: {}" + e.getErrorCode());
            logger.info("Error Message: {}" + e.getErrorMessage());
            throw new RuntimeException(e);
        }
    }

    private PushPayload buildSignUserPayload(String userId, String message) {
        return PushPayload.newBuilder().setAudience(Audience.tag(userId))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildALreadyLoginPayloadAllPlatform(String message){
        return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.tag("already_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildAlreadyLoginpayloadAndroidPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.tag("already_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildAlreadyLoginPayloadIosPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.tag("already_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildNoLoginPayloadAllPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.tag("no_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildNoLoginpayloadAndroidPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.tag("no_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildNoLoginPayloadIosPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.tag("no_login"))
                .setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildAndroidPayloadPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.all()).setNotification(Notification.alert(message)).build();
    }

    private PushPayload buildIosPayloadPlatform(String message) {
        return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.all()).setNotification(Notification.alert(message)).build();
    }

}
