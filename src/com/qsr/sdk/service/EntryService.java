package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.ParameterUtil;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by fc on 2016-07-08.
 */
public class EntryService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(EntryService.class);
    private static final String SELECT_ENTRIES = "SELECT IFNULL(e.entry_name, u.nickname) nickname, " +
            "IFNULL(e.icon, IFNULL(u.head_img_url, '')) icon, u.id AS _id FROM qsr_clientui_entry e " +
            "INNER JOIN qsr_users u ON e.user_id = u.id WHERE e.enabled = 1 AND e.deleted = 0 ORDER BY e.priority DESC LIMIT 12";
    private static final String SELECT_ENTRIES_RESULT = "SELECT IFNULL(e.entry_name, u.nickname) nickname," +
            "IFNULL(e.icon, IFNULL(u.head_img_url, '')) icon, u.id AS _id ";
    private static final int[] TYPE = {1, 2};
    private static final String SELECT_ENTRIES_WITH_PARAM = "FROM qsr_clientui_entry e " +
            "INNER JOIN qsr_users u ON e.user_id = u.id " +
            "WHERE e.enabled = 1 AND e.deleted = 0 AND e.type_id = ? ORDER BY e.priority DESC";

    @CacheAdd(timeout = 30 * 60)
    public List<Map<String, Object>> getEntryList() throws ServiceException {
        try {
            return record2list(Db.find(SELECT_ENTRIES));
        } catch (Throwable t) {
            logger.error("getEntryList was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "大佬加载失败", t);
        }
    }

    @CacheAdd(timeout = 1 * 60 * 60)
    public List<Map<String,Object>> getEntryListWithAI(int pageNumber, int pageSize) throws ServiceException {
        return getEntryListWithParam(TYPE[0], pageNumber, pageSize);
    }

    @CacheAdd(timeout = 1 * 60 * 60)
    public List<Map<String,Object>> getEntryListWithStar(int pageNumber, int pageSize) throws ServiceException {
        return getEntryListWithParam(TYPE[1], pageNumber, pageSize);
    }

    private List<Map<String, Object>> getEntryListWithParam(int typeId, int pageNumber, int pageSize) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_ENTRIES_RESULT, SELECT_ENTRIES_WITH_PARAM, typeId));
        } catch (Throwable t) {
            logger.error("getEntryListWithParam was error, typeId = {}, exception = {}", typeId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "大佬加载失败", t);
        }
    }
}
