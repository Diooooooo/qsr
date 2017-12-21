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

    public List<Map<String, Object>> getSeasonListByLeagueId(String ids) throws ServiceException {
        return record2list(Db.find("", ""));
    }

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
        String sql = "SELECT ta.team_name team_a, ta.team_icon a_icon, tb.team_name team_b, " +
                "tb.team_icon b_icon, l.lea_name, DATE_FORMATE(ts.season_start_play_time,  play_time, " +
                "ts.season_gameweek gameweek, ts.season_fs_a source_a, ts.season_fs_b source_b, " +
                "tss.status_name, ts.season_id ";
        String from;
        PageList<Map<String, Object>> seasonList = null;
        try {
            switch (leagueId) {
                case -2:
                    from = "SELECT ta.team_name team_a, ta.team_icon a_icon, tb.team_name team_b, tb.team_icon b_icon, " +
                            "l.lea_name, ts.season_start_play_time play_time, ts.season_gameweek gameweek, " +
                            "ts.season_fs_a source_a, ts.season_fs_b source_b, tss.status_name, ts.season_id " +
                            "  FROM qsr_team_season ts " +
                            "  INNER JOIN qsr_users_attention ua ON ua.target_id = ts.season_id " +
                            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
                            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
                            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
                            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
                            "  WHERE ua.user_id = ? " +
                            "  AND ua.type_id = 1";
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, sql, from, userId));
                    break;
                case -1:
                    break;
                case 0:
                    from = "FROM qsr_team_season ts " +
                            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
                            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
                            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
                            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
                            "  WHERE ts.lea_id IN (1, 2, 3, 4, 5) " +
                            "  AND YEAR(ts.season_year) = YEAR(NOW()) " +
                            "  ORDER BY ts.season_year, ts.season_gameweek ";
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, sql, from));
                    break;
                default:
                    from = "FROM qsr_team_season ts " +
                            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id " +
                            "  INNER JOIN qsr_team ta ON ta.team_id = ts.season_team_a " +
                            "  INNER JOIN qsr_team tb ON tb.team_id = ts.season_team_b " +
                            "  INNER JOIN qsr_team_season_status tss ON tss.status_id = ts.status_id " +
                            "  WHERE ts.lea_id = ? " +
                            "  AND YEAR(ts.season_year) = YEAR(NOW()) " +
                            "  ORDER BY ts.season_year, ts.season_gameweek ";
                    seasonList = page2PageList(DbUtil.paginate(pageNumber, pageSize, sql, from, leagueId));
                    break;
            }
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取赛程失败", t);
        } finally {
            return seasonList;
        }
    }

    /**
     * 根据联赛ID获取联赛详情
     * @param seasonId
     * @return
     */
    public Map<String,Object> getSeasonInfo(int seasonId) throws ServiceException {
        try {

        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取赛事失败", t);
        } finally {
            return null;
        }
    }
}
