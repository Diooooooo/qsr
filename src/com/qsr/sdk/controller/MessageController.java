package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.MessageService;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

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
