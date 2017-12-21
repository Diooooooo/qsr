package com.qsr.sdk.service.helper;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.stats.Stats;
import com.qsr.sdk.component.stats.StatsManager;
import com.qsr.sdk.component.stats.provider.redis.RedisStatsProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.ruleexecutor.Logger;
import com.qsr.sdk.service.ruleexecutor.LoggerFactory;
import com.qsr.sdk.util.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Computer01 on 2016/6/14.
 */
public class StatsHelper {

    private static final int DEFAULT_CONFIG_ID = 5;

    private static final Logger logger = LoggerFactory.getLogger(StatsHelper.class);

    private static StatsManager statsManager
            = ComponentProviderManager.getService(StatsManager.class, RedisStatsProvider.PROVIDER_ID, DEFAULT_CONFIG_ID);

    private static void checkManagerStatus() throws ApiException {
        if (statsManager == null)
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "不存在的数据统计服务");
    }

    public static Stats getStats(String name) throws ApiException {
        checkManagerStatus();
        return statsManager.getStats(name);
    }

    public static void addStatsData(String name, Map<String, Object> data) {
        try {
            checkManagerStatus();
            statsManager.getStats(name).addStatsData(data);
        } catch (Throwable throwable) {
            logger.error("addStatsData. exception={}, param={}, type={}", throwable, data, name);
        }

    }

    public static long getStatsDataCount(String name) throws ApiException {
        checkManagerStatus();
        return statsManager.getStats(name).getStatsDataCount();
    }

    public static List<Map<String, Object>> getStatsDataList(String name, int startIndex, int count) throws ApiException {
        checkManagerStatus();
        return statsManager.getStats(name).getStatsDataList(startIndex, count);
    }

    public static void setSizeLimit(String name, long sizeLimit) throws ApiException {
        checkManagerStatus();
        statsManager.getStats(name).setSizeLimit(sizeLimit);
    }

    public static void setExpireAt(String name, long expireAt) throws ApiException {
        checkManagerStatus();
        statsManager.getStats(name).setExpireAt(expireAt);
    }

    public static void addSellStatsData(int userId, int productId) {
        try {
            String sql = "select u.nickname,u.head_img_url from razor_user_thirdaccount u inner join (select ? as user_id) i where u.user_id=i.user_id ";
            Record p = Db.findFirst(sql, userId);
            if (p == null) {
                return;
            }

            String nickName = p.getStr("nickname");
            String headImgUrl = p.getStr("head_img_url");

            String category = "SELECT p.name, r.reward, p.category_id " +
                    "  FROM razor_sell_product p INNER JOIN razor_sell_product_category c ON p.category_id = c.category_id " +
                    "  INNER JOIN razor_sell_receipt_reward r ON r.product_id = p.product_id " +
                    "  WHERE p.product_id = ?";
            Record record = Db.findFirst(category, productId);

            int reward = record.getInt("reward");
            String productName = record.getStr("name");
            String desc = nickName + "," + productName + ",+" + reward;
            int categoryId = record.getInt("category_id");

            Map<String, Object> data = new HashMap<>();

            data.put("head_img_url", headImgUrl);
            data.put("desc", desc);

            //单独模块
            addStatsData("sell_receipt_" + String.valueOf(categoryId), data);
            //总模块
            addStatsData("sell_receipt_0", data);
        } catch (Throwable throwable) {
            logger.error("addSellStatsData, exception={}", throwable);
        }
    }
}
