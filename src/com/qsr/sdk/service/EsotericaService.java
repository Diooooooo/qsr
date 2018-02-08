package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EsotericaService extends Service {
    private final static Logger logger = LoggerFactory.getLogger(EsotericaService.class);
    private static final String ESOTERICA_INFO = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  WHERE e.esoterica_id = ? AND e.enabled = 1 ";
    private static final String ESOTERICA_SELECT_LIST = "SELECT IFNULL(u.head_img_url, '') head_img_url, u.nickname, 0 AS rate_return, " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, " +
            "  e.esoterica_date create_time, '' AS detail, e.esoterica_price price, " +
            "  t.team_name team_a, b.team_name team_b, s.season_start_play_time - INTERVAL 30 MINUTE play_time, " +
            "  s.season_fs_a fs_a, s.season_fs_b fs_b";
    private static final String ESOTERICA_SELECT_LIST_V2 = "SELECT IFNULL(u.head_img_url, '') head_img_url, u.nickname, u.id AS _id, " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, " +
            "  e.esoterica_price price, e.esoterica_id, e.status_id, e.esoterica_author, et.type_name, et.type_id, ts.status_id ts_id, ts.status_name ";
    private static final String ESOTERICA_FROM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND e.is_open = 1 AND e.type_id = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_ITEMS_WITH_PARAM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  LEFT JOIN qsr_team_season_esoterica_item i ON i.esoterica_id = e.esoterica_id " +
            "  WHERE e.enabled = 1 " +
            "  GROUP BY i.esoterica_id " +
            "  HAVING COUNT(i.esoterica_id) = ? " +
            "  ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_FROM_SPORTTERY = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  LEFT JOIN qsr_team_season_esoterica_item i ON i.esoterica_id = e.esoterica_id " +
            "  WHERE e.enabled = 1 AND e.type_id IN (?) " +
            "  GROUP BY i.esoterica_id " +
            "  ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_FROM_WITH_LEAGUE_ID = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id " +
            "  INNER JOIN qsr_league_country c ON l.country_id = c.country_id " +
            "  WHERE c.country_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC ";
    private static final String ESOTERICA_USER = "  FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE e.esoterica_author = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SEASON = "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE i.season_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SEASON_TYPE = "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE i.season_id = ? AND e.type_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_TOP = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  WHERE e.top = 1 " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_HISTORY = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "WHERE e.enabled = 1 AND e.status_id != 1 AND e.esoterica_author = ? AND e.esoterica_date < now() AND e.createtime < now()" +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_ITEM = "SELECT l.lea_name n, a.team_name a, b.team_name b, " +
            "  DATE_FORMAT(s.season_start_play_time, '%m-%d %H:%i') pt FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season s ON i.season_id = s.season_id " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "  INNER JOIN qsr_league l ON s.lea_id = l.lea_id " +
            "WHERE i.esoterica_id = ? ORDER BY s.season_start_play_time ASC LIMIT ? ";
    private static final int LIMIT = 10;
    private static final String ESOTERICA_STAR_CONTINUE = "SELECT " +
            "COUNT(CASE WHEN e.status_id = 2 THEN e.status_id END) star, " +
            "COUNT(CASE WHEN e.status_id = 3 THEN e.status_id END) _continue FROM qsr_team_season_esoterica e " +
            "WHERE e.esoterica_author = ? AND e.status_id IN (2, 3) " +
            "LIMIT ?";
    private static final String ESOTERICA_SPOTTERY = "SELECT s.sporttery_issue issue FROM qsr_team_season_sporttery s " +
            "WHERE SUBSTRING_INDEX(s.sporttery_issue, 0, 1) = DATE_FORMAT(NOW(), '%y') " +
            "GROUP BY s.sporttery_issue " +
            "ORDER BY s.sporttery_issue DESC";
    private static final String ESOTERICA_ISSUE = "SELECT ts.season_id, l.lea_name, " +
            "  ts.season_start_play_time start_time, a.team_name a_name, b.team_name b_name " +
            "  FROM qsr_team_season_sporttery s " +
            "  INNER JOIN qsr_team_season ts ON s.season_id = ts.season_id " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team a ON ts.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON ts.season_team_b = b.team_id " +
            "WHERE s.sporttery_issue = ? ";
    private static final String ESOTERICA_LOTTERY = "SELECT l.final_win, l.final_deuce, l.final_lose " +
            "FROM qsr_team_season_lottery l WHERE l.season_id = ? AND l.type_id = 9";

    public PageList<Map<String, Object>> getEsotericaListByLeagueId(int pageNumber, int pageSize, int leagueId)
            throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize,
                    ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM_WITH_LEAGUE_ID, leagueId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByLeagueId was error. leagueId = {}, exception = {}", leagueId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public Map<String,Object> getEsotericaInfo(int esotericaId) throws ServiceException {
        try {
            Record r = Db.findFirst(ESOTERICA_SELECT_LIST_V2 + ESOTERICA_INFO, esotericaId);
            getItemRecord(r);
            return record2map(r);
        } catch (Throwable t) {
            logger.error("getEsotericaInfo was error. esotericaId = {}, exception = {}", esotericaId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String, Object>> getEsotericaHistoryWithPage(int pageNumber, int pageSize, int typeId)
            throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM, typeId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaHistory was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListByUserId(int pageNumber, int pageSize, int authorityId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_USER, authorityId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByUserId was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListBySeasonId(int pageNumber, int pageSize, int seasonId, int typeId) throws ServiceException {
        try {
            Page<Record> pr;
            if (-1 == typeId)
                pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_SEASON, seasonId);
            else
                pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_SEASON_TYPE, seasonId, typeId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListBySeasonId was error, exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaTop(int pageNumber, int pageSize) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_TOP);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaTop was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaHistoryWithAuthorityPrev(int pageNumber, int pageSize, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_HISTORY, userId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaHistoryWithAuthorityPrev was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListByParam(int pageNumber, int pageSize, int num) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_ITEMS_WITH_PARAM, num);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByParam was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListBySporttery(int pageNumber, int pageSize, String type) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM_SPORTTERY, type);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListBySporttery was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaSportteries() throws ServiceException {
        try {
            return record2list(Db.find(ESOTERICA_SPOTTERY));
        } catch (Throwable t) {
            logger.error("getEsotericaSportteries was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载期数失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaListByIssue(String number) throws ServiceException {
        try {
            List<Map<String, Object>> ls = record2list(Db.find(ESOTERICA_ISSUE, number));
            for (Map<String, Object> r: ls) {
                Record sr = Db.findFirst(ESOTERICA_LOTTERY, r.get("season_id"));
                if (null != sr) {
                    r.put("w", sr.get("final_win"));
                    r.put("d", sr.get("final_deuce"));
                    r.put("l", sr.get("final_lose"));
                } else {
                    r.put("w", "0");
                    r.put("d", "0");
                    r.put("l", "0");
                }
            }
            return ls;
        } catch (Throwable t) {
            logger.error("getEsotericaListByIssue was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    private void getStatistics(Page<Record> pr) {
        for (Record r: pr.getList()) {
            getItemRecord(r);
            r.remove("esoterica_id");
            r.remove("_id");
            r.remove("status_id");
            r.remove("esoterica_author");
        }
    }

    private void getItemRecord(Record r) {
        List<Record> lr = Db.find(ESOTERICA_ITEM, r.get("esoterica_id"), LIMIT);
        Record scr = Db.findFirst(ESOTERICA_STAR_CONTINUE, r.get("esoterica_author"), LIMIT);
        String rate = "0";
        r.set("item", record2list(lr));
        r.set("star", scr.get("star"));
        r.set("_continue", scr.get("_continue"));
        r.set("rate", rate);
        r.set("limit", LIMIT);
    }
}
