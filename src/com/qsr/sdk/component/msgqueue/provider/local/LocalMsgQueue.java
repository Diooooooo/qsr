package com.qsr.sdk.component.msgqueue.provider.local;

import com.qsr.sdk.component.msgqueue.Message;
import com.qsr.sdk.component.msgqueue.MsgQueue;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Computer01 on 2016/6/22.
 */
public class LocalMsgQueue implements MsgQueue {

    private final String name; // 队列名称

    private final ArrayBlockingQueue<Message> queue; // 队列主体，包含了对容量的设置。BlockingQueue是线程安全的。

    private long expire = -1; // 消息的过期时间。约定：设为负值时，表示消息是永久保存的。

    private int nextMsgId = 0; // 下一个消息id

    // private Timer expireManager; // 用于检测和删除过期消息的定时器。由于Timer的构造方法会启动定时器线程，因此这里不赋值给它。

    // 用于消费者监听的版本号。对队列的写操作会改变版本号，但删除过期元素除外。
    private long version = 0;

    public LocalMsgQueue(String name, int capacity, long expire) {
        this.name = name;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.expire = expire;
/*        if (expire >= 0) {
            this.expireManager = new Timer(this.name + "_expire_manager"); // 启动定时器
            this.scheduleMonitorTask();
        }*/
    }

    @Override
    public void put(String messageContent) throws ApiException {
        Message message = new Message(nextMsgId + "", messageContent);
        try {
            queue.add(message);
        } catch (IllegalStateException e) {
            this.removeExpiredElements(); // 第一次未添加成功，则删除队列中的过期元素后，再次尝试添加
            try {
                queue.add(message);
            } catch (IllegalStateException e1) {
                throw new ApiException(ErrorCode.ILLEGAL_STATE, "队列已满，无法添加新消息");
            }
        }
        nextMsgId++;
        version++;
    }

    @Override
    public Message get() {
        Message message = queue.peek();
        while (isExpired(message)) {
            queue.poll();
            message = queue.peek();
        }
        return message;
    }

    @Override
    public Message getAndRemove() {
        Message message = queue.poll();
        while (isExpired(message)) {
            message = queue.poll();
        }
        if (message != null)
            version++;
        return message;
    }

    @Override
    public Message getAndRemove(long timeout) {
        Message message = null;
        final long currentTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - currentTimeMillis < timeout) {
            message = this.getAndRemove();
            if (message != null)
                break;
        }
        return message;
    }

    @Override
    public int size() {
        this.removeExpiredElements();
        return queue.size();
    }

    @Override
    public void setExpire(long expire) {
        if (expire >= 0 && this.expire != expire) {
            this.expire = expire;
/*            expireManager.cancel();
            expireManager = new Timer(this.name + "_expire_manager"); // 启动定时器
            this.scheduleMonitorTask();*/
        }
    }

    /**
     * 安排检测和删除过期消息的定时任务。默认为30秒执行一次。
     */
/*    private void scheduleMonitorTask() {
        if (expireManager != null && expire >= 0) {
            expireManager.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (queue != null) {
                        // 此处已验证不会出现并发修改异常问题
                        queue.stream().filter(message -> System.currentTimeMillis() - message.createTimeMillis > expire).forEach(queue::remove);
                    } else {
                        // 队列为null则终止定时器线程
                        expireManager.cancel();
                        expireManager = null;
                    }
                }
            }, 0, 30 * 1000);
        }
    }*/
    public long getVersion() {
        return version;
    }

    private boolean isExpired(Message message) {
        return message != null && System.currentTimeMillis() - message.createTimeMillis > expire;
    }

    private void removeExpiredElements() {
        // 此处已验证不会出现并发修改异常问题
        queue.stream().filter(message -> System.currentTimeMillis() - message.createTimeMillis > expire).forEach(queue::remove);
    }
}
