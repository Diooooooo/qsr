package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by fc on 2016-07-08.
 */
public class EntryService extends Service {

    private final static Logger logger = LoggerFactory.getLogger(EntryService.class);
    public final static String ENTRY_FLAGS = ",4,5,6,34,36,41,42,";

    @CacheAdd(timeout = 30 * 60)
    public List<Map<String, Object>> getEntryListByEntryId(int entryId, int channelId, boolean isAndroid, int version) throws ServiceException {
        return getEntryListByWhere(entryId, channelId, isAndroid, version);
    }

    @CacheAdd(timeout = 30 * 60)
    public List<Map<String, Object>> getEntryList(int channelId, boolean isAndroid, int version) throws ServiceException {
        return getEntryListByWhere(0, channelId, isAndroid, version);
    }

    private List<Map<String, Object>> getEntryListByWhere(int entryId, int channelId, boolean isAndroid, int version) throws ServiceException {
        String android = "SELECT e.entry_id entry_id, e.entry_name entry_name, IFNULL(e.icon, '') icon_path, IFNULL(e.flag_icon, '') flag_icon, t.is_branch is_branch, " +
                "IFNULL(e.doc_url, '') doc_url, IFNULL(e.h5_url, '') h5_url, IFNULL(e.news_url, '') news_url, e.update_times update_times, IFNULL(e.weex_url, '') weex_url " +
                "FROM qsr_clientui_entry e INNER JOIN qsr_clientui_entry_type t ON e.type_id = t.type_id AND e.enabled = 1 AND e.deleted = 0 AND t.enabled = 1 AND t.deleted = 0 " +
                "INNER JOIN qsr_clientui_entry_channel c ON c.entry_id = e.entry_id AND c.enabled = 1 AND c.deleted = 0 " +
                "WHERE e.parent_id = ? AND c.channel_id = ? AND c.android_version <= ? ORDER BY e.priority DESC";
        String ios = "SELECT e.entry_id entry_id, e.entry_name entry_name, IFNULL(e.icon, '') icon_path, IFNULL(e.flag_icon, '') flag_icon, t.is_branch is_branch, " +
                "IFNULL(e.doc_url, '') doc_url, IFNULL(e.h5_url, '') h5_url, IFNULL(e.news_url, '') news_url, e.update_times update_times, IFNULL(e.weex_url, '') weex_url " +
                "FROM qsr_clientui_entry e INNER JOIN qsr_clientui_entry_type t ON e.type_id = t.type_id AND e.enabled = 1 AND e.deleted = 0 AND t.enabled = 1 AND t.deleted = 0 " +
                "INNER JOIN qsr_clientui_entry_channel c ON c.entry_id = e.entry_id AND c.enabled = 1 AND c.deleted = 0 " +
                "WHERE e.parent_id = ? AND c.channel_id = ? AND c.ios_version <= ? ORDER BY e.priority DESC";
        List<Map<String, Object>> categories;
        if (isAndroid) {
            categories = record2list(Db.find(android, entryId, channelId, version));
        } else {
            categories = record2list(Db.find(ios, entryId, channelId, version));
        }
        return categories;
    }

    @Deprecated
    @CacheAdd(timeout = 30 * 60)
    public List<Map<String, Object>> getEntryInfoByEntryId(int entryId) throws ServiceException {
        String sql = "SELECT ifnull(s.item_url, '') url, ifnull(s.item_title, '') title, ifnull(s.icon, '') icon, ifnull(s.content, '') content, " +
                "s.createtime time, s.update_times FROM qsr_clientui_entry_item s " +
                "  INNER JOIN qsr_clientui_entry c ON c.entry_id = s.entry_id " +
                "  WHERE c.entry_id = ? AND s.enabled = 1 AND s.deleted = 0 ORDER BY s.priority DESC";
        logger.debug("getEntryInfoByEntryId sql={}", sql, entryId);
        List<Map<String, Object>> entities = record2list(Db.find(sql, entryId));
        return entities;
    }

    @CacheAdd(timeout = 10 * 60)
    public PageList<Map<String, Object>> getItemListByEntryId(int entryId, int pageIndex, int pageNumber, boolean isAndroid, int version, int channelId) {
        String sql2 = "SELECT IFNULL(i.item_title, '') title, IFNULL(i.item_url, '') url, IFNULL(i.content, '') content, IFNULL(i.icon, '') icon, i.createtime time, i.update_times ";
        String ios = "FROM qsr_clientui_entry e INNER JOIN qsr_clientui_entry_item i ON e.entry_id = i.entry_id AND e.enabled = 1 AND e.deleted = 0 AND i.enabled = 1 AND i.deleted = 0 " +
                "  INNER JOIN qsr_clientui_entry_channel c on c.entry_id = e.entry_id and c.enabled = 1 AND c.deleted = 0 " +
                "  WHERE e.entry_id = ? AND c.ios_version <= ? AND c.channel_id = ? ORDER BY i.priority DESC";
        String android = "FROM qsr_clientui_entry e INNER JOIN qsr_clientui_entry_item i ON e.entry_id = i.entry_id AND e.enabled = 1 AND e.deleted = 0 AND i.enabled = 1 AND i.deleted = 0 " +
                "  INNER JOIN qsr_clientui_entry_channel c on c.entry_id = e.entry_id and c.enabled = 1 AND c.deleted = 0 " +
                "  WHERE e.entry_id = ? AND c.android_version <= ? AND c.channel_id = ? ORDER BY i.priority DESC";
        if (isAndroid) {
            return page2PageList(DbUtil.paginate(pageIndex, pageNumber, sql2, android, entryId, version, channelId));
        } else {
            return page2PageList(DbUtil.paginate(pageIndex, pageNumber, sql2, ios, entryId, version, channelId));
        }
    }

    private List<Map<String, Object>> getItemListByTypeId(int typeId) {
        String sql2 = "SELECT ifnull(i.item_title, '') title, ifnull(i.item_url, '') url, ifnull(i.icon, '') icon, ifnull(i.content, '') content, i.update_times " +
                "FROM qsr_clientui_entry_item i " +
                "INNER JOIN qsr_clientui_entry e ON i.entry_id = e.entry_id AND e.enabled = 1 AND e.deleted = 0 " +
                "INNER JOIN qsr_clientui_entry_channel c ON c.entry_id = e.entry_id AND c.deleted = 0 AND c.enabled = 1 " +
                "INNER JOIN qsr_clientui_entry_item_type it ON i.type_id = it.type_id AND i.enabled = 1 AND i.deleted = 0 AND it.enabled = 1 AND it.deleted = 0 " +
                "WHERE it.type_id = ? ORDER BY i.priority DESC";
        return record2list(Db.find(sql2, typeId));
    }

    @Deprecated
    @CacheAdd(timeout = 10 * 60)
    public Map<String,Object> getItemInfoByEntryId(int entryId) {
        String sql = "SELECT ifnull(i.item_url, '') url, ifnull(i.item_title, '') title, ifnull(i.icon, '') icon, ifnull(i.content, '') content, i.createtime time, i.update_times " +
                "   FROM qsr_clientui_entry_item i INNER JOIN qsr_clientui_entry_item_type t ON i.entry_id = t.entry_id AND i.type_id = t.type_id " +
                "   INNER JOIN qsr_clientui_entry e ON i.entry_id = e.entry_id AND e.enabled = 1 AND e.deleted = 0 " +
                "   INNER JOIN qsr_clientui_entry_channel c ON c.entry_id = e.entry_id AND c.deleted = 0 AND c.enabled = 1 " +
                "   WHERE i.entry_id = ? AND i.enabled = 1 AND i.deleted = 0 AND t.enabled = 1 AND t.deleted = 0 ORDER BY i.updatetime DESC";
        Map<String, Object> info = record2map(Db.findFirst(sql, entryId));
        return info;
    }

    private List<Map<String, Object>> getTypeListByEntryId(int entryId) {
        String sql = "SELECT t.type_id entry_id, t.type_name entry_name, IFNULL(e.icon, '') icon_path, IFNULL(e.flag_icon, '') flag_icon, et.is_branch is_branck, " +
                "  IFNULL(e.doc_url, '') doc_url, IFNULL(e.h5_url, '') h5_url, IFNULL(e.news_url, '') news_url, e.update_times " +
                "  FROM qsr_clientui_entry e INNER JOIN qsr_clientui_entry_item_type t ON e.entry_id = t.entry_id AND e.enabled = 1 AND e.deleted = 0 AND t.enabled = 1 AND t.deleted = 0 " +
                "  INNER JOIN qsr_clientui_entry_channel c ON c.entry_id = e.entry_id AND c.deleted = 0 AND c.enabled = 1 " +
                "  LEFT JOIN qsr_clientui_entry_type et ON et.type_id = e.type_id AND et.enabled = 1 AND et.deleted = 0 " +
                "  WHERE e.entry_id = ? ORDER BY t.priority DESC";
        return record2list(Db.find(sql, entryId));
    }

    @CacheAdd(timeout = 30 * 60)
    public List<Map<String,Object>> getItemListByMobileType(int entryId) throws ServiceException {
        List<Map<String, Object>> entries = getTypeListByEntryId(entryId);
        entries.stream().forEach(target-> target.put("list", getItemListByTypeId(ParameterUtil.integerParam(target, "entry_id"))));
        return entries;
    }
}
