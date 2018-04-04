package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.MessageService;
import com.qsr.sdk.service.SeasonService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.ErrorCode;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternalController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(InternalController.class);
    private static ExecutorService executors = Executors.newFixedThreadPool(100);

    public InternalController() {
        super(logger);
    }

    public void getPlayingSeason() {
        try {
            Fetcher f = this.fetch();
            String pwd=  f.s("manager");
            if (Env.getManagementPassword().equals(pwd)){
                SeasonService seasonService = this.getService(SeasonService.class);
                this.renderData(seasonService.getPlayingSeasons());
            } else {
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "未有调用此接口的权限，请联系管理员");
            }
        } catch (Throwable t){
            this.renderException("getPlayingSeason", t);
        }
    }

    public void getOldSeason() {
        try {
            Fetcher f = this.fetch();
            String pwd=  f.s("manager");
            if (Env.getManagementPassword().equals(pwd)){
                SeasonService seasonService = this.getService(SeasonService.class);
                this.renderData(seasonService.getOldSeasons());
            } else {
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "未有调用此接口的权限，请联系管理员");
            }
        } catch (Throwable t){
            this.renderException("getOldSeason", t);
        }
    }

    public void getFutureSeason() {
        try {
            Fetcher f = this.fetch();
            String pwd = f.s("manager");
            if (Env.getManagementPassword().equalsIgnoreCase(pwd)) {
                SeasonService seasonService = this.getService(SeasonService.class);
                this.renderData(seasonService.getFutureSeason());
            } else {
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "没有权限，请联系系统管理员");
            }
        } catch (Throwable t) {
            this.renderException("getFutureSeason", t);
        }
    }

    public void getOddsSeason() {
        try {
            Fetcher f = this.fetch();
            String pwd = f.s("manager");
            if (Env.getManagementPassword().equals(pwd)) {
                SeasonService seasonService = this.getService(SeasonService.class);
                this.renderData(seasonService.getOddsSeasons());
            } else {
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "没有权限，请联系管理员");
            }
        } catch (Throwable t) {
            this.renderException("getOddsSeason", t);
        }
    }

    public void getPlanSeason() {
        try {
            Fetcher f = this.fetch();
            if (Env.getManagementPassword().equals(f.s("manager"))) {
                SeasonService seasonService = this.getService(SeasonService.class);
                this.renderData(seasonService.getPlanSeason());
            } else {
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "没有权限，请联系管理员");
            }
        } catch (Throwable t) {
            this.renderException("getPlanSeason", t);
        }
    }

    /**
     *
     */
    public void batchCreateChatRoomWithTime() {
        try {
            Fetcher f = this.fetch();
            String managerPwd = f.s("manager");
            if (Env.getManagementPassword().equals(managerPwd)) {
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
                throw new ApiException(ErrorCode.OAUTH2_ERROR, "没有权限，请联系管理员");
            }
        } catch (Throwable t) {
            this.renderException("batchCreateChatRoomWithTime", t);
        }
    }


    public void batchRegisterUsers() {
        try {
            Fetcher f = this.fetch();
            String pwd = f.s("manager");
            if (Env.getManagementPassword().equals(pwd)) {
                UserService userService = this.getService(UserService.class);
                List<Map<String, Object>> users = userService.getUserList();
                MessageService messageService = this.getService(MessageService.class);
                executors.execute(() -> {
                    for (Map<String, Object> m : users) {
                        try {
                            messageService.registerUser(String.valueOf(m.get("s")), String.valueOf(m.get("s")),
                                    String.valueOf(m.get("a")), String.valueOf(m.get("n")));
                        } catch (ServiceException e) {
                            logger.error("batchRegisterUsers was error in controller. exception = {} ", e);
                        }
                    }
                });
                this.renderData();
            } else {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "无权访问此接口，请联系管理员");
            }
        } catch (Throwable t) {
            this.renderException("batchRegisterUsers", t);
        }
    }

    public void batchRefund() {
        try {
            Fetcher f = this.fetch();
            String pwd = f.s("manager");
            if (Env.getManagementPassword().equals(pwd)) {

            } else {
                throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "无权访问此接口，请联系管理员");
            }
        } catch (Throwable t) {
            this.renderException("batchRefund", t);
        }
    }

}
