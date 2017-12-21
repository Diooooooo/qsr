package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;

/**
 * Created by yuan on 2016/1/24.
 */
public class ChannelService extends Service{

    ChannelService() {
    }

    @CacheAdd
    protected Integer getChannelIdByHostWithCache(String host){
        String sql="SELECT c.channel_id FROM qsr_channel c INNER JOIN qsr_channel_host h ON c.channel_id = h.channel_id WHERE c.enabled = 1 AND c.deleted = 0 AND h.host = ? ";
        Integer integer = Db.queryInt(sql, host);
        if(integer==null){
            integer=0;
        }
        return integer;
    }
    @CacheAdd
    protected Integer getChannelIdByUserWithCache(int userId){
        String sql="select channel_id from qsr_users where id=? ";
        Integer integer = Db.queryInt(sql, userId);
        if(integer==null){
            integer=0;
        }
        return integer;
    }
    public int getChannelIdByHost(String host){
        int channelId=getChannelIdByHostWithCache(host);
        return channelId;
    }
    public int getChannelIdByUser(int userId){
        int channelId = getChannelIdByUserWithCache(userId);
        return channelId;
    }
}
