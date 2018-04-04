package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.RankingService;
import com.qsr.sdk.service.SeasonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RankingController extends WebApiController {
    private final static Logger logger = LoggerFactory.getLogger(RankingController.class);

    public RankingController(Logger logger) {
        super(logger);
    }

    public void getSeasonList() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonList params={}", f);
            int leagueId = f.i("league_id", 56);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByLeagueId(leagueId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonList", t);
        }
    }

    public void getSeasonListWithTeam() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("seasonId");
            this.renderData();
        } catch (Throwable t) {
            this.renderException("getSeasonRanking", t);
        }
    }

    public void getRankingTypeList() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("league_id", 56);
            logger.debug("getRankingType params={}", f);
            RankingService rankingService = this.getService(RankingService.class);
            List<Map<String, Object>> rankingType = rankingService.getRankingType(leagueId);
            this.renderData(rankingType, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getRankingType", t);
        }
    }

    public void getRankingList() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getRankingList params = {}", f);
            int leagueId = f.i("league_id", 56);
            int seasonId = f.i("season_year", 1);
            int typeId = f.i("type_id", 1);
            RankingService rankingService = this.getService(RankingService.class);
            List<Map<String, Object>> rankings = rankingService.getRankingList(leagueId, seasonId, typeId);
            this.renderData(rankings, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getRankingList", t);
        }
    }
}
