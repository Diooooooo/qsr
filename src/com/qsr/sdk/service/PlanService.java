package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PlanService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
    private static final String SELECT_PLAN = "SELECT p.plan_name, i.team_id, s.status_id, s.status_name, " +
            "  ts.sports_id, ts.sports_name, ts.sports_name_en, r.role_id, r.role_name FROM qsr_team_season_plan p " +
            "  INNER JOIN qsr_team_season_plan_item i ON p.plan_id = i.plan_id  " +
            "  INNER JOIN qsr_team_season_plan_item_status s ON s.status_id = i.status_id " +
            "  INNER JOIN qsr_team_sportsman ts ON ts.sports_id = i.sportsman_id " +
            "  INNER JOIN qsr_team_sportsman_role r ON r.role_id = ts.role_id " +
            "  WHERE p.season_id = ? " +
            "ORDER BY i.createdate ASC";

    public List<Map<String, Object>> getPlans(int seasonId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_PLAN, seasonId));
        } catch (Throwable t) {
            logger.error("getPlans was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载阵型失败", t);
        }
    }
}
