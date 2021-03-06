package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LeagueService extends Service {

    final static Logger logger = LoggerFactory.getLogger(LeagueService.class);
    final static String SELECT = "SELECT l.lea_name leagueName, l.lea_id leagueId, IFNULL(l.description, '') desc_ ";
    final static String FROM_All = "FROM qsr_league l WHERE l.enabled = 1 ORDER BY l.sorted DESC";
    final static String FROM_FIVE = "FROM qsr_league l WHERE l.enabled = 1 ORDER BY l.sorted DESC";
    final static String FROM_AVERAGE = "FROM qsr_league l WHERE l.is_average = 1 AND l.enabled = 1 ORDER BY l.sorted DESC";
    private static final String LEAGUE_INFO = "FROM qsr_league l WHERE l.lea_id = ? AND l.enabled = 1 ";
    private static final String LEAGUES = "SELECT c.country_id, c.country_name, c.country_code, c.country_en, " +
            "c.country_icon, c.description, c.enabled FROM qsr_league_country c ORDER BY c.enabled DESC, CONVERT(c.country_name USING GBK) ASC";
    private static final String ADD_COUNTRY = "INSERT INTO qsr_league_country " +
            "(country_name,country_code,country_en,country_icon,description) VALUES(?, ?, ?, ?, ?)";
    private static final String MODIFY_COUNTRY = "UPDATE qsr_league_country c " +
            "  INNER JOIN (SELECT ? AS name, ? AS en, ? AS code, ? AS icon, ? AS description, ? AS id) i " +
            "  SET c.country_name = IFNULL(i.name, c.country_name), c.country_en = IFNULL(i.en, c.country_en), " +
            "  c.country_code = IFNULL(i.code, c.country_code), c.country_icon = IFNULL(i.icon, c.country_icon), " +
            "  c.description = IFNULL(i.description, c.description) " +
            "  WHERE c.country_id = i.id";
    private static final String LEAGUES_ALL = "SELECT l.lea_id, l.lea_name, l.lea_full_name, IFNULL(l.description, '') desc_, " +
            "IFNULL(lc.continent_name, '') continent_name, IFNULL(c.country_name, '') country_name, " +
            "l.enabled, l.deleted " +
            "FROM qsr_league l " +
            "LEFT JOIN qsr_league_country c ON l.country_id = c.country_id " +
            "LEFT JOIN qsr_league_continent lc ON l.continent_id = lc.continent_id " +
            "WHERE l.enabled = 1 " +
            "ORDER BY l.sorted DESC, l.enabled DESC";
    private static final String MODIFY_LEAGUE = "UPDATE qsr_league l " +
            "  INNER JOIN (SELECT ? AS n, ? AS f, ? AS de, ? AS cId, ? AS c_id, ? AS e, ? AS d, ? AS i) ql " +
            "  LEFT JOIN qsr_league_continent lc ON ql.cId = lc.continent_id " +
            "  LEFT JOIN qsr_league_country c ON ql.c_id = c.country_id " +
            "  SET l.lea_name = IFNULL(ql.n, l.lea_name), l.lea_full_name = IFNULL(ql.f, l.lea_full_name), " +
            "  l.description = IFNULL(ql.de, l.description), l.continent_id = IFNULL(lc.continent_id, l.continent_id), " +
            "  l.country_id = IFNULL(c.country_id, l.country_id), l.enabled = IF(ql.e, TRUE, FALSE), l.deleted = IF(ql.d, TRUE, FALSE) " +
            "  WHERE l.lea_id = ql.i";

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getAllLeagues() throws ServiceException {
        try {
            return record2list(Db.find(SELECT + FROM_All));
        } catch (Throwable t) {
            logger.error("getAllLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getFiveLeagues() throws ServiceException {
        try {
            return record2list(DbUtil.paginate(1,6,SELECT, FROM_FIVE).getList());
        } catch (Throwable t) {
            logger.error("getFiveLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }

    @CacheAdd(timeUnit = TimeUnit.HOURS, timeout = 2)
    public List<Map<String, Object>> getAverageLeagues() throws ServiceException {
        try {
            return record2list(Db.find(SELECT + FROM_AVERAGE));
        } catch (Throwable t) {
            logger.error("getAverageLeagues was error. exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "消息加载失败", t);
        }
    }

    public Map<String, Object> getLeagueInfo(int leagueId) throws ServiceException {
        try {
            Map<String, Object> info = record2map(Db.findFirst(SELECT + LEAGUE_INFO, leagueId));
            if (null == info)
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛事失败");
            return info;
        } catch (Throwable t) {
            logger.error("getLeagueInfo was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛事失败", t);
        }
    }

    public List<Map<String,Object>> getCountries() throws ServiceException {
        try {
            return record2list(Db.find(LEAGUES));
        } catch (Throwable t) {
            logger.error("getLeagues was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛事类型失败", t);
        }
    }

    public void addCountry(String name, String en, String code, String desc, String fileUrl) throws ServiceException {
        try {
            Db.update(ADD_COUNTRY, name, en, code, desc, fileUrl);
        } catch (Throwable t) {
            logger.error("addCountry was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "添加国家信息失败", t);
        }
    }

    public void modifyCountry(String name, String en, String code, String desc, String fileUrl, int id) throws ServiceException {
        try {
            Db.update(MODIFY_COUNTRY, name, en, code, fileUrl, desc, id);
        } catch (Throwable t) {
            logger.error("modifyCountry was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改国家信息失败", t);
        }
    }

    public List<Map<String, Object>> getAllLeaguesForManager() throws ServiceException {
        try {
            return record2list(Db.find(LEAGUES_ALL));
        } catch (Throwable t) {
            logger.error("getAllLeaguesForManager was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛事类型失败", t);
        }
    }

    public boolean modifyLeague(int leaId, String name, String full, String desc, int continentId, int countryId,
                                boolean enabled, boolean deleted) throws ServiceException {
        try {
            return Db.update(MODIFY_LEAGUE, name, full, desc, continentId, countryId, enabled, deleted, leaId) > 0;
        } catch (Throwable t) {
            logger.error("modifyLeague was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改赛事类型失败", t);
        }
    }
}
