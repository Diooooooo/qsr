package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.ChannelService;
import com.qsr.sdk.service.EntryService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ParameterUtil;
import com.qsr.sdk.util.StringUtil;
import com.qsr.sdk.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by fc on 2016-07-08.
 */
public class EntryController extends WebApiController {
    private final static Logger logger = LoggerFactory.getLogger(EntryController.class);

    protected final static int TRAIN = 12;

    protected final static int INFORM = 10;

    protected final static int MOBILE_TYPE = 11;

    protected final static int PUSH_MOBILE_TYPE = 16;

    public EntryController() {
        super(logger);
    }

    public void getEntryList() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getEntryList, param={}", f);
            String sessionkey = f.s("sessionkey");
            String origin = f.s("origin", "ios");
            String channel = f.s("channel", StringUtil.EMPTY_STRING);
            int version = f.i("version", 54);
            int isApprove = f.i("is_approve", 0);
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            ChannelService channelService = this.getService(ChannelService.class);
            int channelId = channelService.getChannelIdByHost(this.getHeader("host"));
            EntryService entityService = this.getService(EntryService.class);
            List<Map<String, Object>> categories = entityService.getEntryList(channelId, "ANDROID".equals(origin.toUpperCase()), version);
            List<Map<String, Object>> actualCategories = new ArrayList<>();
            //添加标志位，用于判定抽奖、销售团、销售训练营的显示与否
            categories.stream().forEach(target -> {
                if (EntryService.ENTRY_FLAGS.contains(","+String.valueOf(target.get("entry_id"))+",")){
                    target.put("is_lottery_show", false);
                    target.put("is_sale_show", false);
                    target.put("is_sales_show", false);
                } else {
                    target.put("is_lottery_show", false);
                    target.put("is_sale_show", false);
                    target.put("is_sales_show", false);
                }
                if (50 == ParameterUtil.integerParam(target, "entry_id")) {
                    target.put("is_lottery_show", true);
                }
            });
//            配合IOS审核
            if (1 == isApprove && ("APPSTORE".equals(channel.toUpperCase()))) {
                categories.stream().filter(target -> !Env.getFilter_appstore_verify().contains(ParameterUtil.stringParam(target, "entry_id"))).forEach(target -> actualCategories.add(target));
            } else {
                actualCategories.addAll(categories);
            }
            this.renderData(actualCategories);
        } catch (Throwable t) {
            this.renderException("getEntryList", t);
        }
    }

    public void getEntryListByEntryId() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getEntryListByEntryId, param={}", f);
            int entryId = f.i("entry_id");
            String origin = f.s("origin", "ios");
            int version = f.i("version", 5);
            String sessionkey = f.s("sessionkey");
            ChannelService channelService = this.getService(ChannelService.class);
            int channelId = channelService.getChannelIdByHost(this.getHeader("host"));
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EntryService entityService = this.getService(EntryService.class);
            List<Map<String, Object>> categories = entityService.getEntryListByEntryId(entryId, channelId, "ANDROID".equals(origin.toUpperCase()), version);
            Map<String, String> info = new HashMap<>();
            info.put("sessionkey", sessionkey);
            categories.stream().forEach(target -> target.put("h5_url", TemplateUtil.process((String) target.get("h5_url"), info)));
            this.renderData(categories);
        } catch (Throwable t) {
            this.renderException("getEntryListByEntryId", t);
        }
    }

    public void getEntryInfoByEntryId() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getH5UrlBySupplierId, param={}", f);
            int entryId = f.i("entry_id");
            String sessionkey = f.s("sessionkey");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EntryService entityService = this.getService(EntryService.class);
            List<Map<String, Object>> suppliers = entityService.getEntryInfoByEntryId(entryId);
            this.renderData(suppliers);
        } catch (Throwable t) {
            this.renderException("getEntryInfoByEntryId", t);
        }
    }

    public void getItemListByEntryId() {
        try {
            Fetcher f = this.fetch();
            int entryId = f.i("entry_id");
            int pageindex = f.i("pageindex", 1);
            int pagesize = f.i("pagesize", 10);
            int version = f.i("version", 5);
            String origin = f.s("origin", "ios");
            String sessionkey = f.s("sessionkey");
            ChannelService channelService = this.getService(ChannelService.class);
            int channelId = channelService.getChannelIdByHost(this.getHeader("host"));
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EntryService entryService = this.getService(EntryService.class);
            PageList<Map<String, Object>> suppliers = entryService.getItemListByEntryId(entryId, pageindex, pagesize, "ANDROID".equals(origin.toUpperCase()), version, channelId);
            this.renderData(suppliers);
        } catch (Throwable t) {
            this.renderException("getEntryInfoByEntryIdAndTypeId", t);
        }
    }

    public void getItemListByMobileType() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EntryService entryService = this.getService(EntryService.class);
            List<Map<String, Object>> infos = entryService.getItemListByMobileType(PUSH_MOBILE_TYPE);
            this.renderData(infos);
        } catch (Throwable t) {
            this.renderException("getitemListByMobileType", t);
        }
    }

    @Deprecated
    public void getItemInfoByEntryId() {
        try {
            Fetcher f = this.fetch();
            int entryId = f.i("entry_id");
            String sessionkey = f.s("sessionkey");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            EntryService entryService = this.getService(EntryService.class);
            Map<String, Object> info = entryService.getItemInfoByEntryId(entryId);
            this.renderData(info);
        } catch (Throwable t) {
            this.renderException("getItemInfoByEntryIdAndTypeId", t);
        }
    }

    public void getTrainList() {
        try {
            Fetcher f = this.fetch();
            List<Map<String, Object>> entries = getEntriesBySessionkey(f, TRAIN);
            this.renderData(entries);
        } catch (Throwable t) {
            this.renderException("getTrainList", t);
        }
    }

    public void getInformList() {
        try {
            Fetcher f = this.fetch();
            List<Map<String, Object>> entries = getEntriesBySessionkey(f, INFORM);
            this.renderData(entries);
        } catch (Throwable t) {
            this.renderException("getInformList", t);
        }
    }

    public void getMobileTypeList() {
        try {
            Fetcher f = this.fetch();
            List<Map<String, Object>> entries = getEntriesBySessionkey(f, MOBILE_TYPE);
            this.renderData(entries);
        } catch (Throwable t) {
            this.renderException("getMobileType", t);
        }
    }

    private List<Map<String, Object>> getEntriesBySessionkey(Fetcher f, int type) throws ServiceException {
        String sessionkey = f.s("sessionkey");
        String origin = f.s("origin", "ios");
        int version = f.i("version", 5);
        logger.info("fetcher={}, type={}", f, type);
        ChannelService channelService = this.getService(ChannelService.class);
        int channelId = channelService.getChannelIdByHost(this.getHeader("host"));
        UserService userService = this.getService(UserService.class);
        int userId = userService.getUserIdBySessionKey(sessionkey);
        EntryService entryService = this.getService(EntryService.class);
//        List<Map<String, Object>> info = entryService.getEntryListForThirdByEntryId(type);
        List<Map<String, Object>> info = entryService.getEntryListByEntryId(type, channelId, "ANDROID".equals(origin.toUpperCase()), version);
        return info;
    }

    public void getSaleItem() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            Map<String, Object> pk = new HashMap<>();
            pk.put("name", "PK");
            pk.put("url","http://cm.autxz.com/Api/SalesPk/index");
//            Map<String, Object> task = new HashMap<>();
//            task.put("name", "任务");
//            task.put("url","");
//            Map<String, Object> welfare = new HashMap<>();
//            welfare.put("name", "福利");
//            welfare.put("url","");
            List<Map<String, Object>> res = new LinkedList<>();
            res.add(pk);
//            res.add(task);
//            res.add(welfare);
            this.renderData(res);
        } catch (Throwable t) {
            this.renderException("getSaleItem", t);
        }
    }

    public void getSaleFactory() {
        try {
             Fetcher f = this.fetch();
             String sessionkey = f.s("sessionkey");
             Map<String, Object> factory = new HashMap<>();
             factory.put("name", "招募学员");
             factory.put("url", "");
             List<Map<String, Object>> res = new LinkedList<>();
             res.add(factory);
             this.renderData(res);
        } catch (Throwable t) {
            this.renderException("getSaleFactory", t);
        }
    }
}
