package com.qsr.sdk.component.msgqueue.provider.local;

import com.qsr.sdk.exception.ApiException;

/**
 * Created by Computer01 on 2016/6/22.
 */
public interface LocalMsgConsumer {

    /**
     * 将消费者绑定到队列
     * @param queueName 绑定到的队列名称
     * @return 绑定是否成功
     */
    boolean bindMsgQueue(String queueName);

/*    {
        try {
            this.msgQueue = (LocalMsgQueue) MsgQueueHelper.getMsgQueue(queueName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    /**
     * 消费者监听队列的方法。此方法在队列中尚无可消费的消息时，会阻塞当前线程。
     */
    void listen() throws ApiException;

/*    {
        if (msgQueue == null)
            throw new ApiException(ErrorCode.ILLEGAL_STATE, "尚未绑定队列，无法监听");

        long lastVersion = msgQueue.getVersion();
        long curVersion;
        while (true) {
            curVersion = msgQueue.getVersion();
            if (curVersion != lastVersion)
                break;
            else
                lastVersion = curVersion;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
