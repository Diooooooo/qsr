package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.BalanceService;
import com.qsr.sdk.service.EsotericaService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.StringUtil;
import org.kie.api.task.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EsotericaController extends WebApiController {
    final static Logger logger = LoggerFactory.getLogger(EsotericaController.class);
    private static final String[] SPORTTERYS = {"ZC", "RJ", "SF", "YP"};
    private static final String[] TYPES = {"A", "O", "W", "C"};

    public EsotericaController() {
        super(logger);
    }

    public void getEsoterica() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("league_id");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByLeagueId(pageNumber,
                    pageSize, leagueId, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsoterica", t);
        }
    }

    public void getEsotericaWithAuthority() {
        try {
            Fetcher f = this.fetch();
            int authorityId = f.i("authority");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByUserId(pageNumber, pageSize, authorityId, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithAuthority", t);
        }
    }

    public void getEsotericaWithAuthorityPrev() {
        try {
            Fetcher f = this.fetch();
            int authorityId = f.i("authority");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaHistoryWithAuthorityPrev(pageNumber, pageSize, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithAutorityPrev", t);
        }
    }

    public void getEsotericaInfo() {
        try {
            Fetcher f = this.fetch();
            String esotericaId = f.s("esotericaId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaInfo(esotericaId, userId);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaInfo", t);
        }
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            logger.debug("home params={}", f);
            int typeId = f.i("typeId", 1);
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 5);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> es = esotericaService.getEsotericaHistoryWithPage(pageNumber, pageSize, typeId, userId);
            this.renderData(es, SUCCESS);
        } catch (Throwable t) {
            this.renderException("home", t);
        }
    }

    public void getEsotericaWithSeason() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("seasonId");
            int typeId = f.i("typeId", -1);
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 5);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListBySeasonId(pageNumber, pageSize, seasonId, typeId, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithSeason", t);
        }
    }

    public void getEsotericaTop() {
        try {
            String sessionkey = fetch().s("sessionkey", StringUtil.NULL_STRING);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            PageList<Map<String, Object>> tops = esotericaService.getEsotericaTop(1, 5, userId);
            this.renderData(tops, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaTop", t);
        }
    }

    public void getEsotericaWithParam() {
        try {
            Fetcher f = this.fetch();
            int num = f.i("number");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> params = esotericaService.getEsotericaListByParam(pageNumber, pageSize, num, userId);
            this.renderData(params, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithParam", t);
        }
    }

    public void getEsotericaWithSporttery() {
        try{
            Fetcher f = this.fetch();
            String t = f.s("sporttery");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            if (Arrays.asList(SPORTTERYS).contains(t)) {
                String type = SPORTTERYS[0].equalsIgnoreCase(t) ? "1" : SPORTTERYS[1].equalsIgnoreCase(t)
                        ? "3" : SPORTTERYS[2].equalsIgnoreCase(t) ? "2" : SPORTTERYS[3].equalsIgnoreCase(t)
                        ? "1, 2, 3" : StringUtil.NULL_STRING;
                if (StringUtil.isEmptyOrNull(type)) {
                    throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不合法");
                } else {
                    EsotericaService esotericaService = this.getService(EsotericaService.class);
                    PageList<Map<String, Object>> sportteries = esotericaService.getEsotericaListBySporttery(pageNumber, pageSize, type, userId);
                    this.renderData(sportteries, SUCCESS);
                }
            } else {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不合法");
            }
        } catch (Throwable t) {
            this.renderException("getEsotericaWithSporttery", t);
        }
    }

    public void getEsotericaWithSeasonList() {
        try {
            Fetcher f = this.fetch();
            String number = f.s("issue");
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> esotericas = esotericaService.getEsotericaListByIssue(number);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithSeasonList", t);
        }
    }

    public void getEsotericaSportteries() {
        try {
            Fetcher f = this.fetch();
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> sportteries = esotericaService.getEsotericaSportteries();
            this.renderData(sportteries, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaSportteries", t);
        }
    }

    public void getEsotericaWithHot() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            int sportteryId = f.i("sportteryId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByHot(pageNumber, pageSize, sportteryId, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithHot", t);
        }
    }

    public void getEsotericaWithAllSeason() {
        try {
            Fetcher f = this.fetch();
            String issue = f.s("issue");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> esotericas = esotericaService.getEsotericaListWithIssue(issue, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithAllSeason", t);
        }
    }

    public void getEsotericaWithTypeAndIssue() {
        try {
            Fetcher f = this.fetch();
            String issue = f.s("issue");
            int typeId = f.i("type");
            int pageNumber = f.i("pageNumber");
            int pageSize = f.i("pageSize");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaWithTypeAndIssue(pageNumber, pageSize, issue, typeId, userId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithTypeAndIssue", t);
        }
    }

    public void getSportteryInfo() {
        try {
            Fetcher f = this.fetch();
            int _id = f.i("_id");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaSportteryInfo(_id, userId);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSportteryInfo", t);
        }
    }

    public void payEsoterica() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String esotrica_id = f.s("esoterica_id");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            BalanceService balanceService = this.getService(BalanceService.class);
            long pay = balanceService.orderEsoterica(userId, esotrica_id);
            if (!balanceService.check(userId, esotrica_id)) {
                throw new ApiException(ErrorCode.DATA_VERIFYDATA_ERROR, "账户余额不足");
            }
            if (balanceService.payEsoterica(userId, esotrica_id, pay))
                this.renderData(SUCCESS);
            else
                throw new ApiException(ErrorCode.DATA_VERIFYDATA_ERROR, "购买锦囊失败");
        } catch (Throwable t) {
            this.renderException("payEsoterica", t);
        }
    }

    public void getEsotericaListWithPayUser() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String type = f.s("type", "A");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            if (!Arrays.asList(TYPES).contains(type)) {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不正确");
            }
            int typeId;
            if (type.equalsIgnoreCase(TYPES[0])) {
                typeId = 0;
            } else if (type.equalsIgnoreCase(TYPES[1])) {
                typeId = 1;
            } else if (type.equalsIgnoreCase(TYPES[2])){
                typeId = 2;
            } else {
                typeId = 3;
            }
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            this.renderData(esotericaService.getEsotericaListWithPayUser(userId, typeId, pageNumber, pageSize), SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaListWithPayUser", t);
        }
    }

    public void repayEsoterica() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String esotericaId = f.s("esoterica_id");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            BalanceService balanceService = this.getService(BalanceService.class);
            if (!balanceService.checkWithEsoterica(userId, esotericaId)) {
                throw new ApiException(ErrorCode.DATA_VERIFYDATA_ERROR, "账户余额不足");
            }
            boolean flag = esotericaService.repayEsoterica(userId, esotericaId);
            if (flag)
                this.renderData(SUCCESS);
            else
                throw new ApiException(ErrorCode.DATA_VERIFYDATA_ERROR, "支付失败");
        } catch (Throwable t) {
            this.renderException("repayEsoterica", t);
        }
    }

    public void delEsoterica() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String esotericaId = f.s("esoterica_id");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            esotericaService.delEsoterica(userId, esotericaId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("delEsoterica", t);
        }
    }

    public void cancelEsoterica() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            String esotericaId = f.s("esoterica_id");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            esotericaService.cancelEsoterica(userId, esotericaId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("cancelEsoterica", t);
        }
    }
}
