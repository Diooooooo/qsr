package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.DataService;
import com.qsr.sdk.service.LeagueService;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DataController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(DataController.class);

    public DataController() {
        super(logger);
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            SeasonService seasonService = this.getService(SeasonService.class);
            LeagueService leagueService = this.getService(LeagueService.class);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> leagues = leagueService.getFiveLeagues();
            List<Map<String, Object>> datas = dataService.getDataList();
//            PageList<Map<String, Object>> seasons = seasonService.getSeasonListByLeagueId(StringUtil.EMPTY_STRING);
            this.renderData(SUCCESS);
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

    public void getDataInfo() {
        try {
            Fetcher f = this.fetch();
            DataService dataService = this.getService(DataService.class);
            Map<String, Object> info = dataService.getDataInfo();
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getDataInfo", t);
        }
    }

    public void getRankingGroup() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getRankingGroup params={}", f);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> datas = dataService.getDataGroup();
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
            int leagueId = f.i("leagueId", 56);
            DataService dataService = this.getService(DataService.class);
            List<Map<String, Object>> datas = dataService.getSeasonItemWithSource(leagueId);
            this.renderData(datas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonItem", t);
        }
    }

}
