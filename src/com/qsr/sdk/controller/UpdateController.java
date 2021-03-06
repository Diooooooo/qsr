package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpdateController extends WebApiController {

    private final static Logger logger = LoggerFactory.getLogger(UpdateController.class);

    public UpdateController() {
        super(logger);
    }

    /**
     * mobile_type
     *              1   android
     *              2   ios
     *              3   wp
     *              4   symbian
     *              5   blackberry
     *              6   yunos
     *              7   other
     */
    public void update() {
        try {
            Fetcher f = this.fetch();
            int mobileType = f.i("mobile_type", 1);
            UpdateService updateService = this.getService(UpdateService.class);
            Map<String, Object> info = updateService.update(mobileType);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("update", t);
        }
    }
}
