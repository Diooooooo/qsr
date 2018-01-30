package com.qsr.sdk.component.im.provider;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.ApacheHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.chatroom.ChatRoomClient;
import cn.jmessage.api.chatroom.CreateChatRoomResult;
import cn.jmessage.api.common.model.RegisterInfo;
import cn.jmessage.api.common.model.chatroom.ChatRoomPayload;
import cn.jmessage.api.common.model.message.MessageBody;
import cn.jmessage.api.common.model.message.MessagePayload;
import cn.jmessage.api.message.MessageType;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.im.Im;
import com.qsr.sdk.lang.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JIm extends AbstractComponent implements Im {
    private static final Logger logger = LoggerFactory.getLogger(JIm.class);
    private static String APP_ID;
    private static String APP_KEY;
    private static String MASTER_SECERT;
    private static String CHANNEL;
    private static JMessageClient client;
    private static ChatRoomClient roomClient;

    public JIm(Provider provider, Map<?, ?> config) {
        super(provider);
        Parameter p = new Parameter(config);
        APP_ID = p.s("app_id");
        APP_KEY = p.s("app_key");
        MASTER_SECERT = p.s("master_secert");
        CHANNEL = p.s("push_channel");
        client = new JMessageClient(APP_KEY, MASTER_SECERT);
        roomClient = new ChatRoomClient(APP_KEY, MASTER_SECERT);
    }

    @Deprecated
    @Override
    public void sendSignMessage(String targetId, String fromId, String message, int type) throws RuntimeException {
        try {
            MessageBody mb = MessageBody.newBuilder().setText(message).build();
            MessagePayload mp = MessagePayload.newBuilder().setVersion(1).setTargetId(targetId)
                    .setTargetType("chatroom").setMessageBody(mb).setMessageType(MessageType.TEXT)
                    .setFromId(fromId).setFromType("admin").build();
            client.sendMessage(mp);
        } catch (APIConnectionException e) {
            logger.error("JIm was error, exception = {}", e);
            throw new RuntimeException(e);
        } catch (APIRequestException e) {
            logger.error("JIm was error, exception = {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerUser(String name, String password, String avatar, String nickname) {
        try {
            String auth = ServiceHelper.getBasicAuthorization(APP_KEY, MASTER_SECERT);
            ApacheHttpClient httpClient = new ApacheHttpClient(auth, null, ClientConfig.getInstance());
            client.setHttpClient(httpClient);
            try {
                List<RegisterInfo> users = new ArrayList<>();
                RegisterInfo user = RegisterInfo.newBuilder().setUsername(name).setPassword(password).setAvatar(avatar).setNickname(nickname).build();
                users.add(user);
                RegisterInfo[] registerInfos = new RegisterInfo[users.size()];
                client.registerUsers(users.toArray(registerInfos));
            } catch (APIConnectionException e) {
                logger.error("registerUser was error. exception = {}", e);
            } catch (APIRequestException e) {
                logger.error("registerUser was error. exception = {}", e);
            }
        } catch (Throwable t) {
            logger.error("JIm was error, exception = {}", t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public Long createChatRoom(String name, String desc, String owner) {
        try {
            ChatRoomPayload chatRoomPayload = ChatRoomPayload.newBuilder()
                    .setName(name).setDesc(desc).setOwnerUsername(owner).build();
            CreateChatRoomResult r = roomClient.createChatRoom(chatRoomPayload);
            return r.getChatroom_id();
        } catch (Throwable t) {
            logger.error("JIm was error, exception = {}", t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void addChatRoomMember(int roomId, String name) {
        try {
            roomClient.addChatRoomMember(roomId, name);
        } catch (Throwable t) {
            logger.error("JIm was error, exception = {}", t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void deleteChatRoom(Long roomId) {
        try {
            roomClient.deleteChatRoom(roomId);
        } catch (Throwable t) {
            logger.error("JIm was error, exception = {}", t);
            throw new RuntimeException(t);
        }
    }
}
