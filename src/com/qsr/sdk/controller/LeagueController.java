package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LeagueController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(LeagueController.class);

    public LeagueController() {
        super(logger);
    }

    public void getLeagues() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getLeagues params={}", f);
            String sessionkey = f.s("sessionkey", "");
            LeagueService leagueService = this.getService(LeagueService.class);
            List<Map<String, Object>> leagues = leagueService.getLeagues();
            this.renderData(leagues,"成功");
        } catch (Throwable e) {
            this.renderException("getLeagues", e);
        }
    }

}
