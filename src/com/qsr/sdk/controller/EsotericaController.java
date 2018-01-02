package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.EsotericaService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EsotericaController extends WebApiController {
    final static Logger logger = LoggerFactory.getLogger(EsotericaController.class);

    public EsotericaController() {
        super(logger);
    }

    public void getEsoterica() {
        try {
            Fetcher f = this.fetch();
            int leagueId = f.i("leagueId");
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            PageList<Map<String, Object>> esotericas = esotericaService.getEsotericaListByLeagueId(pageNumber,
                    pageSize, leagueId);
            this.renderData(esotericas, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getEsoterica was error, exception = {}", t);
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
            this.renderException("getEsotericaInfo was error, exception = {}", t);
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
            this.renderException("home was error. exception = {}", t);
        }
    }
}
