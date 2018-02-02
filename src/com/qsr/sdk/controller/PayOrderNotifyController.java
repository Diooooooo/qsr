package com.qsr.sdk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayOrderNotifyController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderNotifyController.class);
    public PayOrderNotifyController() {
        super(logger);
    }

    public void nofity() {
        try {

        } catch (Throwable t) {
            this.renderException("notify", t);
        }
    }
}
