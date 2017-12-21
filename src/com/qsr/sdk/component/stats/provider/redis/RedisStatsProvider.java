package com.qsr.sdk.component.stats.provider.redis;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.stats.StatsManager;
import com.qsr.sdk.component.stats.StatsProvider;

import java.util.Map;

/**
 * Created by Computer01 on 2016/6/14.
 */
public class RedisStatsProvider extends AbstractProvider<StatsManager> implements StatsProvider {

    public static final int PROVIDER_ID = 12;

    public RedisStatsProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public RedisStatsManager createComponent(int configId, Map<?, ?> config) {
        return new RedisStatsManager(this, config);
    }
}
