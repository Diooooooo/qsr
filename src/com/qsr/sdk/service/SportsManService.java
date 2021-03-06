package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SportsManService extends Service {
    final static Logger logger = LoggerFactory.getLogger(SportsManService.class);
    private static final String SELECT_SPORTSMAN_INFO = "SELECT s.sports_id, s.sports_name as sportsName, s.sports_number AS sportsNumber, " +
                    "  IFNULL(s.sports_price, 0) AS sports_price, " +
                    "  sr.role_name AS location, sr.role_id, IFNULL(s.sports_country, \"\") AS sportsCountry, " +
                    "  IFNULL(s.sports_img, \"\") AS sportsImg, IFNULL(s.sports_birthday, \"\") AS sportsBirthday, " +
                    "  IFNULL(s.sports_stature, \"\") as sportsStature, IFNULL(s.sports_weight, \"\") AS sportsWeight," +
                    "  IF(ua.att_id IS NOT NULL, 1, 0) is_attention ";

    private static final String SPORTSMAN_TEAM = "  FROM qsr_team_sportsman_relation r " +
                    "  INNER JOIN qsr_team t ON t.team_id = r.team_id " +
                    "  INNER JOIN qsr_team_sportsman s ON s.sports_id = r.sports_id " +
                    "  INNER JOIN qsr_team_sportsman_role sr ON sr.role_id = r.role_id " +
                    "  LEFT JOIN qsr_users_attention ua ON ua.target_id = s.sports_id " +
                    "AND ua.type_id = 2 AND ua.status_id = 1 AND ua.user_id = ? " +
                    "  WHERE t.team_id = ?";
    private static final String SPORTSMAN_INFO = "  FROM qsr_team_sportsman_relation r " +
            "  INNER JOIN qsr_team t ON t.team_id = r.team_id " +
            "  INNER JOIN qsr_team_sportsman s ON s.sports_id = r.sports_id " +
            "  INNER JOIN qsr_team_sportsman_role sr ON sr.role_id = r.role_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = s.sports_id AND ua.type_id = 2 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE s.sports_id = ?";

    @CacheAdd(timeout = 2 * 60 * 60)
    public List<Map<String, Object>> getSportsManByTeamId(int teamId, int userId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SPORTSMAN_INFO + SPORTSMAN_TEAM, userId, teamId));
        } catch (Throwable t) {
            logger.error("getSportsManByTeamId was error. teamId={}, exception={}", teamId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "球员加载失败", t);
        }
    }

    @CacheAdd(timeout = 2 * 60 * 60)
    public Map<String,Object> getSportsmanInfo(int sportsId, int userId) throws ServiceException {
        try {
            return record2map(Db.findFirst(SELECT_SPORTSMAN_INFO + SPORTSMAN_INFO, userId, sportsId));
        } catch (Throwable t) {
            logger.error("getSportsmanInfo was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载球员信息失败", t);
        }
    }
}
