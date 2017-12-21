package com.qsr.sdk.component.stats;

import java.util.List;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/14.
 */
public interface Stats {

    void addStatsData(Map<String, Object> data);

    long getStatsDataCount();

    List<Map<String, Object>> getStatsDataList(long startIndex, int count);

    void setSizeLimit(long sizeLimit);

    void setExpireAt(long expireAt);
}
