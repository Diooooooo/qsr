package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SeasonController extends WebApiController {

    final static Logger logger = LoggerFactory.getLogger(SeasonController.class);

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
            String sessionkey = f.s("sessionkey", StringUtil.EMPTY_STRING);
            int userId = 0;
            if (!StringUtil.isEmptyOrNull(sessionkey)) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            SeasonService seasonService = this.getService(SeasonService.class);
            Map<String, Object> seasonInfo = seasonService.getSeasonInfo(seasonId);
            this.renderData(seasonInfo, "成功");
        } catch (Throwable t) {
            this.renderException("getSeasonInfo", t);
        }
    }

}
