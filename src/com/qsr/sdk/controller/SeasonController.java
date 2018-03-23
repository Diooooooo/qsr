package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.*;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
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
//            if (seasons.isEmpty())
//                seasons = seasonService.getSeasonListBySeasonDateWithPagePrev(userId, id, pageNumber, pageSize);
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
     *              -3          全部
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
//            if (seasons.isEmpty())
//                seasons = seasonService.getSeasonListBySeasonDateWithPage(userId, id, pageNumber, pageSize);
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
//            if (0 != userId) {
//                boolean is_attention = seasonService.isAttention(seasonInfo.get("season_id"), userId, ATTENTION_TYPE[0]);
//                seasonInfo.put("is_attention", is_attention);
//            } else if (0 == userId) {
//                seasonInfo.put("is_attention", false);
//            }
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
            List<Map<String, Object>> pics = new ArrayList<>();
            if (null == forces || forces.size() <= 0) {
                for (String s: Env.getPicForce().split("-")) {
                    Map<String, Object> pic = new HashMap<>();
                    pic.put("icon", s.split(",")[0]);
                    pic.put("url", s.split(",")[1]);
                    pics.add(pic);
                }
            } else {
                Map<String, Object> pic = new HashMap<>();
                pic.put("icon", "");
                pic.put("url", "");
                pics.add(pic);
            }
            Map<String, Object> info = new HashMap<>();
            info.put("item", forces);
            info.put("pic", pics);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getForceSeason", t);
        }
    }

    public void getEntries() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("season_id", 0);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaInfoWithSeasonId(seasonId);
            logger.debug("getEntries params = {} ", f);
            List<Map<String, Object>> lm = new ArrayList<>();
            Map<String, Object> live = new HashMap<>();
            live.put("id", 1);
            live.put("n", "直播君");
            live.put("sorted", 1);
            live.put("enabled", 1);
            Map<String, Object> room = new HashMap<>();
            room.put("id", 2);
            room.put("n", "聊天室");
            room.put("sorted", 2);
            room.put("enabled", 1);
            Map<String, Object> real = new HashMap<>();
            real.put("id", 3);
            real.put("n", "赛况");
            real.put("sorted", 3);
            real.put("enabled", 1);
            Map<String, Object> plan = new HashMap<>();
            plan.put("id", 4);
            plan.put("n", "阵容");
            plan.put("sorted", 4);
            plan.put("enabled", 1);
            Map<String, Object> esoterica = new HashMap<>();
            esoterica.put("id", 5);
            esoterica.put("n", "锦囊");
            esoterica.put("sorted", 5);
            esoterica.put("enabled", 1);
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("id", 6);
            analysis.put("n", "分析");
            analysis.put("sorted", 6);
            analysis.put("enabled", 1);
            Map<String, Object> odds = new HashMap<>();
            odds.put("id", 7);
            odds.put("n", "赔率");
            odds.put("sorted", 7);
            odds.put("enabled", 1);
            lm.add(room);
            lm.add(real);
            lm.add(plan);
            if (null != info)
                lm.add(esoterica);
            lm.add(analysis);
            lm.add(odds);
            this.renderData(lm, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEntries", t);
        }
    }

}
