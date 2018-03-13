package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.service.SportsManService;
import com.qsr.sdk.service.TeamService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamController extends WebApiController {
    final static Logger logger = LoggerFactory.getLogger(TeamController.class);
    public TeamController() {
        super(logger);
    }

    public void getTeamInfo(){
        try {
            Fetcher f = this.fetch();
            logger.debug("getTeamInfo, params={}", f);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int teamId = f.i("teamId");
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            TeamService teamService = this.getService(TeamService.class);
            Map<String, Object> info = teamService.getTeamInfo(teamId, userId);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getTeamInfo", t);
        }
    }

    public void getTeamSportsman() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getTeamSportsman, params={}", f);
            int teamId = f.i("teamId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SportsManService sportsManService = this.getService(SportsManService.class);
            List<Map<String, Object>> sports = sportsManService.getSportsManByTeamId(teamId, userId);
            this.renderData(sports, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getTeamSportsman", t);
        }
    }

    public void getSportsmanInfo() {
        try {
            Fetcher f = this.fetch();
            int sportsId = f.i("sportsId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            SportsManService sportsManService = this.getService(SportsManService.class);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            Map<String, Object> info = sportsManService.getSportsmanInfo(sportsId, userId);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSportsmanInfo", t);
        }
    }

    public void getSeasonListByTeamId(){
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonListByTeamIdWithPage params={}", f);
            int teamId = f.i("teamId");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 5);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            PageList<Map<String, Object>> seasons = seasonService.getSeasonListByTeamId(userId, teamId, pageNumber, pageSize);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamIdWithPage", t);
        }
    }

    public void getSeasonListByTeamIdWithLength() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonListByTeamIdWidthLength, params={}", f);
            int teamId = f.i("teamId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            UserService userService = this.getService(UserService.class);
            int userId = 0;
            if (null != sessionkey)
                userId = userService.getUserIdBySessionKey(sessionkey);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByTeamIdWithFive(teamId, userId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamIdWithLength", t);
        }
    }

    public void getTeamSeasonListByTeamId() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("seassionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            Parameter p = new Parameter(seasonService.getSeasonInfo(seasonId, userId));
            List<Map<String, Object>> a = seasonService.getSeasonListByTeamIdWithFive(p.i("teamAId"), userId);
            List<Map<String, Object>> b = seasonService.getSeasonListByTeamIdWithFive(p.i("teamBId"), userId);
            Map<String, Object> info = new HashMap<>();
            info.put("a", a);
            info.put("b", b);
            this.renderData(info);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamId", t);
        }
    }

    public void getFutureTeamSeasonListByTeamId() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            Parameter p = new Parameter(seasonService.getSeasonInfo(seasonId, userId));
            List<Map<String, Object>> a = seasonService.getSeasonListByTeamIdWithThree(p.i("teamAId"), userId, 3);
            List<Map<String, Object>> b = seasonService.getSeasonListByTeamIdWithThree(p.i("teamBId"), userId, 3);
            Map<String, Object> info = new HashMap<>();
            info.put("a", a);
            info.put("b", b);
            this.renderData(info);
        } catch (Throwable t) {
            this.renderException("getFutureTeamSeasonListByTeamId", t);
        }
    }

    public void getSeasonListByVsTeamIdWithLength() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonListByVsTeamIdWithLength params={}");
            int season_id = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            UserService userService = this.getService(UserService.class);
            int userId = 0;
            if (null != sessionkey)
                userId = userService.getUserIdBySessionKey(sessionkey);
            SeasonService seasonService = this.getService(SeasonService.class);
            Parameter p = new Parameter(seasonService.getSeasonInfo(season_id, userId));
            List<Map<String, Object>> seasons = seasonService.getSeasonListByVsTeamIdWithFive(p.i("teamAId"), p.i("teamBId"), userId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByVsTeamLength", t);
        }
    }

    public void getSeasonListByTeamIdWithYear() {
        try {
            Fetcher f = this.fetch();
            int teamId = f.i("teamId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            UserService userService = this.getService(UserService.class);
            int userId = 0;
            if (null != sessionkey)
                userId = userService.getUserIdBySessionKey(sessionkey);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByTeamIdWithYear(teamId, userId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamIdWithYear", t);
        }
    }
}
