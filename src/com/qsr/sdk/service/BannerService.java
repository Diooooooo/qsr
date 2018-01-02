package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class BannerService extends Service {

    final static Logger logger = LoggerFactory.getLogger(BannerService.class);

    final static String BANNER_SQL = "SELECT IFNULL(b.banner_title, '') title, IFNULL(b.banner_icon, '') icon, " +
            "IFNULL(b.banner_url, '') url  FROM qsr_banner b WHERE b.enabled = 1 AND b.deleted = 0";

    public List<Map<String,Object>> getBannerList() throws ServiceException {
        try {
            return record2list(Db.find(BANNER_SQL));
        } catch (Throwable t) {
            logger.error("getBannerList was error, exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载轮播失败", t);
        }
    }
}
