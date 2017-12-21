package com.qsr.sdk.component.stats;

import com.qsr.sdk.component.Provider;

/**
 * Created by Computer01 on 2016/6/14.
 */
public interface StatsProvider extends Provider {

    StatsManager getComponent(int configId);

    Class<StatsManager> getComponentType();

}