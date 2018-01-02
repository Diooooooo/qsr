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

}
