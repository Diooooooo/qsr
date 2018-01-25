package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.ClientUiEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ClientUiEntryController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(ClientUiEntryController.class);

    public ClientUiEntryController(Logger logger) {
        super(logger);
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            int code = f.i("code");
            String platform = f.s("platform");
            ClientUiEntryService clientUiEntryService = this.getService(ClientUiEntryService.class);
            List<Map<String, Object>> entries = clientUiEntryService.getEntries(code, platform);
            this.renderData(entries, SUCCESS);
        } catch (Exception e) {
            this.renderException("home", e);
        }
    }
}
