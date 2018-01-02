package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EsotericaService extends Service {
    private final static Logger logger = LoggerFactory.getLogger(EsotericaService.class);
    private static final String ESOTERICA_INFO = "";
    private static final String ESOTERICA_SELECT = "SELECT " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, " +
            "  e.esoterica_date create_time, e.esoterica_detail detail, e.esoterica_price price, " +
            "  t.team_name team_a, b.team_name team_b, s.season_start_play_time play_time, " +
            "  s.season_fs_a fs_a, s.season_fs_b fs_b";
    private static final String ESOTERICA_FROM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "WHERE e.enabled = 1 AND e.status_id = 2 AND s.status_id = 4 AND e.is_open = 1 AND e.type_id = ?";
    private static final String ESOTERICA_FROM_WITH_LEAGUE_ID = "";

    public PageList<Map<String, Object>> getEsotericaListByLeagueId(int pageNumber, int pageSize, int leagueId)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT, ESOTERICA_FROM_WITH_LEAGUE_ID,
                    leagueId));
        } catch (Throwable t) {
            logger.error("getEsotericaListByLeagueId was error. leagueId = {}, exception = {}", leagueId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public Map<String,Object> getEsotericaInfo(int esotericaId) throws ServiceException {
        try {
            return record2map(Db.findFirst(ESOTERICA_INFO, esotericaId));
        } catch (Throwable t) {
            logger.error("getEsotericaInfo was error. esotericaId = {}, exception = {}", esotericaId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String, Object>> getEsotericaHistoryWithPage(int pageNumber, int pageSize, int typeId)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT, ESOTERICA_FROM, typeId));
        } catch (Throwable t) {
            logger.error("getEsotericaHistory was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

}
