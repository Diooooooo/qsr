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
    private static final String ESOTERICA_INFO = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE e.esoterica_id = ? AND s.status_id = 4 AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SELECT = "SELECT u.head_img_url, u.nickname, 0 AS rate_return, " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, " +
            "  e.esoterica_date create_time, '' AS detail, e.esoterica_price price, " +
            "  t.team_name team_a, b.team_name team_b, s.season_start_play_time play_time, " +
            "  s.season_fs_a fs_a, s.season_fs_b fs_b";
    private static final String ESOTERICA_SELECT_LIST = "SELECT u.head_img_url, u.nickname, 0 AS rate_return, " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, " +
            "  e.esoterica_date create_time, '' AS detail, e.esoterica_price price, " +
            "  t.team_name team_a, b.team_name team_b, s.season_start_play_time play_time, " +
            "  s.season_fs_a fs_a, s.season_fs_b fs_b";
    private static final String ESOTERICA_FROM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "WHERE e.enabled = 1 AND e.status_id != 1 AND s.status_id = 4 AND e.is_open = 1 AND e.type_id = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_FROM_WITH_LEAGUE_ID = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id " +
            "  INNER JOIN qsr_league_country c ON l.country_id = c.country_id " +
            "  WHERE c.country_id = ? AND s.status_id = 4 AND e.is_open = 1 AND e.status_id != 1 AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC ";
    private static final String ESOTERICA_USER = "  FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE e.esoterica_author = ? AND s.status_id = 4 AND e.is_open = 1 AND e.status_id != 1 AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";

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
            return record2map(Db.findFirst(ESOTERICA_SELECT + ESOTERICA_INFO, esotericaId));
        } catch (Throwable t) {
            logger.error("getEsotericaInfo was error. esotericaId = {}, exception = {}", esotericaId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String, Object>> getEsotericaHistoryWithPage(int pageNumber, int pageSize, int typeId)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST, ESOTERICA_FROM, typeId));
        } catch (Throwable t) {
            logger.error("getEsotericaHistory was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListByUserId(int pageNumber, int pageSize, int authorityId) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT, ESOTERICA_USER, authorityId));
        } catch (Throwable t) {
            logger.error("getEsotericaListByUserId was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }
}
