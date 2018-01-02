package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.LeagueService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LeagueController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(LeagueController.class);

    public LeagueController() {
        super(logger);
    }

    /**
     * 获取联赛类型
     * typeId:          1   五大联赛
     *                  2   杯赛
     *                  3   所有赛事
     */
    public void home() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getLeagues params={}", f);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int typeId = f.i("typeId", 1);
            LeagueService leagueService = this.getService(LeagueService.class);
            List<Map<String, Object>> leagues;
            if (1 == typeId) {
                leagues = leagueService.getFiveLeagues();
            } else if (2 == typeId) {
                leagues = leagueService.getAverageLeagues();
            } else {
                leagues = leagueService.getAllLeagues();
            }
            this.renderData(leagues, SUCCESS);
        } catch (Throwable e) {
            this.renderException("getLeagues", e);
        }
    }

}
