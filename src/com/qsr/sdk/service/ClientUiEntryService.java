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
    private static final String SELECT_ENTRIES_ALL = "SELECT e.entry_id, e.entry_name, e.icon, e.description, t.type_name FROM qsr_clientui_entry e " +
            "  INNER JOIN qsr_clientui_entry_type t ON e.type_id = t.type_id " +
            "  WHERE e.enabled = 1 AND e.deleted = 0 ORDER BY  e.priority DESC";

    public List<Map<String,Object>> getEntries(int code, String platform) throws ServiceException {
        try {
            return record2list(Db.find(SELECT_ENTRIES, code, platform));
        } catch (Throwable t) {
            logger.error("getEntries was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载菜单失败", t);
        }
    }

    public List<Map<String, Object>> getEntries() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_ENTRIES_ALL));
        } catch (Throwable t){
            logger.error("getEntries was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载专家失败", t);
        }
    }
}
