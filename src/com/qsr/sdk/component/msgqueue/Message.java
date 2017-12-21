package com.qsr.sdk.component.msgqueue;

/**
 * Created by Computer01 on 2016/6/22.
 * 描述消息队列中的消息
 */
public class Message {

    public final String id; // 消息的id

    public final String content; // 消息内容
    
    public final long createTimeMillis = System.currentTimeMillis();

    public Message(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
