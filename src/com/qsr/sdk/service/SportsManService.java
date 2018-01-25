package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SportsManService extends Service {
    final static Logger logger = LoggerFactory.getLogger(SportsManService.class);

    public List<Map<String, Object>> getSportsManByTeamId(int teamId) throws ServiceException {
        try {
            String sql = "SELECT s.sports_id, s.sports_name as sportsName, s.sports_number AS sportsNumber, IFNULL(s.sports_price, 0) AS sports_price, " +
                    "  sr.role_name AS location, IFNULL(s.sports_country, \"\") AS sportsCountry, " +
                    "  IFNULL(s.sports_img, \"\") AS sportsImg, IFNULL(s.sports_birthday, \"\") AS sportsBirthday, " +
                    "  IFNULL(s.sports_stature, \"\") as sportsStature, IFNULL(s.sports_weight, \"\") AS sportsWeight "+
                    "  FROM qsr_team_sportsman_relation r " +
                    "  INNER JOIN qsr_team t ON t.team_id = r.team_id " +
                    "  INNER JOIN qsr_team_sportsman s ON s.sports_id = r.sports_id " +
                    "  INNER JOIN qsr_team_sportsman_role sr ON sr.role_id = r.role_id " +
                    "  WHERE t.team_id = ?";
            return record2list(Db.find(sql, teamId));
        } catch (Throwable t) {
            logger.error("getSportsManByTeamId was error. teamId={}, exception={}", teamId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "球员加载失败", t);
        }
    }
}
