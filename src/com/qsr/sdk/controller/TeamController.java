package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.service.SportsManService;
import com.qsr.sdk.service.TeamService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            int userId;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            TeamService teamService = this.getService(TeamService.class);
            Map<String, Object> info = teamService.getTeamInfo(teamId);
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
            SportsManService sportsManService = this.getService(SportsManService.class);
            List<Map<String, Object>> sports = sportsManService.getSportsManByTeamId(teamId);
            this.renderData(sports, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getTeamSportsman", t);
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
            int userId;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            PageList<Map<String, Object>> seasons = seasonService.getSeasonListByTeamIdWithPage(teamId, pageNumber, pageSize);
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
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByTeamIdWithFive(teamId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamIdWithLength", t);
        }
    }

    public void getSeasonListByVsTeamIdWithLength() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonListByVsTeamIdWithLength params={}");
            int teamA = f.i("teamA");
            int teamB = f.i("teamB");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByVsTeamIdWithFive(teamA, teamB);
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
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByTeamIdWithYear(teamId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonListByTeamIdWithYear", t);
        }
    }
}
