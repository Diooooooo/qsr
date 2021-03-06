package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DataService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(DataService.class);
    private final static String SELECT_LIST = "";
    private final static String SELECT_INFO = "";
    private final static String FROM_LIST = "";
    private final static String FROM_INFO = "";
    private final static String DATA_GROUP = "SELECT g.group_id, g.group_name FROM qsr_team_season_lottery_group g WHERE g.enabled = 1;";
    private final static String SEASON_LIST = "SELECT CONCAT(YEAR(li.league_year) - 1, '/', SUBSTRING(DATE_FORMAT(li.league_year, '%Y'), 3)) season, YEAR(li.league_year) - 1 now_year " +
            "  FROM qsr_team_season_ranking_list_item li " +
            "  INNER JOIN qsr_league l ON l.lea_id = li.league_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team t ON t.team_id = li.team_id " +
            "  WHERE li.league_id = ? " +
            "  GROUP BY li.league_year ORDER BY li.league_year DESC";
    private final static String SEASON_LIST_ITEM = "SELECT t.team_id, t.team_name, t.team_icon, i.item_count, " +
            "  i.item_vicotry, i.item_deuce, i.item_lose, i.item_in, i.item_out, i.item_source " +
            "FROM qsr_team_season_ranking_list_item i " +
            "  INNER JOIN qsr_team t ON i.team_id = t.team_id " +
            "  INNER JOIN qsr_team_season_ranking_list_type lt ON i.type_id = lt.type_id " +
            "  INNER JOIN qsr_team_season_ranking_list_group g ON g.group_id = lt.group_id " +
            "WHERE i.league_id = ? AND g.group_id = ? AND i.type_id = 1 AND YEAR(i.league_year) = YEAR(STR_TO_DATE(?, '%Y')) " +
            "ORDER BY i.item_source DESC;";
    private final static String RANKING_TYPE = "SELECT t.type_name, t.type_id " +
            "FROM qsr_team_season_ranking_list_type t " +
            "  INNER JOIN qsr_team_season_ranking_list_item i ON t.type_id = i.type_id " +
            "WHERE i.league_id = ? AND YEAR(i.league_year) = YEAR(?) GROUP BY i.type_id";

    private final static int RANKING_GROUP_SOURCE = 1;
    private static final String IS_SCORE = "SELECT l.is_score isScore FROM qsr_league l WHERE l.lea_id = ? AND l.enabled = 1  ";

    public List<Map<String, Object>> getDataList() throws ServiceException {
        try {
            return record2list(Db.find(""));
        } catch (Throwable t) {
            logger.error("getDataList was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载数据失败", t);
        }
    }

    public List<Map<String,Object>> getSeasons(int leagueId) throws ServiceException {
        try {
            return record2list(Db.find(SEASON_LIST, leagueId));
        } catch (Throwable t) {
            logger.error("getSeasons was error, leagueId = {}, exception = {}", leagueId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    @CacheAdd(timeout = 1 * 60 * 60)
    public List<Map<String,Object>> getSeasonItemWithSource(int leagueId, String year) throws ServiceException {
        try {
            return record2list(Db.find(SEASON_LIST_ITEM, leagueId, RANKING_GROUP_SOURCE, year));
        } catch (Throwable t) {
            logger.error("getSeasonItem was error, leagueId = {}, exception = {}", leagueId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "积分榜加载失败", t);
        }
    }

    public boolean isScore(int leagueId) throws ServiceException {
        try {
            return new Parameter(record2map(Db.findFirst(IS_SCORE, leagueId))).b("isScore");
        } catch (Throwable t) {
            logger.error("isScore was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载数据失败", t);
        }
    }

    public List<Map<String, Object>> getRankingType(int league_id, int year) throws ServiceException {
        try {
            return record2list(Db.find(RANKING_TYPE, league_id, year));
        } catch (Throwable t) {
            logger.error("getRankingType was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载排行类型失败", t);
        }
    }

    public List<Map<String, Object>> getSeasonList(int leagueId, String year, int gameweek) throws ServiceException {
        try {
//            record2list(Db.find(SEASON_LIST_GAMEWEEK, leagueId, year, gameweek));
            return null;
        } catch (Throwable t) {
            logger.error("getSeasonList was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }
}
