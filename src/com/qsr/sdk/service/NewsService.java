package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NewsService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final String SELECT_NEWS = "SELECT n.news_title, IFNULL(n.news_conver, '') news_conver, n.news_content, n.news_detail, " +
            "n.news_tag, n.news_author, DATE_FORMAT(n.editor_create, '%Y-%m-%d') editor_create, n.news_provenance ";
    private static final String FROM_NEWS = " FROM qsr_team_season_news n WHERE n.enabled = 1 ORDER BY n.editor_create DESC ";
    private static final String SELECT_NEWS_INFO = "SELECT n.news_title, IFNULL(n.news_conver, '') news_conver, n.news_content, n.news_detail, n.news_tag, " +
            "n.news_author, DATE_FORMAT(n.editor_create, '%Y-%m-%d') editor_create, n.news_provenance " +
            "FROM qsr_team_season_news n WHERE n.news_id = ? AND n.enabled = 1 ";

    public PageList<Map<String,Object>> getNews(int pageNumber, int pageSize) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_NEWS, FROM_NEWS));
        } catch (Throwable t) {
            logger.error("getNews was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "咨询加载失败", t);
        }
    }

    public Map<String,Object> getNewsInfo(int newId) throws ServiceException {
        try {
            return record2map(Db.findFirst(SELECT_NEWS_INFO, newId));
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "获取资讯详情失败", t);
        }
    }
}
