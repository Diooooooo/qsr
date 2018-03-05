package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LeagueService extends Service {

    final static Logger logger = LoggerFactory.getLogger(LeagueService.class);
    final static String SELECT = "SELECT l.lea_name leagueName, l.lea_id leagueId ";
    final static String FROM_All = "FROM qsr_league l ORDER BY l.sorted DESC";
    final static String FROM_FIVE = "FROM qsr_league l WHERE l.is_average = 0 ORDER BY l.sorted DESC";
    final static String FROM_AVERAGE = "FROM qsr_league l WHERE l.is_average = 1 ORDER BY l.sorted DESC";

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getAllLeagues() throws ServiceException {
        try {
            return record2list(Db.find(SELECT + FROM_All));
        } catch (Throwable t) {
            logger.error("getAllLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getFiveLeagues() throws ServiceException {
        try {
            return record2list(Db.find(SELECT + FROM_FIVE));
        } catch (Throwable t) {
            logger.error("getFiveLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getAverageLeagues() throws ServiceException {
        try {
            return record2list(Db.find(SELECT + FROM_AVERAGE));
        } catch (Throwable t) {
            logger.error("getAverageLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }
}
