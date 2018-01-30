package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.MessageService;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static ExecutorService executors = Executors.newFixedThreadPool(100);

    public MessageController() {
        super(logger);
    }

    @Deprecated
    public void sendMessage() {
        try {
            Fetcher f = this.fetch();
            String sendTo = f.s("send_to");
            String fromTo = f.s("from");
            String message = f.s("msg");
            String type = f.s("type", StringUtil.EMPTY_STRING);
            MessageService messageService = this.getService(MessageService.class);
            messageService.sendMessage(fromTo, sendTo, message, 1);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("sendMessage", t);
        }
    }

    public void registerUser() {
        try {
            Fetcher f = this.fetch();
            String name = f.s("name");
            String password = name;
            String avatar = f.s("avatar");
            String nickname = f.s("nickname");
            MessageService messageService = this.getService(MessageService.class);
            messageService.registerUser(name, password, avatar, nickname);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("registerUser", t);
        }
    }

    public void createChatRoom() {
        try {
            Fetcher f = this.fetch();
            String name = f.s("name");
            String desc = f.s("desc");
            String owner = f.s("owner");
            MessageService messageService = this.getService(MessageService.class);
            messageService.createChatRoom(name, desc, owner);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("createChatRoom", t);
        }
    }

    public void batchCreateChatRoomWithTime() {
        try {
            Fetcher f = this.fetch();
            String managerPwd = f.s("manager");
            if (Env.getManagementPassword().equalsIgnoreCase(managerPwd)) {
                MessageService messageService = this.getService(MessageService.class);
                executors.execute(() -> {
                    try {
                        messageService.batchCreateChatRoomWithTime();
                        messageService.batchDeleteChatRoomWithTime();
                    } catch (ServiceException e) {
                        logger.error("batchCreateChatRoomWithTime was error, exception = {}", e);
                    }
                });
                this.renderData();
            } else {
                logger.error("batchCreateChatRoomWithTime was failed, real ip = {} ", getRealRemoteAddr());
                Map<String, Object> info = new HashMap<>();
                info.put("message", "没有权限，请联系管理员");
                this.renderData(info);
            }
        } catch (Throwable t) {
            this.renderException("batchCreateChatRoomWithTime", t);
        }
    }

    @Deprecated
    public void addChatRoomMember() {
        try {
            Fetcher f = this.fetch();
            int roomId = f.i("room_id");
            String name = f.s("name");
            MessageService messageService = this.getService(MessageService.class);
            messageService.addChatRoomMember(roomId, name);
            this.renderData();
        } catch (Throwable t) {
            this.renderException("addChatRoomMember", t);
        }
    }

}
