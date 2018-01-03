package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AttentionService extends Service {
    private final static Logger logger = LoggerFactory.getLogger(AttentionService.class);
    private final static String SELECT_ATTENTION_WITH_SEASON = "SELECT ta.team_name team_a, IFNULL(ta.team_icon, \"\") a_icon, \n" +
            "            tb.team_name team_b, IFNULL(tb.team_icon, \"\") b_icon, l.lea_name, \n" +
            "            DATE_FORMAT(s.season_start_play_time, \"%H:%m\") play_time, \n" +
            "            s.season_gameweek gameweek, s.season_fs_a source_a, s.season_fs_b source_b, \n" +
            "            ss.status_name, s.season_id, DATE_FORMAT(s.season_start_play_time, \"%y\") play_year,\n" +
            "            DATE_FORMAT(s.season_start_play_time, \"%m-%d\") play_month";
    private final static String SELECT_ATTENTION_WITH_SPORTSMAN = "";
    private final static String SELECT_ATTENTION_WITH_TEAM = "";
    private final static String SELECT_ATTENTION_WITH_MOSTER = "";
    private final static String FROM_ATTENTION_WITH_SEASON = "FROM qsr_users_attention a \n" +
            "INNER JOIN qsr_users_attention_type t ON a.type_id = t.type_id \n" +
            "INNER JOIN qsr_team_season s ON s.season_id = a.target_id\n" +
            "INNER JOIN qsr_league l ON l.lea_id = s.lea_id\n" +
            "INNER JOIN qsr_team_season_status ss ON ss.status_id = s.status_id\n" +
            "LEFT JOIN qsr_team ta ON ta.team_id = s.season_team_a\n" +
            "LEFT JOIN qsr_team tb ON tb.team_id = s.season_team_b\n" +
            "  WHERE a.user_id =?";
    private final static String FROM_ATTENTION_WITH_TEAM = "";
    private final static String FROM_ATTENTION_WITH_MOSTER = "";
    private final static String FROM_ATTENTION_WITH_SPORTSMAN= "";
    private static final String ADD_ATTENTION = "";
    private static final String DEL_ATTENTION_WITH_ID = "";
    private static final String DEL_ATTENTION = "";

    public PageList<Map<String, Object>> getAttentionByUserId(int pageNumber, int pageSize, int userId) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_ATTENTION_WITH_SEASON, FROM_ATTENTION_WITH_SEASON, userId));
        } catch (Throwable t) {
            logger.error("getAttentionByUserId was error. userId = {}, exception = {}", userId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "关注加载失败", t);
        }
    }

    public void addAttentionByUserId(int typeId, int causeId, int userId) throws ServiceException {
        try {
            Db.update(ADD_ATTENTION, typeId, causeId, userId);
        } catch (Throwable t) {
            logger.error("addAttentionByUserId was error. typeId = {} causeId = {} userId = {}", typeId, causeId, userId);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "关注失败", t);
        }
    }

    public void delAttentionWithId(int attentionId, int userId) throws ServiceException {
        try {
            Db.update(DEL_ATTENTION_WITH_ID, attentionId, userId);
        } catch (Throwable t) {
            logger.error("delAttention was error. attentionId = {}, userId = {}", attentionId, userId);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取关失败", t);
        }
    }

    public void delAttention(int userId, int typeId, int causeId) throws ServiceException {
        try {
            Db.update(DEL_ATTENTION, userId, typeId, causeId);
        } catch (Throwable t) {
            logger.error("delAttention was error. userId={}, typeId={}, causeId={}, exception={}", userId, typeId, causeId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取关失败", t);
        }
    }
}
