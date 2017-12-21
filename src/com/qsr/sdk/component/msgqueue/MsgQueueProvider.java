package com.qsr.sdk.component.msgqueue;

import com.qsr.sdk.component.Provider;

/**
 * Created by Computer01 on 2016/6/22.
 */
public interface MsgQueueProvider extends Provider {

    MsgQueueManager getComponent(int configId);

    Class<MsgQueueManager> getComponentType();
}
