package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.MessageService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController() {
        super(logger);
    }

    public void sendMessage() {
        try {
            Fetcher f = this.fetch();
            String sendTo = f.s("send_to");
            String fromTo = f.s("from");
            String message = f.s("msg");
            String type = f.s("type", StringUtil.EMPTY_STRING);
            MessageService messageService = this.getService(MessageService.class);
            messageService.sendMessage(fromTo, sendTo, message, 1);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("sendMessage", t);
        }
    }
}
