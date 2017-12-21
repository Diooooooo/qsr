package com.qsr.sdk.component.msgqueue.provider.alimns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.model.QueueMeta;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.msgqueue.MsgQueue;
import com.qsr.sdk.component.msgqueue.MsgQueueManager;
import com.qsr.sdk.util.ParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/23.
 */
public class AliMnsManager extends AbstractComponent implements MsgQueueManager {

    private static final CloudAccount ACCOUNT;

    private static final Map<String, AliMns> ALI_MNS_MAP = new HashMap<>(); // 缓存

    private static final long MAX_EXPIRE_MILLIS = 1296000000; // 消息的最长保留时间（毫秒）

    private static final long MIN_EXPIRE_MILLIS = 60000; // 消息的最短保留时间（毫秒）

    // 消息队列的默认属性。会在Manager创建时设置一部分，在创建AliMns对象时再设置一部分。
    private final QueueMeta queueDefAttrs;

    static {
        Map<Object, Object> accountConfig = ComponentProviderManager.loadProperty("alimns_account.properties");
        ACCOUNT = new CloudAccount(
                ParameterUtil.stringParam(accountConfig, "mns.accesskeyid"),
                ParameterUtil.stringParam(accountConfig, "mns.accesskeysecret"),
                ParameterUtil.stringParam(accountConfig, "mns.accountendpoint"));
    }

    public AliMnsManager(Provider provider, Map<?, ?> config) {
        super(provider, config);

        queueDefAttrs = new QueueMeta();
        queueDefAttrs.setDelaySeconds(ParameterUtil.longParam(config, "mns.mq.delayseconds"));
        queueDefAttrs.setMaxMessageSize(ParameterUtil.longParam(config, "mns.mq.maxmessagesize"));
        queueDefAttrs.setVisibilityTimeout(ParameterUtil.longParam(config, "mns.mq.visibilitytimeout"));
        queueDefAttrs.setPollingWaitSeconds(ParameterUtil.integerParam(config, "mns.mq.pollingwaitseconds"));
        queueDefAttrs.setLoggingEnabled(ParameterUtil.booleanParam(config, "mns.mq.loggingenabled"));

        // this.loadQueueList();
    }

    // 获取所有阿里云上面的队列信息，并加载入缓存ALI_MNS_MAP
/*    private void loadQueueList() {
        String marker = null;
        do {
            PagingListResult<QueueMeta> result = ACCOUNT.getMNSClient().listQueue(null, marker, null);
            marker = result.getMarker();
            List<QueueMeta> queueMetas = result.getResult();

            for (QueueMeta queueMeta : queueMetas) {
                String name = queueMeta.getQueueName();
                Long expire = queueMeta.getMessageRetentionPeriod() * 1000;
                int capacity = ParameterUtil.integerParam(super.config, "mns.mq.defaultcapacity");

                ALI_MNS_MAP.put(name, (AliMns) this.createMsgQueue(name, capacity, expire));
            }
        } while (marker != null && !"".equals(marker));
    }*/

    @Override
    public MsgQueue getMsgQueue(String name) {
/*        if (ALI_MNS_MAP.size() == 0)
            this.loadQueueList();*/
        AliMns aliMns = ALI_MNS_MAP.get(name);
        if (aliMns == null)
            this.createMsgQueue(name);
        return ALI_MNS_MAP.get(name);
    }

    @Override
    public MsgQueue createMsgQueue(String name) {
        // int capacity = ParameterUtil.integerParam(super.config, "mns.mq.defaultcapacity");
        long expire = ParameterUtil.longParam(super.config, "mns.mq.defaultexpire");
        MsgQueue msgQueue = this.createMsgQueue(name, 0, expire);
        ALI_MNS_MAP.put(name, (AliMns) msgQueue);
        return msgQueue;
    }

    /**
     * 创建基于阿里云的消息队列。
     * @param name
     * @param capacity 此参数无效，传入任意值都是一样的效果。
     * @param expire
     * @return
     */
    @Override
    public MsgQueue createMsgQueue(String name, int capacity, long expire) {
        if (expire < MIN_EXPIRE_MILLIS)
            expire = MIN_EXPIRE_MILLIS;
        else if (expire > MAX_EXPIRE_MILLIS)
            expire = MAX_EXPIRE_MILLIS;

        queueDefAttrs.setQueueName(name);
        queueDefAttrs.setMessageRetentionPeriod(expire / 1000); // 毫秒转换成秒

        AliMns aliMns = new AliMns(ACCOUNT.getMNSClient(), queueDefAttrs);
        ALI_MNS_MAP.put(name, aliMns);
        return aliMns;
    }

    @Override
    public void removeMsgQueue(String name) {
        ALI_MNS_MAP.remove(name);
    }
}
