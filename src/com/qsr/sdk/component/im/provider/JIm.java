package com.qsr.sdk.component.im.provider;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.common.model.message.MessageBody;
import cn.jmessage.api.common.model.message.MessagePayload;
import cn.jmessage.api.message.MessageType;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.im.Im;
import com.qsr.sdk.lang.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JIm extends AbstractComponent implements Im {
    private static final Logger logger = LoggerFactory.getLogger(JIm.class);
    private static String APP_ID;
    private static String APP_KEY;
    private static String MASTER_SECERT;
    private static String CHANNEL;
    private static JMessageClient client;

    public JIm(Provider provider, Map<?, ?> config) {
        super(provider);
        Parameter p = new Parameter(config);
        APP_ID = p.s("app_id");
        APP_KEY = p.s("app_key");
        MASTER_SECERT = p.s("master_secert");
        CHANNEL = p.s("push_channel");
        client = new JMessageClient(APP_KEY, MASTER_SECERT);
    }

    @Override
    public void sendSignMessage(String targetId, String fromId, String message, int type) throws RuntimeException {
        try {
            MessageBody mb = MessageBody.newBuilder().setText(message).build();
            MessagePayload mp = MessagePayload.newBuilder().setVersion(1).setTargetId(targetId).setTargetType(targetId).setMessageBody(mb).setMessageType(MessageType.TEXT).setFromId(fromId).setFromType(fromId).build();
            client.sendMessage(mp);
        } catch (APIConnectionException e) {
            logger.error("JIm was error, exception = {}", e);
            throw new RuntimeException(e);
        } catch (APIRequestException e) {
            logger.error("JIm was error, exception = {}", e);
            throw new RuntimeException(e);
        }
    }
}
