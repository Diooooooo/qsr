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

public class EventService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private static final String SELECT_EVENTS = "SELECT et.type_name, et.type_id, t.start_time, t.description, " +
            "IF(t.team_id = a.team_id, a.team_id, b.team_id) team_id, " +
            "IF(t.team_id = a.team_id, a.team_name, b.team_name) team_name, " +
            "IFNULL(ts.sports_name, t.sportsman_name_in) name_in, " +
            "IFNULL(tsb.sports_name, IFNULL(t.sportsman_name_out, '')) name_out " +
            "FROM qsr_team_season_event_type et " +
            "LEFT JOIN qsr_team_season_event t ON et.type_id = t.type_id " +
            "LEFT JOIN qsr_team_season s ON s.season_id = t.season_id " +
            "LEFT JOIN qsr_team a ON a.team_id = s.season_team_a " +
            "LEFT JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "LEFT JOIN qsr_team_sportsman ts ON ts.sports_id = t.sportsman_id_in " +
            "LEFT JOIN qsr_team_sportsman tsb ON tsb.sports_id = t.sportsman_id_out " +
            "WHERE t.season_id = ? " +
            "ORDER BY t.start_time DESC";

    public List<Map<String, Object>> getEvents(int seasonId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_EVENTS, seasonId));
        } catch (Throwable t) {
            logger.error("getEvents was error. exception = {}" , t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "事件加载失败", t);
        }
    }
}
