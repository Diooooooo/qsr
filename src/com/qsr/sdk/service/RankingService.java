package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RankingService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(RankingService.class);
    private final static String RANKING_TYPE = "";
    private static final String SELECT_RANKING_LIST = "";

    public List<Map<String, Object>> getRankingType(int leagueId) throws ServiceException {
        try {
            return record2list(Db.find(RANKING_TYPE, leagueId));
        } catch (Throwable t) {
            logger.error("getRankingType was error, exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载排行失败", t);
        }
    }

    public List<Map<String,Object>> getRankingList(int leagueId, int seasonId, int typeId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_RANKING_LIST, leagueId, seasonId, typeId));
        } catch (Throwable t) {
            logger.error("getRankingList was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "排行加载失败", t);
        }
    }
}
