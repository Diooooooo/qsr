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
    private final static String SELECT_ATTENTION_WITH_SEASON = "SELECT IF(a.type_id = 1, l.lea_name, IF(a.type_id = 2, ts.sports_name, IF(a.type_id = 3, t.team_name, ''))) name_, " +
            "  IF(a.type_id = 1, '', IF(a.type_id = 2, ts.sports_img, IF(a.type_id = 3, t.team_icon, ''))) img, " +
            "  a.att_id, a.type_id, IF(a.type_id = 1, ta.team_name, '') team_a, IF(a.type_id = 1, tb.team_name, '') team_b, IF(a.type_id = 1, s.season_fs_a, 0) fs_a, IF(a.type_id = 1, s.season_fs_b, 0) fs_b, " +
            "  IF(a.type_id = 1, ta.team_icon, '') team_a_img, IF(a.type_id = 1, tb.team_icon, '') team_b_img, " +
            "  IF(a.type_id = 1, s.season_start_play_time, '') start_time";
    private final static String FROM_ATTENTION_WITH_SEASON = "  FROM qsr_users_attention a " +
            "  LEFT JOIN qsr_team t ON a.target_id = t.team_id " +
            "  LEFT JOIN qsr_team_season s ON s.season_id = a.target_id " +
            "  LEFT JOIN qsr_team_sportsman ts ON a.target_id = ts.sports_id " +
            "  LEFT JOIN qsr_league l ON s.lea_id = l.lea_id AND l.enabled = 1 " +
            "  LEFT JOIN qsr_team ta ON s.season_team_a = ta.team_id " +
            "  LEFT JOIN qsr_team tb ON s.season_team_b = tb.team_id " +
            "  WHERE a.user_id = ? AND a.status_id = 1 " +
            "ORDER BY a.createtime DESC";
    private static final String ADD_ATTENTION = "INSERT INTO qsr_users_attention(target_id, user_id, type_id) " +
            "  SELECT i.target_id, i.user_id, i.type_id FROM (SELECT ? AS status_id, ? AS target_id, ? AS user_id, ? AS type_id) i " +
            " ON DUPLICATE KEY UPDATE status_id = i.status_id";
    private static final String DEL_ATTENTION_WITH_ID = "UPDATE qsr_users_attention a SET a.status_id = ? WHERE a.att_id = ?";
    private static final String DEL_ATTENTION = "INSERT INTO qsr_users_attention(target_id, user_id, type_id) " +
            "  SELECT i.target_id, i.user_id, i.type_id FROM (SELECT ? AS status_id, ? AS target_id, ? AS user_id, ? AS type_id) i " +
            " ON DUPLICATE KEY UPDATE status_id = i.status_id";

    private static final int[] STATUS_ID = {1, 2};

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
            Db.update(ADD_ATTENTION, STATUS_ID[0], causeId, userId, typeId);
        } catch (Throwable t) {
            logger.error("addAttentionByUserId was error. typeId = {} causeId = {} userId = {} exception = {}", typeId, causeId, userId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "关注失败", t);
        }
    }

    public void delAttentionWithId(int attentionId) throws ServiceException {
        try {
            Db.update(DEL_ATTENTION_WITH_ID, STATUS_ID[1], attentionId);
        } catch (Throwable t) {
            logger.error("delAttention was error. attentionId = {} exception = {} ", attentionId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取关失败", t);
        }
    }

    public void delAttention(int userId, int typeId, int causeId) throws ServiceException {
        try {
            Db.update(DEL_ATTENTION, STATUS_ID[1], causeId, userId, typeId);
        } catch (Throwable t) {
            logger.error("delAttention was error. userId={}, typeId={}, causeId={}, exception={}", userId, typeId, causeId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取关失败", t);
        }
    }
}
