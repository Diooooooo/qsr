package com.qsr.sdk.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.service.FileStorageService;
import com.qsr.sdk.service.SmsService;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.util.Constants;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class UserController extends WebApiController {
	final static Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController() {
		super(logger);
	}

	public void register() {
	    try {
	        Fetcher f = this.fetch();
	        logger.debug("register, params={}", f);

	        String mobile = f.s("phone_number");
	        String password = f.s("password");
	        String confirm = f.s("confirm");
	        String nickName = f.s("nick_name", "");
	        String userType = f.s("user_type", "qsr");

	        UserService userService = this.getService(UserService.class);
	        userService.register(mobile, nickName, password, confirm, userType);
	        this.renderData("成功");
        } catch (Throwable e) {
	        this.renderException("register", e);
        }
    }

	public void login() {
		try {
			Fetcher f = this.fetch();
			logger.debug("login,params={}", f);

			String clientIp = this.getRealRemoteAddr();
			String mobile= f.s("phone_number");
			String password = f.s("password");
			String userType = f.s("usertype", "qsr");
			UserService userService = this.getService(UserService.class);
			String sessionkey = userService.login(mobile, password, userType);
			int userId = userService.getUserIdBySessionKey(sessionkey);
			//更新信息
			userService.modifyLog(userId, clientIp);
			Map<String, Object> data = userService.getUserInfo(userId);
			this.renderData(data, "成功");
		} catch (Throwable e) {
			this.renderException("login", e);
		}
	}

	public void loginByVerifyCode() {
	    try {
	    	Fetcher f = this.fetch();
	    	logger.debug("loginByVerifyCode params={}", f);

	    	String mobile = f.s("phone_number");
	    	String verifyCode = f.s("verify_code");

	    	SmsService smsService = this.getService(SmsService.class);
	    	smsService.verifyCode(mobile, verifyCode);
	    	this.renderData("成功");
		} catch (Throwable e) {
	    	this.renderException("loginByVerifyCode", e);
		}
	}

	public void getUserProfile() {
	    try {
	        Fetcher f = this.fetch();
	        logger.debug("getUserProfile, params={}", f);

	        String sessionkey = f.s("sessionkey");
	        UserService userService = this.getService(UserService.class);
	        int userId = userService.getUserIdBySessionKey(sessionkey);
	        Map<String, Object> userInfo = userService.getUserInfo(userId);
	        this.renderData(userInfo, "成功");
        } catch (Throwable e) {
	        this.renderException("getUserProfile", e);
        }
    }

    public void modifyUserInfo() {
	    try {
	        Fetcher f = this.fetch();
	        logger.debug("modifyUserInfo, params={}", f);
	        List<String> imgs = Arrays.asList("head_img");
	        String sessionkey = f.s("sessionkey");
	        String nickName = f.s("nick_name", StringUtil.EMPTY_STRING);
	        String content = f.s("content", StringUtil.EMPTY_STRING);

	        List<UploadFile> tempUpdateFiles = f.getUploadFiles();
	        List<UploadFile> uploadFiles = new ArrayList<>();
	        for (UploadFile uf : tempUpdateFiles) {
	            if (imgs.contains(uf.getParameterName())) {
	                uploadFiles.add(uf);
                }
            }
            FileStorageService fileStorageService = this.getService(FileStorageService.class);
	        int fileProviderId = Env.getFileStorageProviderId();
	        int fileId = 0;
	        for (UploadFile up : uploadFiles) {
	            File file = up.getFile();
	            String fileUrl = fileStorageService.getFileMd5Uri("/", file);
	            fileId = fileStorageService.uploadFile(fileProviderId, fileUrl, file);
	            file.delete();
            }
            UserService userService = this.getService(UserService.class);
	        int userId = userService.getUserIdBySessionKey(sessionkey);
	        userService.modifyUserInfo(userId, nickName, content, fileId);
	        this.renderData("成功");
        } catch (Throwable e) {
	        this.renderException("modifyUserInfo", e);
        }
    }

    public void pageList() {
	    try {
	        Fetcher f = this.fetch();
	        List<Map<String, Object>> list = new ArrayList<>();
            getList(list);
            Page<Map<String, Object>> pages = new Page(list, 1, 10, 1, 10);
            this.renderData(pages, "成功");
        } catch (Throwable e) {
	        this.renderException("pageList", e);
        }
    }

    private void getList(List<Map<String, Object>> list) {
        for (int i = 0 ; i < 10; i ++) {
            Map<String, Object> m = new HashMap<>();
            m.put("key", "key");
            m.put("value", i);
            list.add(m);
}
    }

    public void list() {
	    try {
	        Fetcher f = this.fetch();
	        logger.debug("list, params = {}", f);
	        List<Map<String, Object>> list = new ArrayList<>();
            getList(list);
            this.renderData(list, "成功");
        } catch (Throwable e ) {
	        this.renderException("list", e);
        }
    }

    public void exception() {
	    try {
	        Fetcher f = this.fetch();
	        logger.debug("exception, params={}", f);
	        UserService userService = this.getService(UserService.class);
	        userService.exception();
	        this.renderData("成功");
        } catch (Throwable e) {
	        this.renderException("list", e);
        }
    }

	public void resetPasswordByPhoneNum() {
		try {

			Fetcher f = this.fetch();
			logger.debug("resetPasswordByPhoneNum,params={}", f);
			String mobile = f.s("phone_number");
			String verifyCode = f.s("verify_code");
			String newPassword = f.s("new_password");
			String confirm = f.s("confirm");

			SmsService smsService = this.getService(SmsService.class);
			smsService.verifyCode(Constants.SMS_TYPE_RESET_PWD, mobile, verifyCode);
			UserService userService = this.getService(UserService.class);
			userService.resetPassword(mobile, newPassword, confirm);
			this.renderData("成功");
		} catch (Throwable e) {
			this.renderException("resetPasswordByPhoneNum", e);
		}
	}

	public void modifyHead() {
		try {
			Fetcher f = this.fetch();
			List<UploadFile> files = f.getUploadFiles();
			if (null != files && files.size() == 1) {
			    String sessionkey = f.s("sessionkey");
			    UserService userService = this.getService(UserService.class);
			    int userId = userService.getUserIdBySessionKey(sessionkey);
				FileStorageService fileStorageService = this.getService(FileStorageService.class);
				int providerId = Env.getFileStorageProviderId();
				File uploadFile = files.get(0).getFile();
				String fileUrl = fileStorageService.getFileMd5Uri("heads/", uploadFile);
				int fileId = fileStorageService.uploadFile(providerId, fileUrl, uploadFile);
				uploadFile.delete();
				userService.modifyHead(userId, fileId, fileUrl);
				this.renderData("成功");
			} else {
				throw new IllegalArgumentException("需上传一张图片");
			}
		} catch (Throwable e) {
			this.renderException("modifyHead", e);
		}
	}

}
