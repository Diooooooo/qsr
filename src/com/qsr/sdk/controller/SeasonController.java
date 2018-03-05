package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.*;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SeasonController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(SeasonController.class);
    private static final int[] ATTENTION_TYPE = {1, 2, 3, 4, 5};

    public SeasonController() {
        super(logger);
    }

    /**
     * 比赛页面数据填充
     * id:      -2          表示关注
     *          -1          表示彩票
     *          0           表示重要
     *           其他        表示对应联赛
     */
    public void home() {
        try {
            Fetcher f = this.fetch();
            logger.debug("season home params={}", f);
            int id = f.i("league_id", 0);
            int pageNumber = f.i("page_number", 1);
            int pageSize = f.i("page_size", 10);
            String sessionkey = f.s("sessionkey", null);
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            PageList<Map<String, Object>> seasons = seasonService.getSeasonListBySeasonDateWithPage(userId, id, pageNumber, pageSize);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("home", t);
        }
    }

    /**
     * 当前日期前的比赛
     * id           -2          关注
     *              -1          彩票
     *               0          重要
     *             其他         对应联赛
     */
    public void prev() {
        try {
            Fetcher f = this.fetch();
            logger.debug("prev params={}", f);
            int id = f.i("league_id", 0);
            int pageNumber = f.i("page_number", 1);
            int pageSize = f.i("page_size", 10);
            String sessionkey = f.s("sessionkey", null);
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)){
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            PageList<Map<String, Object>> seasons = seasonService.getSeasonListBySeasonDateWithPagePrev(userId, id, pageNumber, pageSize);
            this.renderData(seasons, SUCCESS);
        } catch (Throwable t) {
            this.renderException("prev", t);
        }
    }

    /**
     * 获取比赛详情
     */
    public void getSeasonInfo() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSeasonInfo, params={}", f);
            int seasonId = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            Map<String, Object> seasonInfo = seasonService.getSeasonInfo(seasonId, userId);
            if (0 != userId) {
                boolean is_attention = seasonService.isAttention(seasonInfo.get("season_id"), userId, ATTENTION_TYPE[0]);
                seasonInfo.put("is_attention", is_attention);
            } else if (0 == userId) {
                seasonInfo.put("is_attention", false);
            }
            this.renderData(seasonInfo, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSeasonInfo", t);
        }
    }

    /**
     * 获取事件
     */
    public void getEvent() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            EventService eventService = this.getService(EventService.class);
            List<Map<String, Object>> maps = eventService.getEvents(seasonId);
            this.renderData(maps, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEvents", t);
        }
    }

    /**
     * 技术分析
     */
    public void getTechnique() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            TechniqueService techniqueService = this.getService(TechniqueService.class);
            List<Map<String, Object>> maps = techniqueService.getTechniques(seasonId);
            this.renderData(maps, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getTechnique", t);
        }
    }

    /**
     * 阵型及阵容
     */
    public void getPlan() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            PlanService planService = this.getService(PlanService.class);
            List<Map<String, Object>> maps = planService.getPlans(seasonId);
            this.renderData(maps, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getPlan", t);
        }
    }

    /**
     * 盘口及赔率
     */
    public void getLottery() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id");
            String sessionkey = f.s("seasionkey", StringUtil.NULL_STRING);
            LotteryService lotteryService = this.getService(LotteryService.class);
            List<Map<String, Object>> maps = lotteryService.getLotteries(seasonId);
            this.renderData(maps, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getLotteries", t);
        }
    }

    /**
     * 焦点赛事
     */
    public void getForceSeason() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            logger.debug("getForceSeason params={}", f);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> forces = seasonService.getSeasonForce();
            this.renderData(forces, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getForceSeason", t);
        }
    }
}
