package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SeasonService extends Service {

    final static Logger logger = LoggerFactory.getLogger(SeasonService.class);
    final static String SELECT_SEASON_HISTOR = "SELECT ta.team_name team_a, IFNULL(ta.team_icon, \"\") a_icon, " +
            "tb.team_name team_b, IFNULL(tb.team_icon, \"\") b_icon, l.lea_name, " +
            "DATE_FORMAT(ts.season_start_play_time, \"%H:%m\") play_time, " +
            "ts.season_gameweek gameweek, ts.season_fs_a source_a, ts.season_fs_b source_b, "+
            "tss.status_name, ts.season_id, DATE_FORMAT(ts.season_start_play_time, \"%y\") play_year," +
            "DATE_FORMAT(ts.season_start_play_time, \"%m-%d\") play_month ";
    final static String FROM_SEASON_BY_SEASON_DATE_WIth_USER = "  FROM qsr_team_season ts " +
                            "  INNER JOIN qsr_users_attention ua ON ua.target_id = ts.season_id " +
                            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
                            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
                            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
                            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
                            "  WHERE ua.user_id = ? " +
                            "  AND ua.type_id = 1 AND tss.is_attention = 1 " +
                            "  ORDER BY ua.createtime DESC";
    final static String FROM_SEASON_BY_LEAGUE = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  WHERE ts.lea_id IN (1, 2, 3, 4, 5) " +
            "  AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "  ORDER BY ts.season_year DESC, ts.season_gameweek DESC";
    final static String FROM_SEASON_BY_LEAGUE_ID = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  WHERE ts.lea_id = ? " +
            "  AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "  ORDER BY ts.season_year DESC, ts.season_gameweek DESC";
    final static String FROM = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "  WHERE ts.season_team_a = ? OR ts.season_team_b = ? " +
            "  AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "  ORDER BY ts.season_year DESC, ts.season_gameweek DESC";
    final static String SEASON_INFO = "SELECT l.lea_name AS leagueName, s.season_start_play_time playTime, t.team_name AS teamA, " +
            "  s.season_team_a as teamAId, qt.team_name AS teamB, s.season_team_b AS teamBId," +
            "  IFNULL(s.season_fs_a, 0) AS scoreA, IFNULL(s.season_fs_b, 0) AS scoreB, s.season_gameweek AS gameweek, " +
            "  tss.status_name AS statusName, " +
            "  CASE " +
            "    WHEN s.season_home_team_id = s.season_team_a THEN s.season_team_b " +
            "    WHEN s.season_home_team_id = s.season_team_b THEN s.season_team_b " +
            "    ELSE 0 " +
            "  END AS home_team, " +
            "  s.season_situation AS situation, s.season_analysis AS analysis, s.season_guess AS guess, " +
            "  s.season_odds AS odds, IFNULL(s.season_live, \"\")  AS live " +
            "FROM qsr_team_season s " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id " +
            "  INNER JOIN qsr_team t ON s.season_team_a = t.team_id " +
            "  INNER JOIN qsr_team qt ON qt.team_id = s.season_team_b " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = s.season_id " +
            "WHERE s.season_id = ?";
    final static String TEAM_SEASON_HISTORY_WITH_VS = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "WHERE ts.season_team_a = ? AND ts.season_team_b = ? " +
            "AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "ORDER BY ts.season_year DESC, ts.season_gameweek DESC LIMIT ?";
    final static String TEAM_SEASON_HISTORY = "FROM qsr_team_season ts " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
            "  LEFT JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
            "  LEFT JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
            "WHERE ts.status_id = 4 AND ts.season_team_a = ? OR ts.season_team_b = ? " +
            "AND YEAR(ts.season_year) = YEAR(NOW()) " +
            "ORDER BY ts.season_year DESC, ts.season_gameweek DESC LIMIT ?";

    /**
     * 根据联赛Id获取赛程
     * @param leagueId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws ServiceException
     */
    public PageList<Map<String, Object>> getSeasonListBySeasonDateWithPage(
            int userId, int leagueId, int pageNumber, int pageSize)
            throws ServiceException {
        PageList<Map<String, Object>> seasonList = null;
        try {
            switch (leagueId) {
                case -2:
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                            SELECT_SEASON_HISTOR, FROM_SEASON_BY_SEASON_DATE_WIth_USER, userId));
                    break;
                case -1:
                    break;
                case 0:
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                            SELECT_SEASON_HISTOR, FROM_SEASON_BY_LEAGUE));
                    break;
                default:
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize,
                            SELECT_SEASON_HISTOR, FROM_SEASON_BY_LEAGUE_ID, leagueId));
                    break;
            }
            return seasonList;
        } catch (Throwable t) {
            logger.error("getSeasonListBySeasonDateWithPage was error, userId={}, leagueId={}, pageNumber={}, " +
                    "pageSize={}, exception={}", userId, leagueId, pageNumber, pageSize, t);
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
    public PageList<Map<String, Object>> getSeasonListByTeamIdWithPage(int teamId, int pageNumber, int pageSize)
            throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_SEASON_HISTOR, FROM, teamId, teamId));
        } catch (Throwable t) {
            logger.error("getSeasonListByTeamIdWithPage was error. teamId={}, pageNumber={}, pageSize={}, exception={}",
                    teamId, pageNumber, pageSize, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据球队ID获取最近场次的比赛
     * @param teamId
     * @return
     * @throws ServiceException
     */
    public List<Map<String, Object>> getSeasonListByTeamIdWithFive(int teamId) throws ServiceException {
        try {
            return getTeamSeasonHistory(teamId, 0, 5);
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
    public List<Map<String, Object>> getSeasonListByVsTeamIdWithFive(int teamA, int teamB) throws ServiceException {
        try {
            return getTeamSeasonHistory(teamA, teamB, 5);
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }

    /**
     * 根据联赛ID获取联赛详情
     * @param seasonId
     * @return
     */
    public Map<String,Object> getSeasonInfo(int seasonId) throws ServiceException {
        try {
            Map<String, Object> info = record2map(Db.findFirst(SEASON_INFO, seasonId));
            if (null == info)
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "参数不正确");
            return info;
        } catch (Throwable t) {
            logger.error("getSeasonInfo was error. seasonId={}, exception={}", seasonId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛事加载失败", t);
        }
    }

    private List<Map<String, Object>> getTeamSeasonHistory(int teamA, int teamB, int limit) throws ServiceException {
        try {
            List<Map<String, Object>> rel;
            if (0 != teamB) {
                rel = record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY_WITH_VS, teamA, teamB, limit));
            } else {
                rel = record2list(Db.find(SELECT_SEASON_HISTOR + TEAM_SEASON_HISTORY, teamA, teamA, limit));
            }
            return rel;
        } catch (Throwable t) {
            logger.error("getTeamSeasonHistory was error. team_a = {}, team_b = {}, limit = {}, exception = {}",
                    teamA, teamB, limit, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "赛程加载失败", t);
        }
    }
}
