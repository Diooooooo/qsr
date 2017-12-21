package com.qsr.sdk.component.msgqueue.provider.alimns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.QueueMeta;
import com.qsr.sdk.component.msgqueue.Message;
import com.qsr.sdk.component.msgqueue.MsgQueue;

/**
 * Created by Computer01 on 2016/6/23.
 */
public class AliMns implements MsgQueue {

    private final String name;

    private final CloudQueue queue;

    // private long expire;

    public AliMns(MNSClient client, QueueMeta queueMeta) {
        this.name = queueMeta.getQueueName();
        this.queue = client.getQueueRef(name);

        try {
            queue.getAttributes();
        } catch (Exception e) {
            queue.create(queueMeta);
        }
        // this.expire = queueMeta.getMessageRetentionPeriod() * 1000;
    }

    @Override
    public void put(String messageContent) {
        com.aliyun.mns.model.Message message = new com.aliyun.mns.model.Message();
        // 为了照顾通用性，消息体采用UTF-8编码。阿里消息服务默认的编码是Base64。
        message.setMessageBody(messageContent, com.aliyun.mns.model.Message.MessageBodyType.RAW_STRING);
        queue.putMessage(message);
    }

    @Override
    public Message get() {
        com.aliyun.mns.model.Message aliMessage = queue.peekMessage();
        Message message = null;
        if (aliMessage != null) {
            // 消息体以UTF-8解码
            // 阿里消息服务的消息id格式：79BF6C9C4E4393F2-1-1557C22D6DA-200000001
            message = new Message(aliMessage.getMessageId(), aliMessage.getMessageBodyAsRawString());
        }
        return message;
    }

    @Override
    public Message getAndRemove() {
        com.aliyun.mns.model.Message aliMessage = queue.popMessage();
        Message message = null;
        if (aliMessage != null) {
            message = new Message(aliMessage.getMessageId(), aliMessage.getMessageBodyAsRawString());
            queue.deleteMessage(aliMessage.getReceiptHandle());
        }
        return message;
    }

    @Override
    public Message getAndRemove(long timeout) {
        com.aliyun.mns.model.Message aliMessage = queue.popMessage((int) (timeout / 1000));
        Message message = null;
        if (aliMessage != null) {
            message = new Message(aliMessage.getMessageId(), aliMessage.getMessageBodyAsRawString());
            queue.deleteMessage(aliMessage.getReceiptHandle());
        }
        return message;
    }

    @Override
    public int size() {
        return queue.getAttributes().getActiveMessages().intValue();
    }

    @Override
    public void setExpire(long expire) {
        QueueMeta attributes = queue.getAttributes();
        attributes.setMessageRetentionPeriod(expire / 1000);
        queue.setAttributes(attributes);
    }
}
