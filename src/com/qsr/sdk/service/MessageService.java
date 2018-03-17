package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.MessageHelper;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MessageService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private static final String SELECT_WAITING_ROOMS = "SELECT s.season_id, " +
            "            CONCAT(s.season_start_play_time, '-', a.team_name, ' VS ', b.team_name) AS room_name, s.self_chatroom_id, s.chatroom_id " +
            "            FROM qsr_team_season s  " +
            "            INNER JOIN qsr_team a ON s.season_team_a = a.team_id " +
            "            INNER JOIN qsr_team b ON s.season_team_b = b.team_id " +
            "            WHERE s.status_id = 1 " +
            "            AND DATE_FORMAT(s.season_start_play_time, '%H-%i') > DATE_FORMAT(NOW(), '%H-%i') + INTERVAL 60 MINUTE " +
            "            AND (s.chatroom_id = 0 OR s.self_chatroom_id = 0)";
    private static final String MODIFY_CHATROOM = "UPDATE qsr_team_season s SET s.chatroom_id = ? WHERE s.season_id = ?";
    private static final String MODIFY_SELF_CHATROOM = "UPDATE qsr_team_season s SET s.self_chatroom_id = ? WHERE s.season_id = ?";
    private static final String DELETE_CHATROOM = "UPDATE qsr_team_season s SET s.self_chatroom_id = -1, s.chatroom_id = -1 WHERE s.season_id = ?";
    private static final String SELECT_DELETE_ROOMS = "SELECT s.season_id, s.self_chatroom_id, s.chatroom_id " +
            "            FROM qsr_team_season s " +
            "            WHERE s.status_id = 4 " +
            "            AND DATE_FORMAT(s.season_start_play_time, '%H-%i') <= DATE_FORMAT(NOW(), '%H-%i') " +
            "            AND (s.chatroom_id != 0 OR s.self_chatroom_id != 0) AND (s.chatroom_id != -1 OR s.self_chatroom_id != -1)";

    @Deprecated
    public void sendMessage(String fromTo, String sendTo, String message, int type) throws ServiceException {
        try {
            MessageHelper.pushMessage(fromTo, sendTo, message, type);
        } catch (Throwable t) {
            logger.error("sendMessage was error, exception = {}, from={}, send={}, message={}, type={}", t, fromTo, sendTo, message, type);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "消息发送失败", t);
        }
    }

    public void registerUser(String name, String password, String avatar, String nickname) throws ServiceException {
        try {
            MessageHelper.registerUser(name, password, avatar, nickname);
        } catch (Throwable t) {
            logger.error("registerUser was error. exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "聊天注册失败", t);
        }
    }

    public void createChatRoom(String name, String desc, String owner) throws ServiceException {
        try {
            MessageHelper.createChatRoom(name, desc, owner);
        } catch (Throwable t) {
            logger.error("createChatRoom was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "创建聊天室失败", t);
        }
    }

    public void addChatRoomMember(int roomId, String name) throws ServiceException {
        try {
            MessageHelper.addChatRoomMember(roomId, name);
        } catch (Throwable t) {
            logger.error("addChatRoomMember was error. exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "添加聊天室成员失败", t);
        }
    }

    public void batchCreateChatRoomWithTime() throws ServiceException {
        try {
            List<Map<String, Object>> waitingRooms = record2list(Db.find(SELECT_WAITING_ROOMS));
            for (Map<String, Object> m: waitingRooms) {
                Parameter p = new Parameter(m);
                if (0 == p.l("self_chatroom_id")) {
                    Long selfId = MessageHelper.createChatRoom(p.s("room_name"), "直播室", Env.getChatRoom());
                    Db.update(MODIFY_SELF_CHATROOM, selfId, p.i("season_id"));
                }
                if (0 == p.l("chatroom_id")) {
                    Long id = MessageHelper.createChatRoom(p.s("room_name"), "公共聊天室", Env.getChatRoom());
                    Db.update(MODIFY_CHATROOM, id, p.i("season_id"));
                }
            }
        } catch (Throwable t) {
            logger.error("batchCreateChatRoomWithTime was error, exception = {} ", t);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "聊天室创建失败", t);
        }
    }

    public void batchDeleteChatRoomWithTime() throws ServiceException {
        try {
            List<Map<String, Object>> deleteRooms = record2list(Db.find(SELECT_DELETE_ROOMS));
            for (Map<String, Object> m: deleteRooms) {
                Parameter p = new Parameter(m);
                MessageHelper.deleteChatRoom(p.l("chatroom_id"));
                MessageHelper.deleteChatRoom(p.l("self_chatroom_id"));
                Db.update(DELETE_CHATROOM, p.i("season_id"));
            }
        } catch (Throwable t) {
            logger.error("batchDeleteChatRoomWithTime was error, exception = {}", t);
            throw new ServiceException(getServiceName(), ErrorCode.REPEAT_OPERATION, "聊天室删除失败", t);
        }
    }
}
