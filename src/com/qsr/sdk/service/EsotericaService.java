package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.ErrorCode;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EsotericaService extends Service {
    private final static Logger logger = LoggerFactory.getLogger(EsotericaService.class);
    private static final String ESOTERICA_INFO = "SELECT " +
            "IFNULL(cu.icon, u.head_img_url) head_img_url, IFNULL(cu.entry_name, u.nickname) nickname, " +
            "IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, IF(pe.pay_id, e.esoterica_detail, '') detail, IF(pe.pay_id, 1, 0) saled, cu.entry_id author, " +
            "DATE_FORMAT(e.esoterica_date, '%m-%d %H:%i') t, IFNULL(cu.description, '') _desc, " +
            "e.esoterica_price price, e.esoterica_no esoterica_id, e.status_id, e.esoterica_author, et.type_name, et.type_id, " +
            "ts.status_id ts_id, ts.status_name, IF(ua.att_id != null, 1, 0) is_attention " +
            "FROM qsr_team_season_esoterica e " +
            "INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "INNER JOIN (SELECT ? AS userId, ? as eNo) i " +
            "LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = i.userId " +
            "LEFT JOIN qsr_pay_esoterica pe ON pe.status_id = 1 AND pe.esoterica_no = e.esoterica_no AND pe.user_id = i.userId " +
            "WHERE e.esoterica_no = i.eNo AND e.enabled = 1 ";
    private static final String ESOTERICA_SELECT_LIST_V2 = "SELECT IFNULL(cu.icon, u.head_img_url) head_img_url, IFNULL(cu.entry_name, u.nickname) nickname, " +
            "  IFNULL(e.esoterica_title, '') title, IFNULL(e.esoterica_intro, '') intro, cu.entry_id author, " +
            "  DATE_FORMAT(e.esoterica_date, '%m-%d %H:%i') t, IFNULL(cu.description, '') _desc, " +
            "  e.esoterica_price price, e.esoterica_no esoterica_id, e.status_id, e.esoterica_author, et.type_name, et.type_id, " +
            "  ts.status_id ts_id, ts.status_name, IF(ua.att_id != null, 1, 0) is_attention ";
    private static final String ESOTERICA_FROM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND e.is_open = 1 AND e.type_id = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_ITEMS_WITH_PARAM = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_team_season_esoterica_item i ON i.esoterica_id = e.esoterica_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE e.enabled = 1 " +
            "  GROUP BY i.esoterica_id " +
            "  HAVING COUNT(i.esoterica_id) = ? " +
            "  ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_FROM_SPORTTERY = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_team_season_esoterica_item i ON i.esoterica_id = e.esoterica_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE e.enabled = 1 AND e.type_id IN (?) AND e.status_id = 1 " +
            "  GROUP BY i.esoterica_id " +
            "  ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_FROM_WITH_LEAGUE_ID = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season s ON s.season_id = e.sea_id " +
            "  INNER JOIN qsr_team t ON t.team_id = s.season_team_a " +
            "  INNER JOIN qsr_team b ON b.team_id = s.season_team_b " +
            "  INNER JOIN qsr_league l ON l.lea_id = s.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_league_country c ON l.country_id = c.country_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE c.country_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC ";
    private static final String ESOTERICA_USER = "  FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE e.esoterica_author = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SEASON = "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE i.season_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SEASON_TYPE = "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE i.season_id = ? AND e.type_id = ? " +
            "  AND e.enabled = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_TOP = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "  WHERE e.top = 1 " +
            "  AND e.enabled = 1 AND e.status_Id = 1 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_HISTORY = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id != 1 AND e.esoterica_author = ? AND e.esoterica_date < now() " +
            "AND e.createtime < now()" +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_ITEM = "SELECT l.lea_name n, a.team_name a, a.team_icon a_icon, b.team_name b, b.team_icon b_icon, " +
            "  DATE_FORMAT(s.season_start_play_time, '%m-%d %H:%i') pt, IFNULL(sl.lottery_win, '') l_w, IFNULL(sl.lottery_deuce, '') l_d, IFNULL(sl.lottery_lose, '') l_l," +
            "  IFNULL(sl.final_win, '') f_w, IFNULL(sl.final_deuce, '') f_d, IFNULL(sl.final_lose, '') f_l, s.status_id, s.season_fs_a, s.season_fs_b " +
            "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season_esoterica e on e.esoterica_id = i.esoterica_id " +
            "  INNER JOIN qsr_team_season s ON i.season_id = s.season_id " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "  INNER JOIN qsr_league l ON s.lea_id = l.lea_id AND l.enabled = 1 " +
            "  LEFT JOIN qsr_team_season_lottery sl ON sl.season_id = s.season_id AND sl.type_id = 9 " +
            "WHERE e.esoterica_no = ? ORDER BY s.season_start_play_time ASC LIMIT ? ";
    private static final int LIMIT = 10;
    private static final String ESOTERICA_STAR_CONTINUE = "SELECT " +
            "COUNT(CASE WHEN e.status_id = 2 THEN e.status_id END) star, " +
            "COUNT(CASE WHEN e.status_id = 3 THEN e.status_id END) _continue FROM qsr_team_season_esoterica e " +
            "WHERE e.esoterica_author = ? AND e.status_id IN (2, 3) " +
            "LIMIT ?";
    private static final String ESOTERICA_SPOTTERY = "SELECT s.sporttery_issue issue FROM qsr_team_season_sporttery s " +
            "WHERE SUBSTRING_INDEX(s.sporttery_issue, 0, 1) = DATE_FORMAT(NOW(), '%y') " +
            "GROUP BY s.sporttery_issue " +
            "ORDER BY s.sporttery_issue DESC";
    private static final String ESOTERICA_ISSUE = "SELECT ts.season_id, l.lea_name, " +
            "  ts.season_start_play_time start_time, a.team_name a_name, b.team_name b_name " +
            "  FROM qsr_team_season_sporttery s " +
            "  INNER JOIN qsr_team_season ts ON s.season_id = ts.season_id " +
            "  INNER JOIN qsr_league l ON ts.lea_id = l.lea_id AND l.enabled = 1 " +
            "  INNER JOIN qsr_team a ON ts.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON ts.season_team_b = b.team_id " +
            "WHERE s.sporttery_issue = ? ";
    private static final String ESOTERICA_LOTTERY = "SELECT l.final_win, l.final_deuce, l.final_lose " +
            "FROM qsr_team_season_lottery l WHERE l.season_id = ? AND l.type_id = 9";
    private static final String ESOTERICA_HOT = " FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND e.esoterica_author = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_WITH_ISSUE = " FROM qsr_team_season_sporttery s " +
            "  INNER JOIN qsr_team_season_esoterica_item i ON s.season_id = i.season_id " +
            "  INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND s.sporttery_issue = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_WITH_TYPE_AND_ISSUE = "  FROM qsr_team_season_sporttery s " +
            "INNER JOIN qsr_team_season_esoterica_item i ON s.season_id = i.season_id " +
            "INNER JOIN qsr_team_season_esoterica e ON i.esoterica_id = e.esoterica_id " +
            "INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND s.sporttery_issue = ? AND e.type_id = ? " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String ESOTERICA_SPORTTERY_INFO = "SELECT IFNULL(u.head_img_url, e.icon) head_img_url, " +
            "IFNULL(e.entry_name, u.nickname) nickname, " +
            "IFNULL(e.description, '') _desc, u.id _id, IF(ua.att_id IS NULL, 0, 1) is_attention " +
            "  FROM qsr_clientui_entry e " +
            "  INNER JOIN qsr_users u ON e.user_id = u.id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = u.id AND ua.status_id = 1 AND ua.type_id = 3 AND ua.user_id = ? " +
            "  WHERE e.user_id = ?";
    private static final String SELECT_ESOTERICA_LIST = "SELECT pe.order_number, e.esoterica_no, e.esoterica_title, e.esoterica_price, pe.createtime, s.status_name esotericaStatus, es.status_name payStatus, es.status_id ";
    private static final String ESOTERICA_FROM_PAY_USER = "FROM qsr_pay_esoterica pe " +
            "INNER JOIN qsr_pay_esoterica_status es ON pe.status_id = es.status_id " +
            "INNER JOIN qsr_team_season_esoterica e ON pe.esoterica_no = e.esoterica_no " +
            "INNER JOIN qsr_team_season_esoterica_status s ON s.status_id = e.status_id " +
            "WHERE pe.user_id = ? AND pe.enabled = 1 ORDER BY pe.createtime DESC ";
    private static final String ESOTERICA_FROM_PAY_USER_TYPE = "FROM qsr_pay_esoterica pe " +
            "INNER JOIN qsr_pay_esoterica_status es ON pe.status_id = es.status_id " +
            "INNER JOIN qsr_team_season_esoterica e ON pe.esoterica_no = e.esoterica_no " +
            "INNER JOIN qsr_team_season_esoterica_status s ON s.status_id = e.status_id " +
            "WHERE pe.user_id = ? AND pe.status_id = ? AND pe.enabled = 1 ORDER BY pe.createtime DESC ";
    private static final String REPAY_ESOTERICA = "UPDATE qsr_pay_esoterica e " +
            "  INNER JOIN qsr_users u ON u.id = e.user_id " +
            "  SET e.status_id = 1 WHERE e.user_id = ? AND e.order_number = ? AND e.enabled = 1 AND e.status_id = 2";
    private static final String DEL_ESOTERICA = "UPDATE qsr_pay_esoterica e " +
            "  INNER JOIN qsr_users u ON e.user_id = u.id " +
            "  SET e.enabled = 0 WHERE u.id = ? AND e.order_number = ? AND e.enabled = 1 ";
    private static final String CANCEL_ESOTERICA = "UPDATE qsr_pay_esoterica e " +
            "  INNER JOIN qsr_users u ON u.id = e.user_id " +
            "  SET e.status_id = 3 WHERE u.id = ? AND e.order_number = ? AND e.enabled = 1 and e.status_id = 2";
    private static final String REPAY_BALANCE_LOG = "INSERT qsr_user_balance_log(user_id, type, " +
            "  currency_type_id, income, block, balance, description, cause_id, cause_type_id) " +
            "  SELECT i.userId, 1, 3, 0 - se.esoterica_price, b.block, b.balance - se.esoterica_price, i.description, i.causeId, i.causeTypeId " +
            "  FROM (SELECT ? AS userId, ? AS orderNumber, ? AS description, ? AS causeId, ? AS causeTypeId) i " +
            "  INNER JOIN qsr_users u ON i.userId = u.id " +
            "  INNER JOIN qsr_pay_esoterica e ON e.order_number = i.orderNumber " +
            "  INNER JOIN qsr_team_season_esoterica se ON se.esoterica_no = e.esoterica_no " +
            "  INNER JOIN qsr_user_balance b ON u.id = b.user_id AND b.currency_type_id = 3";
    private static final String REPAY_BALANCE = "UPDATE qsr_user_balance b " +
            "INNER JOIN (SELECT ? AS userId, ? AS orderNumber) i ON b.user_id = i.userId AND b.currency_type_id = 3 " +
            "INNER JOIN qsr_pay_esoterica pe ON pe.order_number = i.orderNumber AND pe.user_id = i.userId " +
            "AND pe.enabled = 1 AND pe.status_id = 1 " +
            "INNER JOIN qsr_team_season_esoterica se ON se.esoterica_no = pe.esoterica_no " +
            "SET b.balance = b.balance - se.esoterica_price ";
    private static final String ESOTERICA_FREE = "FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status ts ON e.status_id = ts.status_id " +
            "  INNER JOIN qsr_team_season_esoterica_type et ON e.type_id = et.type_id " +
            "  INNER JOIN qsr_users u ON e.esoterica_author = u.id " +
            "  INNER JOIN qsr_clientui_entry cu ON e.esoterica_author = cu.user_id " +
            "  LEFT JOIN qsr_users_attention ua ON ua.target_id = e.esoterica_id AND ua.status_id = 1 AND ua.type_id = 1 AND ua.user_id = ? " +
            "WHERE e.enabled = 1 AND e.status_id = 1 AND e.is_open = 1 AND e.esoterica_price = 0 " +
            "ORDER BY e.stick DESC, e.createtime DESC";
    private static final String EOSTERICA_INFO = "SELECT e.esoterica_no, e.esoterica_title title, e.esoterica_price*100 price, e.currency_type_id type_id " +
            "FROM qsr_team_season_esoterica e WHERE e.esoterica_no = ? AND e.enabled = 1 AND e.status_id = 1";
    private static final String ESOTERICA_WITH_SEASON_ID = "SELECT i.item_id FROM qsr_team_season_esoterica_item i WHERE i.season_id = ?";
    private static final String ESOTERICA_ALL = "SELECT e.esoterica_id, e.esoterica_no, e.esoterica_title, " +
            "  e.esoterica_intro, e.esoterica_date, e.esoterica_price, s.status_name, s.status_id " +
            "  FROM qsr_team_season_esoterica e " +
            "  INNER JOIN qsr_team_season_esoterica_status s ON s.status_id = e.status_id " +
            "  ORDER BY e.esoterica_date DESC, e.top DESC";
    private static final String ADD_ESOTERICA = "INSERT INTO qsr_team_season_esoterica" +
            "(type_id,esoterica_no,esoterica_title,esoterica_intro,esoterica_detail" +
            " ,esoterica_author,esoterica_date,is_open,esoterica_price,esoterica_tag,enabled,top" +
            ") SELECT i.typeId, MD5(NOW()), i.title, i.intro, i.detail, " +
            "i.author, NOW(), i.isOpen, i.price, i.tag, i.enabled, i.top " +
            "FROM (SELECT ? AS typeId, ? AS title, ? AS intro, ? AS detail, ? AS author, ? AS isOpen, ? AS price, ? AS tag, ? AS enabled, ? AS top) i";
    private static final String ADD_ESOTERICA_SEASON = "INSERT INTO qsr_team_season_esoterica_item(esoterica_id, season_id) VALUES (?, ?)";
    private static final String MODIFY_ESOTERICA = "UPDATE qsr_team_season_esoterica e " +
            "  INNER JOIN (SELECT ? AS enabled, ? AS top, ? AS open, ? AS e_id, ? AS price) i " +
            "  SET e.esoterica_price = IFNULL(i.price, e.esoterica_price), e.enabled = IFNULL(i.enabled, e.enabled), " +
            "  e.top = IFNULL(i.top, e.top), e.is_open = IFNULL(i.open, e.is_open) " +
            "  WHERE e.esoterica_id = i.e_id";
    private static final String ESOTERICA_TYPE = "SELECT t.type_id, t.type_name, t.descrption " +
            "FROM qsr_team_season_esoterica_type t WHERE t.enabled = 1";
    private static final String ESOTERICA_SEASON_ITEM = "SELECT i.season_id, l.lea_name, t.type_name, " +
            "  s.season_gameweek, a.team_name a_name, b.team_name, s.season_start_play_time play_time " +
            "  FROM qsr_team_season_esoterica_item i " +
            "  INNER JOIN qsr_team_season s ON i.season_id = s.season_id " +
            "  INNER JOIN qsr_league l ON s.lea_id = l.lea_id " +
            "  INNER JOIN qsr_team_season_type t ON s.type_id = t.type_id " +
            "  INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "  INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            " WHERE i.esoterica_id = ? ";
    private static final String ESOTERICA_WITH_SEASON_ID_V2 = "SELECT e.esoterica_no FROM qsr_team_season_esoterica e " +
            "INNER JOIN qsr_team_season_esoterica_item i ON e.esoterica_id = i.esoterica_id " +
            "WHERE i.season_id = ? " +
            "GROUP BY i.esoterica_id " +
            "HAVING COUNT(i.esoterica_id) = 1 " +
            "ORDER BY e.esoterica_date DESC, e.esoterica_author ASC";

    public PageList<Map<String, Object>> getEsotericaListByLeagueId(int pageNumber, int pageSize, int leagueId, int userId)
            throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize,
                    ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM_WITH_LEAGUE_ID, userId, leagueId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByLeagueId was error. leagueId = {}, exception = {}", leagueId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public Map<String,Object> getEsotericaInfo(String esotericaId, int userId) throws ServiceException {
        try {
            Record r = Db.findFirst(ESOTERICA_INFO, userId, esotericaId);
            if (null == r)
                throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "未查询到相关锦囊",
                        new NullPointerException());
            getItemRecord(r);
            return record2map(r);
        } catch (Throwable t) {
            logger.error("getEsotericaInfo was error. esotericaId = {}, exception = {}", esotericaId, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public Map<String, Object> getEsotericaInfo(String esotericaNo) throws ServiceException {
        try {
            return record2map(Db.findFirst(EOSTERICA_INFO, esotericaNo));
        } catch (Throwable t) {
            logger.error("getEsotericaInfo was error. esotericaId = {}, exception = {}", esotericaNo, t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载详情失败", t);
        }
    }

    @CacheAdd(timeout = 3 * 60)
    public PageList<Map<String, Object>> getEsotericaHistoryWithPage(int pageNumber, int pageSize, int typeId, int userId)
            throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM, userId, typeId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaHistory was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    @CacheAdd(timeout = 3 * 60)
    public PageList<Map<String,Object>> getEsotericaListByUserId(int pageNumber, int pageSize, int authorityId, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_USER, userId, authorityId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByUserId was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    @CacheAdd(timeout = 3 * 60)
    public PageList<Map<String,Object>> getEsotericaListBySeasonId(int pageNumber, int pageSize, int seasonId, int typeId, int userId) throws ServiceException {
        try {
            Page<Record> pr;
            if (-1 == typeId)
                pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_SEASON, userId, seasonId);
            else
                pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_SEASON_TYPE, userId, seasonId, typeId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListBySeasonId was error, exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    @CacheAdd(timeout = 5 * 60)
    public PageList<Map<String,Object>> getEsotericaTop(int pageNumber, int pageSize, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_TOP, userId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaTop was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    @CacheAdd(timeout = 30 * 60)
    public PageList<Map<String,Object>> getEsotericaHistoryWithAuthorityPrev(int pageNumber, int pageSize, int userId, int author) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_HISTORY, userId, author);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaHistoryWithAuthorityPrev was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListByParam(int pageNumber, int pageSize, int num, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_ITEMS_WITH_PARAM, userId, num);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByParam was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListBySporttery(int pageNumber, int pageSize, String type, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_FROM_SPORTTERY, userId, type);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListBySporttery was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaSportteries() throws ServiceException {
        try {
            return record2list(Db.find(ESOTERICA_SPOTTERY));
        } catch (Throwable t) {
            logger.error("getEsotericaSportteries was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载期数失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaListByIssue(String number) throws ServiceException {
        try {
            List<Map<String, Object>> ls = record2list(Db.find(ESOTERICA_ISSUE, number));
            for (Map<String, Object> r: ls) {
                Record sr = Db.findFirst(ESOTERICA_LOTTERY, r.get("season_id"));
                if (null != sr) {
                    r.put("w", sr.get("final_win"));
                    r.put("d", sr.get("final_deuce"));
                    r.put("l", sr.get("final_lose"));
                } else {
                    r.put("w", "0");
                    r.put("d", "0");
                    r.put("l", "0");
                }
            }
            return ls;
        } catch (Throwable t) {
            logger.error("getEsotericaListByIssue was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaListByHot(int pageNumber, int pageSize, int sportteryId, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_HOT, userId, sportteryId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaListByHot was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    private void getStatistics(Page<Record> pr) {
        for (Record r: pr.getList()) {
            getItemRecord(r);
//            r.remove("esoterica_id");
            r.remove("status_id");
            r.remove("esoterica_author");
        }
    }

    private void getItemRecord(Record r) {
        List<Record> lr = Db.find(ESOTERICA_ITEM, r.get("esoterica_id"), LIMIT);
        Record scr = Db.findFirst(ESOTERICA_STAR_CONTINUE, r.get("esoterica_author"), LIMIT);
        String rate = "0";
        r.set("item", record2list(lr));
        r.set("star", scr.get("star"));
        r.set("_continue", scr.get("_continue"));
        r.set("rate", rate);
        r.set("limit", lr.size());
    }

    public List<Map<String,Object>> getEsotericaListWithIssue(String issue, int userId) throws ServiceException {
        try {
            List<Record> ls = Db.find(ESOTERICA_SELECT_LIST_V2 + ESOTERICA_WITH_ISSUE, userId, issue);
            for (Record r: ls) {
                getItemRecord(r);
            }
            return record2list(ls);
        } catch (Throwable t) {
            logger.error("getEsotericaListWithIssue was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public PageList<Map<String,Object>> getEsotericaWithTypeAndIssue(int pageNumber, int pageSize, String issue, int typeId, int userId) throws ServiceException {
        try {
            Page<Record> pr = DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_WITH_TYPE_AND_ISSUE, userId, issue, typeId);
            getStatistics(pr);
            return page2PageList(pr);
        } catch (Throwable t) {
            logger.error("getEsotericaWithTypeAndIssue was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载锦囊失败", t);
        }
    }

    public Map<String,Object> getEsotericaSportteryInfo(int id, int userId) throws ServiceException {
        try {
            return record2map(Db.findFirst(ESOTERICA_SPORTTERY_INFO, userId, id));
        } catch (Throwable t) {
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载专家信息失败", t);
        }
    }

    public PageList<Map<String, Object>> getEsotericaListWithPayUser(int userId, int typeId, int pageNumber, int pageSize) throws ServiceException {
        try {
            PageList<Map<String, Object>> ls;
            if (typeId == 0)
                ls = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_ESOTERICA_LIST,
                        ESOTERICA_FROM_PAY_USER, userId));
            else
                ls = page2PageList(DbUtil.paginate(pageNumber, pageSize, SELECT_ESOTERICA_LIST,
                        ESOTERICA_FROM_PAY_USER_TYPE, userId, typeId));
            return ls;
        } catch (Throwable t) {
            logger.error("getEsotericaListWithPayUser was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载已购列表失败", t);
        }
    }

    public boolean repayEsoterica(int userId, String esotericaId) throws ServiceException {
        try {
            long[] repay = {0};
            return Db.tx(() -> DbUtil.update(REPAY_ESOTERICA, repay, userId, esotericaId) > 0
                      && Db.update(REPAY_BALANCE_LOG, userId, esotericaId, "购买锦囊", repay[0], 2) > 0
                      && Db.update(REPAY_BALANCE, userId, esotericaId) > 0
            );
        } catch (Throwable t) {
            logger.error("repayEsoterica was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "购买锦囊失败", t);
        }
    }

    public boolean delEsoterica(int userId, String esotericaId) throws ServiceException {
        try {
            return Db.update(DEL_ESOTERICA, userId, esotericaId) > 0;
        } catch (Throwable t) {
            logger.error("delEsoterica was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "删除锦囊订单失败", t);
        }
    }

    public boolean cancelEsoterica(int userId, String esotericaId) throws ServiceException {
        try {
            return Db.update(CANCEL_ESOTERICA, userId, esotericaId) > 0;
        } catch (Throwable t) {
            logger.error("cancelEsoterica was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "取消锦囊订单失败", t);
        }
    }

    public PageList<Map<String, Object>> getFreeEsotericaList(int pageNumber, int pageSize, int userId) throws ServiceException {
        try {
            return page2PageList(DbUtil.paginate(pageNumber, pageSize, ESOTERICA_SELECT_LIST_V2, ESOTERICA_FREE, userId));
        } catch (Throwable t) {
            logger.error("getFreeEsotericaList was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载免费锦囊失败", t);
        }
    }

    public Map<String,Object> getEsotericaInfoWithSeasonId(int seasonId) throws ServiceException {
        try {
            return record2map(Db.findFirst(ESOTERICA_WITH_SEASON_ID, seasonId));
        } catch (Throwable t) {
            logger.error("getEsotericaInfoWithSeasonid was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "获取专家列表失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaListForManager() throws ServiceException {
        try {
            return record2list(Db.find(ESOTERICA_ALL));
        } catch (Throwable t){
            logger.error("getEsotericaListForManager was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "获取锦囊列表失败", t);
        }
    }

    public boolean addEsoterica(int entryId, String seasonId, String title, String intro, String detail, int price,
                                boolean top, boolean enabled, boolean open, String tag, int typeId) throws ServiceException {
        try {
            int[] ids={0};
            DbUtil.update(ADD_ESOTERICA, ids, typeId, title, intro, detail, entryId, open, price, tag, enabled, top);
            for (String s: seasonId.split(",")) {
                Db.update(ADD_ESOTERICA_SEASON, ids[0], s);
            }
            return true;
        } catch (Throwable t) {
            logger.error("addEsoterica was error. params = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "保存锦囊失败", t);
        }
    }

    public boolean modifyEsoterica(int id, boolean enabled, boolean open, boolean top, int price) throws ServiceException {
        try {
            return Db.update(MODIFY_ESOTERICA, enabled, open, top, id, price) > 0;
        } catch (Throwable t) {
            logger.error("modifyEsoterica was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.DATA_SAVA_FAILED, "修改锦囊失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaType() throws ServiceException {
        try {
            return record2list(Db.find(ESOTERICA_TYPE));
        } catch (Throwable t) {
            logger.error("getEsotericaType was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载类型失败", t);
        }
    }

    public List<Map<String,Object>> getEsotericaSeasonItem(int id) throws ServiceException {
        try {
            return record2list(Db.find(ESOTERICA_SEASON_ITEM, id));
        } catch (Throwable t) {
            logger.error("getEsotericaSeasonItem was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "加载赛程失败", t);
        }
    }

    public Map<String,Object> getEsotericaWithSeasonId(String seasonId) throws ServiceException {
        try {
            return record2map(Db.findFirst(ESOTERICA_WITH_SEASON_ID_V2, seasonId));
        } catch (Throwable t) {
            logger.error("getEsotericaWithSeasonId was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.LOAD_FAILED_FROM_DATABASE, "查询锦囊失败", t);
        }
    }
}
