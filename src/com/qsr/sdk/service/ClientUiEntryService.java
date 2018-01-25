package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ClientUiEntryService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(ClientUiEntryService.class);
    private static final String SELECT_ENTRIES = "";

    public List<Map<String,Object>> getEntries(int code, String platform) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_ENTRIES, code, platform));
        } catch (Throwable t) {
            logger.error("getEntries was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载菜单失败", t);
        }
    }
}
