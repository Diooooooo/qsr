package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeasonService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(SeasonService.class);
    private final static String SELECT_SEASON_HISTOR = "SELECT ta.team_name team_a, ta.team_id as team_a_id, " +
            "tb.team_id as team_b_id, IFNULL(ta.team_icon, \"\") a_icon, " +
            "tb.team_name team_b, IFNULL(tb.team_icon, \"\") b_icon, l.lea_name, " +
            "DATE_FORMAT(ts.season_start_play_time, \"%H:%i\") play_time, IFNULL(t.type_name, '') type_name, " +
            "ts.season_gameweek gameweek, ts.season_fs_a source_a, ts.season_fs_b source_b, " +
            "tss.status_name, tss.status_id, ts.season_id, DATE_FORMAT(ts.season_start_play_time, \"%Y\") play_year," +
            "DATE_FORMAT(ts.season_start_play_time, \"%m-%d\") play_month, IF(ua.att_id IS NOT NULL, 1, 0) is_attention, " +
            "IF(ts.season_start_play_time + INTERVAL 90 MINUTE <= NOW(), 1, 0) is_over ";
    private final static String FROM_SEASON_BY_SEASON_DATE_WIth_USER = "  FROM qsr_team_season ts " +
                            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
                            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
                            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
                            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
                            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
                            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id " +
                            "  AND ua.type_id = 1 AND ua.status_id = 1 " +
                            "  WHERE ua.user_id = ? " +
                            "  AND ua.type_id = 1 " +
                            "  AND ts.season_start_play_time >= NOW() - INTERVAL 1 DAY " +
                            "  ORDER BY ua.createtime ASC";
    private final static String FROM_SEASON_BY_LEAGUE = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.lea_id IN ("+Env.getFilterLeague()+") " +
            "  AND ts.season_start_play_time >= NOW() - INTERVAL 1 DAY " +
            "  ORDER BY ts.season_start_play_time ASC, ts.season_year ASC, ts.season_gameweek ASC";
    private final static String FROM_SEASON_BY_LEAGUE_ID = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.lea_id = ? " +
            "  AND ts.season_start_play_time >= NOW() - INTERVAL 1 DAY " +
            "  ORDER BY ts.season_start_play_time ASC";
    private final static String FROM = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ?" +
            "  WHERE (ts.season_team_a = ? OR ts.season_team_b = ?) " +
            "  AND YEAR(ts.season_start_play_time) = YEAR(NOW()) " +
            "  ORDER BY ts.season_year DESC, ts.season_gameweek DESC";
    private final static String FROM_SEASON_BY_SEASON_DATE_WIth_USER_PREV = "  FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ?" +
            "  WHERE ua.type_id = 1 " +
            "  AND ts.season_start_play_time < NOW() - INTERVAL 1 DAY "+
            "  ORDER BY ua.createtime DESC, ts.season_start_play_time ASC";
    private final static String FROM_SEASON_BY_LEAGUE_PREV = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.lea_id IN ("+ Env.getFilterLeague()+") " +
            "  AND ts.season_start_play_time < NOW() - INTERVAL 1 DAY " +
            "  ORDER BY ts.season_year DESC, ts.season_start_play_time ASC, ts.season_gameweek DESC";
    private final static String FROM_SEASON_BY_LEAGUE_ID_PREV = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.lea_id = ? " +
            "  AND ts.season_start_play_time < NOW() - INTERVAL 1 DAY " +
            "  ORDER BY ts.season_start_play_time DESC";
    private final static String SEASON_INFO = "SELECT l.lea_name AS leagueName, s.season_start_play_time playTime, t.team_name AS teamA, " +
            "  s.season_team_a as teamAId, qt.team_name AS teamB, s.season_team_b AS teamBId," +
            "  IFNULL(s.season_fs_a, 0) AS scoreA, IFNULL(s.season_fs_b, 0) AS scoreB, s.season_gameweek AS gameweek, " +
            "  tss.status_name AS statusName, " +
            "  CASE " +
            "    WHEN s.season_home_team_id = s.season_team_a THEN s.season_team_b " +
            "    WHEN s.season_home_team_id = s.season_team_b THEN s.season_team_b " +
            "    ELSE 0 " +
            "  END AS home_team, s.season_id, t.team_icon a_icon, qt.team_icon b_icon, " +
            "  s.season_situation AS situation, s.season_analysis AS analysis, s.season_guess AS guess, " +
            "  s.season_odds AS odds, IFNULL(s.season_live, \"\")  AS live, s.self_chatroom_id AS self, s.chatroom_id," +
            "  IF(ua.att_id is not null, 1, 0) is_attention, IF(s.season_start_play_time + INTERVAL 90 MINUTE <= NOW(), 1, 0) is_over " +
            "FROM qsr_team_season s " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team t ON s.season_team_a = t.team_id " +
            "  INNER JOIN qsr_team qt ON qt.team_id = s.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = s.status_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = s.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "WHERE s.season_id = ?";
    private final static String TEAM_SEASON_HISTORY_WITH_VS = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "WHERE " +
//            "ts.season_start_play_time < NOW() AND " +
            "(ts.season_team_a = ? AND ts.season_team_b = ?) OR (ts.season_team_a = ? AND ts.season_team_b = ?) " +
//            "AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "ORDER BY ts.season_year DESC, ts.season_gameweek DESC LIMIT ?";
    private final static String TEAM_SEASON_HISTORY = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "WHERE ts.status_id = 4 AND (ts.season_team_a = ? OR ts.season_team_b = ?) " +
            "AND ts.season_start_play_time < NOW() " +
            "ORDER BY ts.season_year DESC, ts.season_gameweek DESC LIMIT ?";
    private static final String TEAM_SEASON_HISTORY_YEAR = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "WHERE (ts.season_team_a = ? OR ts.season_team_b = ?) " +
            "AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "ORDER BY ts.season_year DESC, ts.season_gameweek DESC";
    private static final String SELECT_SEASON_LIST_BY_LEAGUE_ID = "";
    private static final String SELECT_SEASON_FORCE = "SELECT l.lea_name, " +
            "DATE_FORMAT(s.season_start_play_time, '%m/%d %H:%i') play_time, a.team_name name_a, a.team_icon icon_a, " +
            "b.team_name name_b, b.team_icon icon_b, s.season_id, COUNT(e.esoterica_id) authority " +
            "FROM qsr_team_season_force f " +
            "  INNER JOIN qsr_team_season s ON f.season_id = s.season_id " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team a ON a.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  LEFT JOIN qsr_team_season_esoterica_item i ON i.season_id = s.season_id " +
            "  LEFT JOIN qsr_team_season_esoterica e on e.esoterica_id = i.esoterica_id " +
            "  WHERE f.enabled = 1 " +
            "GROUP BY s.season_id " +
            "ORDER BY f.createtime DESC " +
            "LIMIT 5";
    private static final String SELECT_SEASON_PLAYING_SEASON = "SELECT s.season_fid FROM qsr_team_season s " +
            "WHERE s.status_id in (1, 3, 5, 6) AND s.season_start_play_time BETWEEN NOW() - INTERVAL 90 MINUTE AND NOW() + INTERVAL 90 MINUTE";
    private static final String FROM_SEASON_ALL = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.season_start_play_time >= NOW() - INTERVAL 1 DAY  " +
            "  ORDER BY ts.season_start_play_time ASC, ts.season_gameweek ASC";
    private static final String FROM_SEASON_ALL_BY_PREV = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id  AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "  WHERE ts.season_start_play_time < NOW() - INTERVAL 1 DAY " +
            "  ORDER BY ts.season_year DESC, ts.season_start_play_time ASC, ts.season_gameweek DESC";
    private static final String SELECT_SEASON_LIST_BY_LEAGUE_ID_WITH_YEAR = "SELECT l.lea_name, a.team_name team_a, " +
            "  a.team_id team_a_id, b.team_name team_b, b.team_id team_b_id, IFNULL(a.team_icon, '') a_icon, " +
            "  IFNULL(b.team_icon, '') b_icon, t.type_name, IFNULL(st.sub_type_name, '') sub_type_name," +
            "  ss.status_name, ss.status_id, s.season_start_play_time, " +
            "  DATE_FORMAT(s.season_start_play_time, '%H:%i') play_time, " +
            "  DATE_FORMAT(s.season_start_play_time, '%m-%d') play_month, " +
            "  DATE_FORMAT(s.season_start_play_time, '%Y') play_year, " +
            "  s.season_gameweek gameweek, s.season_fs_a source_a, s.season_fs_b source_b, s.season_id " +
            "  FROM qsr_team_season s " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "  INNER JOIN qsr_league l ON s.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status ss ON s.status_id = ss.status_id " +
            "  INNER JOIN qsr_team_season_type t ON s.type_id = t.type_id " +
            "  LEFT JOIN qsr_team_season_sub_type st ON s.sub_type_id = st.sub_type_id " +
            "  WHERE s.lea_id = ? AND YEAR(s.season_year) = ? " +
            "ORDER BY s.season_start_play_time DESC";
    private static final String SELECT_SEASON_LIST_BY_GAMEWEEK = "";
    private static final String TEAM_SEASON_HISTORY_THREE = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team_season_type t ON t.type_id = ts.type_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = ts.season_id AND ua.type_id = 1 AND ua.status_id = 1 AND ua.user_id = ? " +
            "WHERE ts.status_id = 1 AND (ts.season_team_a = ? OR ts.season_team_b = ?) " +
            "AND ts.season_start_play_time > NOW() " +
            "ORDER BY ts.season_start_play_time ASC, ts.season_gameweek ASC LIMIT ?";
    private static final String SELECT_SEASON_ODDS_SEASON = "SELECT s.season_fid FROM qsr_team_season s " +
            "WHERE s.season_start_play_time BETWEEN NOW() - INTERVAL 3 DAY AND NOW() + INTERVAL 90 MINUTE ";
    private static final String SELECT_SEASON_PLAN_SEASON = "SELECT s.season_fid FROM qsr_team_season s " +
            "WHERE s.season_start_play_time > NOW() + INTERVAL 120 MINUTE ";
    private static final String FORCES = "SELECT " +
            "  f.force_id, l.lea_name, a.team_name a_name, b.team_name b_name, s.season_start_play_time play_time, " +
            "  IF(t.type_name ='联赛赛程', '', t.type_name) type_name, " +
            "  IF(s.season_gameweek = '', '', CONCAT('第', s.season_gameweek, '轮')) gameweek, f.enabled " +
            "  FROM qsr_team_season_force f " +
            "  INNER JOIN qsr_team_season s ON f.season_id = s.season_id " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "  INNER JOIN qsr_league l ON s.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team_season_type t ON s.type_id = t.type_id " +
            "  ORDER BY f.enabled DESC, f.createtime DESC";
    private static final String MODIFY_FORCE = "UPDATE qsr_team_season_force f " +
            "INNER JOIN qsr_team_season s ON f.season_id = s.season_id " +
            "  SET f.enabled = ? where f.season_id = ?";
    private static final String ADD_FORCE = "INSERT INTO qsr_team_season_force(season_id, description) " +
            "SELECT s.season_id, i.description " +
            "FROM (SELECT ? AS seasonId, ? AS description) i INNER JOIN qsr_team_season s ON i.seasonId = s.season_id";
    private static final String SEASONS_LIST = "SELECT l.lea_name, s.season_start_play_time play_time, t.type_name, " +
            "  s.season_gameweek, a.team_name a_name, b.team_name b_name, s.season_id " +
            "  FROM qsr_team_season s INNER JOIN qsr_league l ON s.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "  INNER JOIN qsr_team_season_type t ON s.type_id = t.type_id " +
            "WHERE s.season_start_play_time BETWEEN NOW() AND NOW() + INTERVAL 10 DAY " +
            "  AND s.status_id IN (1, 5, 6) " +
            "  ORDER BY s.season_start_play_time ASC";

    /**
     * 根据联赛Id获取赛程
     * @param leagueId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws ServiceException
     */
    public PageList<Map<String, Object>> getSeasonListBySeasonDateWithPage(int userId, int leagueId,
                                                                           int pageNumber, int pageSize)
            throws ServiceException {
        try {
            return getMaps(userId, leagueId, pageNumber, pageSize, false);
        } catch (Throwable t) {
            logger.error("getSeasonListBySeasonDateWithPage was error, userId={}, leagueId={}, pageNumber={}, " +
                    "pageSize={}, exception={}", userId, leagueId, pageNumber, pageSize, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    @CacheAdd(timeout = 60)
    private PageList<Map<String, Object>> getMaps(int userId, int leagueId, int pageNumber,
                                                  int pageSize, boolean isPrev) {
        PageList<Map<String, Object>> seasonList = null;
        switch (leagueId) {
            case -3:
                if (isPrev)
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR,
                            FROM_SEASON_ALL_BY_PREV, userId));
                else
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR,
                            FROM_SEASON_ALL, userId));
                break;
            case -2:
                if (isPrev)
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR,
                            FROM_SEASON_BY_SEASON_DATE_WIth_USER_PREV, userId));
                else
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                        SELECT_SEASON_HISTOR, FROM_SEASON_BY_SEASON_DATE_WIth_USER, userId));
                break;
            case -1:
                break;
            case 0:
                if (isPrev)
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR,
                            FROM_SEASON_BY_LEAGUE_PREV, userId));
                else
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                        SELECT_SEASON_HISTOR, FROM_SEASON_BY_LEAGUE, userId));
                break;
            default:
                if (isPrev)
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR,
                            FROM_SEASON_BY_LEAGUE_ID_PREV, userId, leagueId));
                else
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                        SELECT_SEASON_HISTOR, FROM_SEASON_BY_LEAGUE_ID, userId, leagueId));
                break;
        }
        return seasonList;
    }

    /**
     * 上一时间段比赛
     * @param userId
     * @param leagueId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws ServiceException
     */
    public PageList<Map<String, Object>> getSeasonListBySeasonDateWithPagePrev(int userId, int leagueId,
                                                                               int pageNumber, int pageSize)
            throws ServiceException {
        try {
            return getMaps(userId, leagueId, pageNumber, pageSize, true);
        } catch (Throwable t) {
            logger.error("getSeasonListBySeasonDateWithPagePrev was error, userId = {}, leagueId = {}, pageNumber = {}, " +
                    "pageSize = {}, exception = {}", userId, leagueId, pageNumber, pageSize, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据球队ID获取当年球队联赛信息(包含分页)
     * @param teamId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws ServiceException
     */
    public PageList<Map<String, Object>> getSeasonListByTeamId(int userId, int teamId, int pageNumber, int pageSize) throws ServiceException {
        return getSeasonListByTeamIdWithPage(pageNumber, pageSize, FROM, userId, teamId, teamId);
    }

    private PageList<Map<String, Object>> getSeasonListByTeamIdWithPage(int pageNumber, int pageSize, String where, Object... params)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR, where, params));
        } catch (Throwable t) {
            logger.error("getSeasonListByTeamIdWithPage was error. pageNumber={}, pageSize={}, exception={}",
                    pageNumber, pageSize, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据球队ID获取最近场次的比赛
     * @param teamId
     * @return
     * @throws ServiceException
     */
    public List<Map<String, Object>> getSeasonListByTeamIdWithFive(int teamId, int userId) throws ServiceException {
        try {
            return getTeamSeasonHistory(teamId, userId, 0, 5);
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据对垒双方获取最近场次的比赛
     * @param teamA
     * @param teamB
     * @return
     * @throws ServiceException
     */
    @CacheAdd(timeout = 2 * 60)
    public List<Map<String, Object>> getSeasonListByVsTeamIdWithFive(int teamA, int teamB, int userId) throws ServiceException {
        try {
            return getTeamSeasonHistory(teamA, userId, teamB, 5);
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据联赛ID获取联赛详情
     * @param seasonId
     * @return
     */
    public Map<String,Object> getSeasonInfo(int seasonId, int userId) throws ServiceException {
        try {
            Map<String, Object> info = record2map(Db.findFirst(SEASON_INFO, userId, seasonId));
            if (null == info)
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "参数不正确");
            return info;
        } catch (Throwable t) {
            logger.error("getSeasonInfo was error. seasonId={}, exception={}", seasonId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛事加载失败", t);
        }
    }

    private List<Map<String, Object>> getTeamSeasonHistory(int teamA, int userId, int teamB, int limit) throws ServiceException {
        try {
            List<Map<String, Object>> rel;
            if (0 != teamB) {
                rel = record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY_WITH_VS, userId, teamA, teamB, teamB, teamA, limit));
            } else {
                rel = record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY, userId, teamA, teamA, limit));
            }
            return rel;
        } catch (Throwable t) {
            logger.error("getTeamSeasonHistory was error. team_a = {}, team_b = {}, limit = {}, exception = {}",
                    teamA, teamB, limit, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    @CacheAdd(timeout = 2 * 60 * 60)
    public List<Map<String,Object>> getSeasonListByTeamIdWithYear(int teamId, int userId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY_YEAR, userId, teamId, teamId));
        } catch (Throwable t) {
            logger.error("getSeasonListByTeamIdWithYear was error. teamId={}, exception={}", teamId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    @CacheAdd(timeout = 1 * 60 * 60)
    public List<Map<String, Object>> getSeasonListByLeagueId(int leagueId) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_LIST_BY_LEAGUE_ID, leagueId));
        } catch (Throwable t) {
            logger.error("getSeasonListByLeagueId was error, exception = {}",t );
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "历史加载失败", t);
        }
    }

    @CacheAdd(timeout = 3 * 60)
    public List<Map<String, Object>> getSeasonListByLeagueId(int leagueId, String year) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_LIST_BY_LEAGUE_ID_WITH_YEAR, leagueId, year));
        } catch (Throwable t) {
            logger.error("getSeasonListByLeagueId was error, exception = {}",t );
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "历史加载失败", t);
        }
    }

    @CacheAdd(name = "force", timeout = 10, timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getSeasonForce() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_FORCE));
        } catch (Throwable t) {
            logger.error("getSeasonForce was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取焦点赛事失败", t);
        }
    }

    @CacheAdd(name = "playing", timeout = 2, timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getPlayingSeasons() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_PLAYING_SEASON));
        } catch (Throwable t) {
            logger.error("getPlayingSeasons was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取进行中比赛失败", t);
        }
    }

    public List<Map<String, Object>> getSeasonListByGameweek(int leagueId, int gameweek) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_LIST_BY_GAMEWEEK, leagueId, gameweek));
        } catch (Throwable t) {
            logger.error("getSeasonListByGameweek was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    @CacheAdd(timeout = 60 * 60, userKey = "futureSeason", name = "futureSeason")
    public List<Map<String,Object>> getSeasonListByTeamIdWithThree(int teamAId, int userId, int limit) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY_THREE, userId, teamAId, teamAId, limit));
        } catch (Throwable t) {
            logger.error("getSeasonListByTeamIdWithThree was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    public List<Map<String, Object>> getOddsSeasons() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_ODDS_SEASON));
        } catch (Throwable t) {
            logger.error("getOddsSeasons was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取比赛列表失败", t);
        }
    }

    public List<Map<String, Object>> getPlanSeason() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_SEASON_PLAN_SEASON));
        } catch (Throwable t) {
            logger.error("getPlanSeason was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取比赛列表失败", t);
        }
    }

    public List<Map<String,Object>> getForces() throws ServiceException {
        try {
            return record2list(Db.find(FORCES));
        } catch (Throwable t) {
            logger.error("getForces was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取焦点赛事失败", t);
        }
    }

    public void modifyForces(int seasonId, int enabled) throws ServiceException {
        try {
            Db.update(MODIFY_FORCE, enabled, seasonId);
        } catch (Throwable t) {
            logger.error("modifyForces was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "修改焦点赛事失败", t);
        }
    }

    public void addForces(int seasonId, String desc) throws ServiceException {
        try {
            Db.update(ADD_FORCE, seasonId, desc);
        } catch (Throwable t) {
            logger.error("addForces", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "保存焦点赛事失败", t);
        }
    }

    public List<Map<String,Object>> getSeasonList() throws ServiceException {
        try {
            return record2list(Db.find(SEASONS_LIST));
        } catch (Throwable t) {
            logger.error("getSeasonList was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛事列表失败", t);
        }
    }
}
