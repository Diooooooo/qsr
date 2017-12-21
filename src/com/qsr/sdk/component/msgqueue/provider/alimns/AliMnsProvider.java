package com.qsr.sdk.component.msgqueue.provider.alimns;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.msgqueue.MsgQueueManager;
import com.qsr.sdk.component.msgqueue.MsgQueueProvider;

import java.util.Map;

/**
 * Created by Computer01 on 2016/6/23.
 */
public class AliMnsProvider extends AbstractProvider<MsgQueueManager> implements MsgQueueProvider {

    public static final int PROVIDER_ID = 16;

    public AliMnsProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public MsgQueueManager createComponent(int configId, Map<?, ?> config) {
        return new AliMnsManager(this, config);
    }
}
