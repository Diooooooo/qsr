package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.DataService;
import com.qsr.sdk.service.LeagueService;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.service.realtimedata.ActivationData;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class DataController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final String[] SEASONS = {"日期", "时间", "主队", "比分", "客队"};
    private static final String[] SCORE = {"排名", "球队", "场次", "胜", "平", "负", "进/失球", "积分"};

    public DataController() {
        super(logger);
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("league_id");
            SeasonService seasonService = this.getService(SeasonService.class);
            LeagueService leagueService = this.getService(LeagueService.class);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonListByLeagueId(leagueId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("home", t);
        }
    }

    public void getDataList() {
        try {
            Fetcher f = this.fetch();
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> dataList = dataService.getDataList();
            this.renderData(dataList, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getDataList", t);
        }
    }

    public void getRankingType() {
        try {
            Fetcher f = this.fetch();
            int league_id = f.i("league_id");
            int year = f.i("year", 0);
            year = 0 == year ? LocalDate.now().getYear() : year;
            logger.debug("getRankingGroup params={}", f);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> datas = dataService.getRankingType(league_id, year);
            this.renderData(datas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getRankingGroup", t);
        }
    }

    public void getSeasonLotteryType() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonLotteryType params={}", f);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonLotteryType", t);
        }
    }

    public void getSeasonList() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("league_id");
            String year = f.s("year", String.valueOf(LocalDate.now().getYear()));
            int gameweek = f.i("gameweek", 1);
            DataService dataService = this.getService(DataService.class);
            this.renderData(dataService.getSeasonList(leagueId, year, gameweek));
        } catch (Throwable t) {
            this.renderException("getSeasonList", t);
        }
    }

    public void getGameweek() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("league_id");
            String year = f.s("year", String.valueOf(LocalDate.now().getYear()));
            SeasonService seasonService = this.getService(SeasonService.class);
        } catch (Throwable t) {
            this.renderException("getGamewwek", t);
        }
    }

    public void getSeasons() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasons params={}", f);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int leagueId = f.i("leagueId", 56);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> seasons = dataService.getSeasons(leagueId);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasons", t);
        }
    }

    public void getSeasonItem() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonItem params = {}", f);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int leagueId = f.i("leagueId", 56);
            String year = f.s("year", String.valueOf(LocalDate.now().getYear()));
            DataService dataService = this.getService(DataService.class);
            LeagueService leagueService = this.getService(LeagueService.class);
            Map<String, Object> info = leagueService.getLeagueInfo(leagueId);
            boolean isScore = dataService.isScore(leagueId);
            Map<String, Object> data = new HashMap<>();
            data.put("is_score", isScore);
            data.put("desc_", info.get("desc_"));
            if (isScore) {
                List<Map<String, Object>> datas = dataService.getSeasonItemWithSource(leagueId, year);
                data.put("header", headerWithScore());
                data.put("item", datas);
            } else {
                SeasonService seasonService = this.getService(SeasonService.class);
                List<Map<String, Object>> datas = seasonService.getSeasonListByLeagueId(leagueId, year);
                data.put("header", headerWithSeason());
                data.put("item", datas);
            }
            this.renderData(data, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonItem", t);
        }
    }

    private List<String> headerWithSeason() {
        List<String> headers = new ArrayList<>();
        for (String s: Arrays.asList(SEASONS)) {
            headers.add(s);
        }
        return headers;
    }

    private List<String> headerWithScore() {
        List<String> headers = new ArrayList<>();
        for (String s: Arrays.asList(SCORE)) {
            headers.add(s);
        }
        return headers;
    }

}
