package com.qsr.sdk.component.msgqueue;

import com.qsr.sdk.component.Component;

/**
 * Created by Computer01 on 2016/6/22.
 */
public interface MsgQueueManager extends Component {

    MsgQueue getMsgQueue(String name);

    MsgQueue createMsgQueue(String name); // 使用配置文件中提供的默认设置创建消息队列

    MsgQueue createMsgQueue(String name, int capacity, long expire);

    void removeMsgQueue(String name);
}
