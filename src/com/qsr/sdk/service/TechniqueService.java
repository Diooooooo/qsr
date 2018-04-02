package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TechniqueService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(TechniqueService.class);
    private static final String SELECT_TECHNIQUE = "SELECT t.type_name, t.type_id, st.score_a, a.team_id team_a, " +
            "b.team_id team_b, st.score_b, a.team_name team_name_a, b.team_name team_name_b " +
            "FROM qsr_team_season_technique_type t " +
            "LEFT JOIN qsr_team_season_technique st ON st.type_id = t.type_id " +
            "LEFT JOIN qsr_team a ON a.team_id = st.team_a " +
            "LEFT JOIN qsr_team b ON b.team_id = st.team_b " +
            "WHERE st.season_id = ? AND t.enabled = 1 " +
            "ORDER BY t.sorted DESC";

    public List<Map<String, Object>> getTechniques(int seasonId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_TECHNIQUE, seasonId));
        } catch (Throwable t) {
            logger.error("getTechnique was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载技术分析失败", t);
        }
    }
}
