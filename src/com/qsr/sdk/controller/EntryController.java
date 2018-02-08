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

    public EntryController() {
        super(logger);
    }

    public void getSpecialists() {
        try {
            Fetcher f = this.fetch();
            logger.debug("getSpecialists params = {} ", f);
            EntryService entryService = this.getService(EntryService.class);
            List<Map<String, Object>> specialists = entryService.getEntryList();
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialist", t);
        }
    }

    public void getSpecialistWithAI() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 5);
            EntryService entryService = this.getService(EntryService.class);
            List<Map<String, Object>> specialists = entryService.getEntryListWithAI(pageNumber, pageSize);
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialistWithAI", t);
        }
    }

    public void getSpecialistWithStar() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 5);
            EntryService entryService = this.getService(EntryService.class);
            List<Map<String, Object>> specialists = entryService.getEntryListWithStar(pageNumber, pageSize);
            this.renderData(specialists, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getSpecialistWithStar", t);
        }
    }
}
