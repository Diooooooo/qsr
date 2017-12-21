package com.qsr.sdk.component.msgqueue;

import com.qsr.sdk.exception.ApiException;

/**
 * Created by Computer01 on 2016/6/22.
 */
public interface MsgQueue {

    void put(String messageContent) throws ApiException; // 添加一条消息到队列尾部，如果队列已满，抛出异常。只需要提供消息的内容。

    Message get(); // 获取队列头部的消息

    Message getAndRemove(); // 获取并删除队列头部的消息。如果队列中没有消息，则立即返回null。

    Message getAndRemove(long timeout); // 获取并删除队列头部的消息。如果队列中没有消息，则在指定的超时（单位是毫秒）内等待可用消息。

    int size(); // 获取当前队列中的元素个数

    void setExpire(long expire); // 设置队列中所有消息的过期时间（先放进去的消息会先过期）。队列本身不会过期。
}
