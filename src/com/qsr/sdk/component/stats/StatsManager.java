package com.qsr.sdk.component.stats;

import com.qsr.sdk.component.Component;

/**
 * Created by Computer01 on 2016/6/16.
 */
public interface StatsManager extends Component {

    Stats getStats(String name);
}
