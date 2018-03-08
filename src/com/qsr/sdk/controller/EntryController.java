package com.qsr.sdk.controller;

import cn.jpush.api.report.UsersResult;
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

    public EntryController() {
        super(logger);
    }

    public void getSpecialists() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            logger.debug("getSpecialists params = {} ", f);
            int userId = 0;
            EntryService entryService = this.getService(EntryService.class);
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            List<Map<String, Object>> specialists = entryService.getEntryList(userId);
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialist", t);
        }
    }

    public void getSpecialistWithAI() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 6);
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int userId = 0;
            EntryService entryService = this.getService(EntryService.class);
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            List<Map<String, Object>> specialists = entryService.getEntryListWithAI(userId, pageNumber, pageSize);
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialistWithAI", t);
        }
    }

    public void getSpecialistWithStar() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 6);
            int userId = 0;
            EntryService entryService = this.getService(EntryService.class);
            if (null != sessionkey) {
                UserService userService = this.getService(UserService.class);
                userId = userService.getUserIdBySessionKey(sessionkey);
            }
            List<Map<String, Object>> specialists = entryService.getEntryListWithStar(userId, pageNumber, pageSize);
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialistWithStar", t);
        }
    }
}
