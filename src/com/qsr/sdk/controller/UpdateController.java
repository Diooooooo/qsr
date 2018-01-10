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

    public void update() {
        try {
            Fetcher f = this.fetch();
            int versionCode = f.i("version_code");
            UpdateService updateService = this.getService(UpdateService.class);
            Map<String, Object> info = updateService.update(versionCode);
            this.renderData(info, SUCCESS);
        } catch (Throwable t) {
            this.renderException("update", t);
        }
    }
}
