package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class BannerController extends WebApiController {
    final static Logger logger = LoggerFactory.getLogger(BannerController.class);

    public BannerController() {
        super(logger);
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            logger.debug("home params = {}", f);
            BannerService bannerService = this.getService(BannerService.class);
            List<Map<String, Object>> banners = bannerService.getBannerList();
            this.renderData(banners, SUCCESS);
        } catch (Throwable t) {
            this.renderException("home was erro. exception = {}", t);
        }
    }

}
