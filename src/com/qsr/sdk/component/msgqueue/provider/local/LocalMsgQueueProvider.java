package com.qsr.sdk.component.msgqueue.provider.local;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.msgqueue.MsgQueueManager;
import com.qsr.sdk.component.msgqueue.MsgQueueProvider;

import java.util.Map;

/**
 * Created by Computer01 on 2016/6/22.
 */
public class LocalMsgQueueProvider extends AbstractProvider<MsgQueueManager> implements MsgQueueProvider {

    public static final int PROVIDER_ID = 15;

    public LocalMsgQueueProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public MsgQueueManager createComponent(int configId, Map<?, ?> config) {
        return new LocalMsgQueueManager(this, config);
    }
}
