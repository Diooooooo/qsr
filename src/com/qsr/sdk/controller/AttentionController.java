package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.service.AttentionService;
import com.qsr.sdk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AttentionController extends WebApiController {
    private final static Logger logger = LoggerFactory.getLogger(AttentionController.class);

    public AttentionController() {
        super(logger);
    }

    public void home() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber", 1);
            int pageSize = f.i("pageSize", 10);
            String sessionkey = f.s("sessionkey");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            AttentionService attentionService = this.getService(AttentionService.class);
            PageList<Map<String, Object>> attentions = attentionService.getAttentionByUserId(pageNumber, pageSize, userId);
            this.renderData(attentions, SUCCESS);
        } catch (Throwable t) {
            this.renderException("home", t);
        }
    }

    public void addAttention() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            int typeId = f.i("type");
            int causeId = f.i("causeId");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            AttentionService attentionService = this.getService(AttentionService.class);
            attentionService.addAttentionByUserId(typeId, causeId, userId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("addAttention was error. exception = {}", t);
        }
    }

    public void delAttentionWithId() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            int attentionId = f.i("attentionId");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            AttentionService attentionService = this.getService(AttentionService.class);
            attentionService.delAttentionWithId(attentionId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("delAttention was error. exception = {}", t);
        }
    }

    public void delAttention() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey");
            int causeId = f.i("causeId");
            int typeId = f.i("type");
            UserService userService = this.getService(UserService.class);
            int userId = userService.getUserIdBySessionKey(sessionkey);
            AttentionService attentionService = this.getService(AttentionService.class);
            attentionService.delAttention(userId, typeId, causeId);
            this.renderData(SUCCESS);
        } catch (Throwable t) {
            this.renderException("delAttention was error. exception ={}", t);
        }
    }
}
