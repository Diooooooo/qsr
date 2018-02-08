package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.EsotericaService;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EsotericaController extends WebApiController {
    final static Logger logger = LoggerFactory.getLogger(EsotericaController.class);
    private static final String[] SPORTTERYS = {"ZC", "RJ", "SF", "YP"};

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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByLeagueId(pageNumber,
                    pageSize, leagueId);
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
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByUserId(pageNumber, pageSize, authorityId);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaHistoryWithAuthorityPrev(pageNumber, pageSize, authorityId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithAutorityPrev", t);
        }
    }

    public void getEsotericaInfo() {
        try {
            Fetcher f = this.fetch();
            int esotericaId = f.i("esotericaId");
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaInfo(esotericaId);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> es = esotericaService.getEsotericaHistoryWithPage(pageNumber, pageSize, typeId);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListBySeasonId(pageNumber, pageSize, seasonId, typeId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithSeason", t);
        }
    }

    public void getEsotericaTop() {
        try {
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> tops = esotericaService.getEsotericaTop(1, 5);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> params = esotericaService.getEsotericaListByParam(pageNumber, pageSize, num);
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
            if (Arrays.asList(SPORTTERYS).contains(t)) {
                String type = SPORTTERYS[0].equalsIgnoreCase(t) ? "1" : SPORTTERYS[1].equalsIgnoreCase(t)
                        ? "3" : SPORTTERYS[2].equalsIgnoreCase(t) ? "2" : SPORTTERYS[3].equalsIgnoreCase(t)
                        ? "1, 2, 3" : StringUtil.NULL_STRING;
                if (StringUtil.isEmptyOrNull(type)) {
                    throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不合法");
                } else {
                    EsotericaService esotericaService = this.getService(EsotericaService.class);
                    PageList<Map<String, Object>> sportteries = esotericaService.getEsotericaListBySporttery(pageNumber, pageSize, type);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByHot(pageNumber, pageSize, sportteryId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithHot", t);
        }
    }

    public void getEsotericaWithAllSeason() {
        try {
            Fetcher f = this.fetch();
            String issue = f.s("issue");
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> esotericas = esotericaService.getEsotericaListWithIssue(issue);
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
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaWithTypeAndIssue(pageNumber, pageSize, issue, typeId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsotericaWithTypeAndIssue", t);
        }
    }

    public void getSportteryInfo() {
        try {
            Fetcher f = this.fetch();
            int _id = f.i("_id");
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            Map<String, Object> info = esotericaService.getEsotericaSportteryInfo(_id);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSportteryInfo", t);
        }
    }
}
