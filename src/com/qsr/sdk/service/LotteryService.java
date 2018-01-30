package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LotteryService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private static final String SELECT_LOTTERIES = "SELECT g.group_id, g.group_name, t.type_name, l.lottery_win, " +
            "  l.lottery_deuce, l.lottery_lose, l.final_win, l.final_deuce, l.final_lose " +
            "  FROM qsr_team_season_lottery_type t " +
            "  INNER JOIN qsr_team_season_lottery_group g ON g.group_id = t.group_id " +
            "  LEFT JOIN qsr_team_season_lottery l ON t.type_id = l.type_id " +
            "  LEFT JOIN qsr_team_season s ON s.season_id = l.season_id " +
            "  WHERE l.season_id = ? " +
            "ORDER BY g.group_id";

    public List<Map<String, Object>> getLotteries(int seasonId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_LOTTERIES, seasonId));
        } catch (Throwable t) {
            logger.error("getLotteries was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赔率加载失败", t);
        }
    }
}
