package com.qsr.sdk.component.msgqueue.provider.local;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.msgqueue.MsgQueue;
import com.qsr.sdk.component.msgqueue.MsgQueueManager;
import com.qsr.sdk.util.ParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/22.
 */
public class LocalMsgQueueManager extends AbstractComponent implements MsgQueueManager {

    private static final Map<String, LocalMsgQueue> LOCAL_MSG_QUEUE_MAP = new HashMap<>(); // 缓存

    public LocalMsgQueueManager(Provider provider, Map<?, ?> config) {
        super(provider, config);
    }

    @Override
    public MsgQueue getMsgQueue(String name) {
        return LOCAL_MSG_QUEUE_MAP.get(name);
    }

    @Override
    public MsgQueue createMsgQueue(String name) {
        int capacity = ParameterUtil.integerParam(config, "default_queue_capacity");
        long expire = ParameterUtil.longParam(config, "default_msg_expire_millis");
        LocalMsgQueue msgQueue = new LocalMsgQueue(name, capacity, expire);
        LOCAL_MSG_QUEUE_MAP.put(name, msgQueue);
        return msgQueue;
    }

    @Override
    public MsgQueue createMsgQueue(String name, int capacity, long expire) {
        LocalMsgQueue msgQueue = new LocalMsgQueue(name, capacity, expire);
        LOCAL_MSG_QUEUE_MAP.put(name, msgQueue);
        return msgQueue;
    }

    @Override
    public void removeMsgQueue(String name) {
        LOCAL_MSG_QUEUE_MAP.remove(name);
    }
}
