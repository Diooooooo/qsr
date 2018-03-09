package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.LeagueService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
                leagues = buildLeague(leagues);
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

    public List<Map<String, Object>> buildLeague(List<Map<String, Object>> leagues) {
        List<Map<String, Object>> n = new ArrayList<>();
        Map<String, Object> attetion = new HashMap<>();
        Map<String, Object> all = new HashMap<>();
        Map<String, Object> major = new HashMap<>();
        attetion.put("leagueName", "关注");
        attetion.put("leagueId", "-2");
        all.put("leagueName", "全部");
        all.put("leagueId", "-3");
        major.put("leagueName", "重要");
        major.put("leagueId", "0");
        n.add(attetion);
        n.add(major);
        n.add(all);
        n.addAll(leagues);
        return n;
    }

}
