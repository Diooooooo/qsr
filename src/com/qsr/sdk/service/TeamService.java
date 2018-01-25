package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TeamService extends Service {

    final static Logger logger = LoggerFactory.getLogger(TeamService.class);

    /**
     * 获取球队基本信息
     * @param teamId
     * @return
     * @throws ServiceException
     */
    public Map<String, Object> getTeamInfo(int teamId) throws ServiceException {
        try {
            String sql = "SELECT t.team_id AS teamId, t.team_name AS teamName, IFNULL(t.team_icon, \"\") AS teamIcon, "+
                    "  IFNULL(t.team_country, \"\") AS teamCountry, IFNULL(t.team_createdate, \"\") AS createDate, " +
                    "  IFNULL(t.team_city, \"\") AS teamCity, IFNULL(t.team_web, \"\") AS web, " +
                    "  IFNULL(t.team_email, \"\") AS email, IFNULL(t.team_addr, \"\") AS addr, " +
                    "  IFNULL(t.team_honor, \"\") AS honor, IFNULL(t.team_home, \"\") AS home, " +
                    "  IFNULL(t.team_best, \"\") AS best, IFNULL(t.team_price, \"\") AS price " +
                    "FROM qsr_team t WHERE t.team_id = ?";
            return record2map(Db.findFirst(sql, teamId));
        } catch (Throwable t) {
            logger.error("getTeamInfo was error. teamId={}, exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "球队加载失败", t);
        }
    }
}
