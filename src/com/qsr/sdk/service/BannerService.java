package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BannerService extends Service {

    final static Logger logger = LoggerFactory.getLogger(BannerService.class);

    final static String BANNER_SQL = "SELECT IFNULL(b.banner_title, '') title, IFNULL(b.banner_icon, '') icon, " +
            "IFNULL(b.banner_url, '') url  FROM qsr_banner b WHERE b.enabled = 1 AND b.deleted = 0";
    private static final String BANNER_MODIFY = "UPDATE qsr_banner b SET b.banner_title = ?, b.banner_url = ?, b.banner_icon = ?, b.description = ?, b.enabled = ?, b.deleted = ? WHERE b.banner_id = ?";
    private static final String BANNER_ALL = "SELECT b.banner_id, b.banner_title, b.banner_url, b.banner_icon, b.description, " +
            "b.enabled, b.deleted FROM qsr_banner b ORDER BY b.enabled DESC, b.updatetime DESC";
    private static final String BANNER_ADD = "INSERT INTO qsr_banner(banner_title, banner_url, banner_icon, " +
            "description, enabled, deleted) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DEL_BANNER = "UPDATE qsr_banner b SET b.deleted = 1 , b.enabled = 0 WHERE b.banner_id = ? ";

    @CacheAdd(timeout = 1 * 60 * 60)
    public List<Map<String,Object>> getBannerList() throws ServiceException {
        try {
            return record2list(Db.find(BANNER_SQL));
        } catch (Throwable t) {
            logger.error("getBannerList was error, exception={}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载轮播失败", t);
        }
    }

    public List<Map<String, Object>> getBannerListForManager() throws ServiceException {
        try {
            return record2list(Db.find(BANNER_ALL));
        } catch (Throwable t) {
            logger.error("getBannerListForManager was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "读取轮播图失败", t);
        }
    }

    public boolean addBanner(String title, String url, String icon, String desc, boolean enabled, boolean deleted) throws ServiceException {
        try {
            return Db.update(BANNER_ADD, title, url, icon, desc, enabled, deleted) > 0;
        } catch (Throwable t) {
            logger.error("addBanner was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "保存轮播图失败", t);
        }
    }

    public boolean modifyBanner(int bannerId, String title, String url, String icon, String desc, boolean enabled, boolean deleted) throws ServiceException {
        try {
            return Db.update(BANNER_MODIFY, title, url, icon, desc, enabled, deleted, bannerId) > 0;
        } catch (Throwable t) {
            logger.error("modifyBanner was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改轮播图失败", t);
        }
    }

    public void deleteBanner(int bannerId) throws ServiceException {
        try {
            Db.update(DEL_BANNER, bannerId);
        } catch (Throwable t) {
            logger.error("deleteBanner was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "删除轮播图失败", t);
        }
    }
}
