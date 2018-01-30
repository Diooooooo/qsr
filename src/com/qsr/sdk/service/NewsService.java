package com.qsr.sdk.service;

import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NewsService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final String SELECT_NEWS = "SELECT n.news_title, n.news_content, n.news_detail, n.news_tag, n.news_author, n.editor_create, n.news_provenance ";
    private static final String FROM_NEWS = " FROM qsr_team_season_news n WHERE n.enabled = 1 ";

    public PageList<Map<String,Object>> getNews(int pageNumber, int pageSize) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_NEWS, FROM_NEWS));
        } catch (Throwable t) {
            logger.error("getNews was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "咨询加载失败", t);
        }
    }
}
