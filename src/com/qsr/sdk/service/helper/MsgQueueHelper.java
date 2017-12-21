package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.msgqueue.Message;
import com.qsr.sdk.component.msgqueue.MsgQueue;
import com.qsr.sdk.component.msgqueue.MsgQueueManager;
import com.qsr.sdk.component.msgqueue.provider.local.LocalMsgQueueProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.ruleexecutor.Logger;
import com.qsr.sdk.service.ruleexecutor.LoggerFactory;
import com.qsr.sdk.util.ErrorCode;

/**
 * Created by Computer01 on 2016/6/22.
 */
public class MsgQueueHelper {

    private static final int DEFAULT_CONFIG_ID = 8;

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgQueueHelper.class);

    private static MsgQueueManager msgQueueManager
            = ComponentProviderManager.getService(MsgQueueManager.class, LocalMsgQueueProvider.PROVIDER_ID, DEFAULT_CONFIG_ID);

    private static void checkManagerStatus() throws ApiException {
        if (msgQueueManager == null)
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "不存在的消息队列服务");
    }

    public static MsgQueue getMsgQueue(String name) throws ApiException {
        checkManagerStatus();
        return msgQueueManager.getMsgQueue(name);
    }

    public static MsgQueue createMsgQueue(String name) throws ApiException {
        checkManagerStatus();
        return msgQueueManager.createMsgQueue(name);
    }

    public static MsgQueue createMsgQueue(String name, int capacity, long expire) throws ApiException {
        checkManagerStatus();
        return msgQueueManager.createMsgQueue(name, capacity, expire);
    }

    public static void removeMsgQueue(String name) throws ApiException {
        checkManagerStatus();
        msgQueueManager.removeMsgQueue(name);
    }

    public static void put(String name, String messageContent) throws ApiException {
        getMsgQueue(name).put(messageContent);
    }

    public static Message get(String name) throws ApiException {
        return getMsgQueue(name).get();
    }

    public static Message getAndRemove(String name) throws ApiException {
        return getMsgQueue(name).getAndRemove();
    }

    public static Message getAndRemove(String name, long timeout) throws ApiException {
        return getMsgQueue(name).getAndRemove(timeout);
    }

    public static int size(String name) throws ApiException {
        return getMsgQueue(name).size();
    }

    public static void setExpire(String name, long expire) throws ApiException {
        getMsgQueue(name).setExpire(expire);
    }
}
