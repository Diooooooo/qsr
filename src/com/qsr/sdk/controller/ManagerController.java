package com.qsr.sdk.controller;

import com.jfinal.upload.UploadFile;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.*;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class ManagerController extends WebApiController {
    private final static Logger logger = LoggerFactory.getLogger(ManagerController.class);
    private static final List<String> icon = Arrays.asList("file");
    private static final String[] TYPES = {"Admin", "QSR"};
    public ManagerController() {
        super(logger);
    }


    public void login() {
        try {
            Fetcher f = this.fetch();
            logger.debug("login params = {} ", f);
            String name = f.s("name");
            String pwd = f.s("pwd");
            String type = f.s("type", TYPES[1]);
            UserService userService = this.getService(UserService.class);
            String sk = userService.login(name, pwd, type);
            Map<String, Object> info = new HashMap<>();
            info.put("sessionkey", sk);
            this.renderData(info);
        } catch (Throwable t) {
            this.renderException("login", t);
        }
    }

    /**********轮播图************/
    public void banners() {
        try {
            Fetcher f = this.fetch();
            logger.debug("banners params = {}", f);
            BannerService bannerService = this.getService(BannerService.class);
            List<Map<String, Object>> banners = bannerService.getBannerListForManager();
            this.renderData(banners);
        } catch (Throwable t) {
            this.renderException("banners was error. exception = {}", t);
        }
    }

    public void addBanner() {
        try {
            Fetcher f = this.fetch();
            String title = f.s("banner_title");
            String url = f.s("banner_url");
            String desc = f.s("description");
            boolean enabled = f.b("enabled");
            boolean deleted = f.b("deleted");
            logger.debug("addBanner params = {} ", f);
            BannerService bannerService = this.getService(BannerService.class);
            bannerService.addBanner(title, url, f.s("icon"), desc, enabled, deleted);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("addBanners was error. exception = {}", t);
        }
    }

    public void modifyBanner() {
        try {
            Fetcher f = this.fetch();
            int bannerid = f.i("banner_id");
            String title = f.s("banner_title", StringUtil.NULL_STRING);
            String url = f.s("banner_url", StringUtil.NULL_STRING);
            String desc = f.s("description", StringUtil.NULL_STRING);
            boolean enabled = f.b("enabled", true);
            boolean deleted = f.b("deleted", false);
            logger.debug("modifyBanner params = {}", f);
            BannerService bannerService = this.getService(BannerService.class);
            bannerService.modifyBanner(bannerid, title, url, f.s("icon"), desc, enabled, deleted);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("modifyBanner was error. exception = {} ", t);
        }
    }

    public void deleteBanner() {
        try {
            Fetcher f = this.fetch();
            logger.debug("deleteBanner", f);
            int bannerId = f.i("banner_id");
            BannerService bannerService = this.getService(BannerService.class);
            bannerService.deleteBanner(bannerId);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("deleteBanner", t);
        }
    }

    /*****************焦点赛事*****************/
    public void getForces() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getForces params = {} ", f);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> forces = seasonService.getForces();
            this.renderData(forces);
        } catch (Throwable t) {
            this.renderException("getForces", t);
        }
    }

    public void modifyForces() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("force_id");
            int enabled = f.i("enabled");
            logger.debug("modifyForces params = {} ", f);
            SeasonService seasonService = this.getService(SeasonService.class);
            seasonService.modifyForces(seasonId, enabled);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("modifyForces", t);
        }
    }

    public void addForces() {
        try {
            Fetcher f = this.fetch();
            int seasonId = f.i("seasonId");
            String desc = f.s("desc", StringUtil.NULL_STRING);
            SeasonService seasonService = this.getService(SeasonService.class);
            seasonService.addForces(seasonId, desc);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("addForces", t);
        }
    }

    /**************国家**************/
    public void countries() {
        try {
            Fetcher f = this.fetch();
            logger.debug("countries params={}", f);
            LeagueService leagueService = this.getService(LeagueService.class);
            List<Map<String, Object>> leagues = leagueService.getCountries();
            this.renderData(leagues);
        } catch (Throwable t) {
            this.renderException("countries", t);
        }
    }

    public void addCountry() {
        try {
            Fetcher f = this.fetch();
            logger.debug("addCountry", f);
            String name = f.s("name");
            String en = f.s("en", StringUtil.NULL_STRING);
            String code = f.s("code", StringUtil.NULL_STRING);
            String desc = f.s("desc", StringUtil.NULL_STRING);
            LeagueService leagueService = this.getService(LeagueService.class);
            leagueService.addCountry(name, en, code, desc, f.s("icon"));
            this.renderData();
        } catch (Throwable t) {
            this.renderException("addCountry", t);
        }
    }

    public void modifyCountry() {
        try {
            Fetcher f = this.fetch();
            logger.debug("addCountry", f);
            int id = f.i("id");
            String name = f.s("name", StringUtil.NULL_STRING);
            String en = f.s("en", StringUtil.NULL_STRING);
            String code = f.s("code", StringUtil.NULL_STRING);
            String desc = f.s("desc", StringUtil.NULL_STRING);
            LeagueService leagueService = this.getService(LeagueService.class);
            leagueService.modifyCountry(name, en, code, desc, f.s("icon"), id);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("modifyCountry", t);
        }
    }

    /**************大洲**********/
    public void continents() {
        try {
            Fetcher f = this.fetch();
            logger.debug("continents params = {} ", f);
        } catch (Throwable t) {
            this.renderException("continents", t);
        }
    }

    public void addContinent() {
        try {

        } catch (Throwable t) {
            this.renderException("addContinent", t);
        }
    }

    public void modifyContinent() {
        try {

        } catch (Throwable t) {
            this.renderException("modifyContinent", t);
        }
    }

    /********赛事类型**********/
    public void leagues() {
        try {
            Fetcher f = this.fetch();
            logger.debug("leagues params = {} ", f);
            LeagueService leagueService = this.getService(LeagueService.class);
            this.renderData(leagueService.getAllLeaguesForManager());
        } catch (Throwable t) {
            this.renderException("leagues", t);
        }
    }

    public void modifyLeague() {
        try {
            Fetcher f = this.fetch();
            logger.debug("modifyLeague params = {}", f);
            int leaId = f.i("lea_id");
            String name = f.s("name", StringUtil.NULL_STRING);
            String full = f.s("full", StringUtil.NULL_STRING);
            String desc = f.s("desc", StringUtil.NULL_STRING);
            int continentId = f.i("cId", 0);
            int countryId = f.i("c_id", 0);
            boolean enabled = f.b("enabled", true);
            boolean deleted = f.b("deleted", false);
            LeagueService leagueService = this.getService(LeagueService.class);
            boolean successed = leagueService.modifyLeague(leaId, name, full, desc, continentId, countryId, enabled, deleted);
            if (successed)
                this.renderData();
            else
                throw new ApiException(ErrorCode.DATA_SAVA_FAILED, "修改赛事类型失败");
        } catch (Throwable t) {
            this.renderException("modifyLeagues", t);
        }
    }

    /*****AI*******/
    public void entries() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getEntries params = {} ", f);
            ClientUiEntryService clientUiEntryService = this.getService(ClientUiEntryService.class);
            List<Map<String, Object>> entries = clientUiEntryService.getEntries();
            this.renderData(entries);
        } catch (Throwable t) {
            this.renderException("entries", t);
        }
    }

    public void modifyEntry() {
        try {

        } catch (Throwable t) {
            this.renderException("modifyEntry", t);
        }
    }

    public void addEntry() {
        try {

        } catch (Throwable t) {
            this.renderException("addEntry", t);
        }
    }

    /**********APP***************/
    public void apps() {
        try {
            Fetcher f = this.fetch();
            logger.debug("apps params = {} ", f);
            UpdateService updateService = this.getService(UpdateService.class);
            this.renderData(updateService.getAllUpdateHistory());
        } catch (Throwable t){
            this.renderException("apps", t);
        }
    }

    public void modifyApp() {
        try {

        } catch (Throwable t) {
            this.renderException("modifyApp", t);
        }
    }

    public void addApp() {
        try {

        } catch (Throwable t) {
            this.renderException("addApp", t);
        }
    }

    /*************锦囊**************/
    public void esotericas() {
        try {
            Fetcher f = this.fetch();
            logger.debug("esotericas params = {}", f);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> es = esotericaService.getEsotericaListForManager();
            this.renderData(es);
        } catch (Throwable t){
            this.renderException("esotericas", t);
        }
    }

    public void addEsoterica() {
        try {
            Fetcher f = this.fetch();
            logger.debug("addEsoterica params = {}", f);
            int entryId = f.i("id");
            String seasonId = f.s("season_ids");
            String title = f.s("title");
            String intro = f.s("intro");
            String detail = f.s("detail");
            int price = f.i("price");
            int typeId = f.i("type", 1);
            boolean top = f.b("top", false);
            boolean enabled = f.b("enabled", true);
            boolean open = f.b("open", false);
            String tag = f.s("tag", StringUtil.NULL_STRING);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            esotericaService.addEsoterica(entryId, seasonId, title, intro, detail, price, top, enabled, open, tag, typeId);
            this.renderData();
        } catch (Throwable t){
            this.renderException("addEsoterica", t);
        }
    }

    public void modifyEsoterica() {
        try {
            Fetcher f = this.fetch();
            logger.debug("modifyEsoterica params = {}", f);
            int id = f.i("esoterica_id");
            boolean enabled = f.b("enabled", true);
            boolean open = f.b("open", false);
            boolean top = f.b("top", false);
            int price = f.i("price", 0);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            boolean successed = esotericaService.modifyEsoterica(id, enabled, open, top, price);
            System.out.println(successed);
            this.renderData();
        } catch (Throwable t){
            this.renderException("modifyEsoterica", t);
        }
    }

    public void esotericaTypes() {
        try {
            Fetcher f = this.fetch();
            logger.debug("esotericaTypes params = {}", f);
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> types = esotericaService.getEsotericaType();
            this.renderData(types);
        } catch (Throwable t) {
            this.renderException("esotericaTypes", t);
        }
    }

    public void getItem() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getItem params = {} ", f);
            int id = f.i("esoterica_id");
            EsotericaService esotericaService = this.getService(EsotericaService.class);
            List<Map<String, Object>> infos = esotericaService.getEsotericaSeasonItem(id);
            this.renderData(infos);
        } catch (Throwable t) {
            this.renderException("getItem", t);
        }
    }

    /**********赛事***********/
    public void seasons() {
        try {
            Fetcher f = this.fetch();
            logger.debug("seasons params = {} ", f);
            SeasonService seasonService = this.getService(SeasonService.class);
            List<Map<String, Object>> seasons = seasonService.getSeasonList();
            this.renderData(seasons);
        } catch (Throwable t) {
            this.renderException("seasons", t);
        }
    }

    /*********资讯***********/
    public void getNews() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getNews params = {} ", f);
            String title = f.s("title", StringUtil.NULL_STRING);
            NewsService newsService = this.getService(NewsService.class);
            List<Map<String, Object>> news = newsService.getNewsForManager(title);
            this.renderData(news);
        } catch (Throwable t) {
            this.renderException("getNews", t);
        }
    }

    public void modifyNews() {
        try {
            Fetcher f = this.fetch();
            logger.debug("addNews params = {} ", f);
            int newsId = f.i("newsId");
            String title = f.s("title", StringUtil.NULL_STRING);
            String content = f.s("content", StringUtil.NULL_STRING);
            String detail = f.s("detail", StringUtil.NULL_STRING);
            String tag = f.s("tag", StringUtil.NULL_STRING);
            String provenance = f.s("provenance", "量球匠");
            String provenance_url = f.s("provenance_url", "http://www.liangqiujiang.com");
            boolean enabled = f.b("enabled", false);
            String desc = f.s("desc", StringUtil.NULL_STRING);
            String author = f.s("author");
            NewsService newsService = this.getService(NewsService.class);
            newsService.modifyNews(newsId, title, f.s("icon"), content, detail, tag, provenance_url, provenance, author, enabled, desc);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("modifyNews", t);
        }
    }

    public void addNews() {
        try {
            Fetcher f = this.fetch();
            logger.debug("addNews params = {} ", f);
            String title = f.s("title");
            String content = f.s("content");
            String detail = f.s("detail");
            String tag = f.s("tag");
            String provenance = f.s("provenance", "量球匠");
            String provenance_url = f.s("provenance_url", "http://www.liangqiujiang.com");
            boolean enabled = f.b("enabled", false);
            String desc = f.s("desc", StringUtil.NULL_STRING);
            String author = f.s("author");
            NewsService newsService = this.getService(NewsService.class);
            newsService.addNews(title, f.s("icon"), content, detail, tag, provenance_url, provenance, author, enabled, desc);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("addNews", t);
        }
    }

    public void pic() {
        try {
            Fetcher f = this.fetch();
            logger.debug("pic params = {}", f);
            List<UploadFile> temps = f.getUploadFiles();
            List<UploadFile> ufs = new ArrayList<>();
            for (UploadFile up: temps) {
                if (icon.contains(up.getParameterName()))
                    ufs.add(up);
            }
            FileStorageService fss = this.getService(FileStorageService.class);
            int[] fileIds = new int[ufs.size()];
            int i = 0;
            uploadFiles(temps, fss, fileIds, i, "news");
            this.renderData((Object)fss.getFileUrl(fileIds[0]));
        } catch (Throwable t) {
            this.renderException("pic", t);
        }
    }

    /**********球队**********/
    public void getTeams() {
        try {

        } catch (Throwable t) {
            this.renderException("getTeams", t);
        }
    }

    public void addTeam() {
        try {
        } catch (Throwable t) {
            this.renderException("addTeam", t);
        }
    }

    public void modifyTeam() {
        try {
            Fetcher f = this.fetch();
        } catch (Throwable t) {
            this.renderException("modifyTeam", t);
        }
    }


    private void uploadFiles(List<UploadFile> files, FileStorageService fss, int[] fileIds, int i, String fix) throws ServiceException {
        for (UploadFile uf: files) {
            File file = uf.getFile();
            String fileUrl = fss.getFileMd5Uri(fix + "/", file);
            fileIds[i] = fss.uploadFile(Env.getFileStorageProviderId(), fileUrl, file);
            file.delete();
            i ++;
        }
    }
}
