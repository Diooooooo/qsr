package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NewsController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    public NewsController() {
        super(logger);
    }

    public void getNews() {
        try {
            Fetcher f = this.fetch();
            int pageSize = f.i("page_size", 10);
            int pageNumber = f.i("page_number", 1);
            NewsService newsService = this.getService(NewsService.class);
            PageList<Map<String, Object>> news = newsService.getNews(pageNumber, pageSize);
            this.renderData(news, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getNews", t);
        }
    }
}
