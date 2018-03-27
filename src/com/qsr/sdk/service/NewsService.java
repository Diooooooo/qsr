package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NewsService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final String SELECT_NEWS = "SELECT n.news_id, n.news_title, IFNULL(n.news_conver, '') news_conver, n.news_content, n.news_detail, " +
            "n.news_tag, n.news_author, DATE_FORMAT(n.editor_create, '%Y-%m-%d %H:%i:%s') editor_create, n.news_provenance, IFNULL(n.news_provenance_url, '') provenance_url ";
    private static final String FROM_NEWS = " FROM qsr_team_season_news n WHERE n.enabled = 1 ORDER BY n.editor_create DESC ";
    private static final String SELECT_NEWS_INFO = "SELECT n.news_title, IFNULL(n.news_conver, '') news_conver, n.news_content, n.news_detail, n.news_tag, " +
            "n.news_author, DATE_FORMAT(n.editor_create, '%Y-%m-%d %H:%i:%s') editor_create, n.news_provenance, IFNULL(n.news_provenance_url, '') provenance_url " +
            "FROM qsr_team_season_news n WHERE n.news_id = ? AND n.enabled = 1 ";
    private static final String NEWS = "SELECT s.news_id, s.news_title, s.news_conver, s.news_content, " +
            "s.news_detail, IFNULL(s.news_tag, '') tag, s.news_provenance_url, s.news_provenance " +
            "FROM qsr_team_season_news s ORDER BY s.createtime DESC";
    private static final String NEWS_PARAM = "SELECT s.news_id, s.news_title, s.news_conver, s.news_content, " +
            "s.news_detail, IFNULL(s.news_tag, '') tag, s.news_provenance_url, s.news_provenance " +
            "FROM qsr_team_season_news s " +
            "WHERE s.news_content like ? " +
            "ORDER BY s.createtime DESC";
    private static final String ADD_NEWS = "INSERT INTO qsr_team_season_news(news_title, news_conver, news_content, " +
            "news_detail, news_tag, news_provenance_url, news_provenance, author_id, " +
            "news_author, editor_create, enabled, description) " +
            "SELECT i.title, i.conver, i.content, " +
            "i.detail, i.tag, i.url, i.provenance, u.id, " +
            "u.nickname, NOW(), i.enabled, i.description " +
            "FROM (SELECT ? AS userId, ? AS title, ? AS conver, ? AS content, " +
            "? AS detail, ? AS tag, ? AS url, ? AS provenance, " +
            "? AS enabled, ? AS description) i " +
            "INNER JOIN qsr_users u ON u.id = i.userId";
    private static final String MODIFY_NEWS = "UPDATE qsr_team_season_news s " +
            "  INNER JOIN (SELECT ? AS title, ? AS conver, ? AS content, ? AS detail, ? AS tag, " +
            "  ? AS provenance_url, ? AS provenance, ? AS author, ? AS enabled, ? as description, ? AS news_id) i " +
            "  INNER JOIN qsr_users u ON i.author = u.id " +
            "  SET s.news_title = IFNULL(i.title, s.news_title), s.news_conver = IFNULL(i.conver, s.news_conver), " +
            "  s.news_content = IFNULL(i.content, s.news_content), s.news_detail = IFNULL(i.detail, s.news_detail), " +
            "  s.news_tag = IFNULL(i.tag, s.news_tag), " +
            "  s.news_provenance_url = IFNULL(i.provenance_url, s.news_provenance_url), " +
            "  s.news_provenance = IFNULL(i.provenance, s.news_provenance), " +
            "  s.author_id = IFNULL(u.id, s.author_id), s.news_author = IFNULL(u.nickname, s.news_author), " +
            "  s.enabled = IFNULL(i.enabled, s.enabled), s.description = IFNULL(i.description, s.description) " +
            "  WHERE s.news_id = i.news_id";

    @CacheAdd(timeout = 1, timeUnit = TimeUnit.MINUTES)
    public PageList<Map<String,Object>> getNews(int pageNumber, int pageSize) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_NEWS, FROM_NEWS));
        } catch (Throwable t) {
            logger.error("getNews was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "资讯加载失败", t);
        }
    }

    public Map<String,Object> getNewsInfo(int newId) throws ServiceException {
        try {
            return record2map(Db.findFirst(SELECT_NEWS_INFO, newId));
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "获取资讯详情失败", t);
        }
    }

    public List<Map<String,Object>> getNewsForManager(String title) throws ServiceException {
        try {
            List<Map<String, Object>> news;
            if (StringUtil.isEmptyOrNull(title)) {
                news = record2list(Db.find(NEWS));
            } else {
                news = record2list(Db.find(NEWS_PARAM, "%"+title+"%"));
            }
            return news;
        } catch (Throwable t){
            logger.error("getNewsForManager was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取资讯失败", t);
        }
    }

    public void addNews(String title, String fileUrl, String content, String detail, String tag,
                        String provenance_url, String provenance, String author,
                        boolean enabled, String desc) throws ServiceException {
        try {
            Db.update(ADD_NEWS, author, title, fileUrl, content, detail, tag, provenance_url, provenance, enabled, desc);
        } catch (Throwable t) {
            logger.error("addNews was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "保存资讯失败", t);
        }
    }

    public void modifyNews(int newsId, String title, String fileUrl, String content, String detail, String tag,
                           String provenance_url, String provenance, String author, boolean enabled, String desc)
            throws ServiceException {
        try {
            Db.update(MODIFY_NEWS, title, fileUrl, content, detail, tag, provenance_url, provenance, author, enabled, desc, newsId);
        } catch (Throwable t) {
            logger.error("modifyNews was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改资讯失败", t);
        }
    }
}
