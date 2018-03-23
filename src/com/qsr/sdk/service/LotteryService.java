package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LotteryService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private static final String SELECT_LOTTERIES = "SELECT g.group_id, g.group_name, t.type_name, l.lottery_win, l.lottery_id, " +
            "  l.lottery_deuce, l.lottery_lose, l.final_win, l.final_deuce, l.final_lose, DATE_FORMAT(l.updatetime, '%m-%d %H:%i') AS change_time " +
            "  FROM qsr_team_season_lottery_type t " +
            "  INNER JOIN qsr_team_season_lottery_group g ON g.group_id = t.group_id " +
            "  LEFT JOIN qsr_team_season_lottery l ON t.type_id = l.type_id " +
            "  LEFT JOIN qsr_team_season s ON s.season_id = l.season_id " +
            "  WHERE l.season_id = ? " +
            "ORDER BY t.sorted ASC, g.group_id ASC";

    private static final String SELECT_LOTTERY_ITEMS = "SELECT i.item_win, i.item_deuce, i.item_lose, " +
            "i.item_win_status, i.item_lose_status, DATE_FORMAT(i.createtime, '%m-%d %H:%i') change_time " +
            "FROM qsr_team_season_lottery_item i WHERE i.lottery_id = ? ORDER BY i.createtime ASC ";

    @CacheAdd(name = "lottery", timeout = 1, timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getLotteries(int seasonId) throws ServiceException {
        try {
            List<Map<String, Object>> lotteries = record2list(Db.find(SELECT_LOTTERIES, seasonId));
            for (Map<String, Object> m: lotteries) {
                List<Map<String, Object>> items = record2list(Db.find(SELECT_LOTTERY_ITEMS, m.get("lottery_id")));
                m.put("item_list", null == items ? new ArrayList<>() : items);
            }
            return lotteries;
        } catch (Throwable t) {
            logger.error("getLotteries was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赔率加载失败", t);
        }
    }
}
