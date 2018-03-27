package com.qsr.sdk.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.*;
import com.qsr.sdk.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class UserController extends WebApiController {
	final static Logger logger = LoggerFactory.getLogger(UserController.class);
	private final static String FILE_PREFIX = "userfiles/";
    private static final int[] TYPES = {0, 7, 14, 30, 90};
    private static final String PREX = "P";

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
	        String nickName = f.s("nick_name", PREX + mobile + System.currentTimeMillis());
	        String userType = f.s("user_type", "qsr");
	        String ip = this.getRemoteAddr();

	        if (!password.equals(confirm)) {
	        	throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "两次密码不一样");
			}
            UserService userService = this.getService(UserService.class);
	        if (userService.check(mobile)) {
	            throw new ApiException(ErrorCode.USER_ALREADY_EXIST, "已注册的手机号");
            }
			MessageService messageService = this.getService(MessageService.class);
			messageService.registerUser(Md5Util.digest(mobile), Md5Util.digest(mobile), StringUtil.NULL_STRING, nickName);
	        userService.register(mobile, nickName, password, confirm, userType, ip);
	        this.renderData(SUCCESS);
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
			this.renderData(data, SUCCESS);
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
	    	this.renderData(SUCCESS);
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
	        BalanceService balanceService = this.getService(BalanceService.class);
	        int userId = userService.getUserIdBySessionKey(sessionkey);
	        Map<String, Object> userInfo = userService.getUserInfo(userId);
	        userInfo.put("balance", balanceService.getUserBalance(userId));
	        this.renderData(userInfo, SUCCESS);
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
	        String sex = f.s("user_sex", StringUtil.NULL_STRING);
	        String birthday = f.s("birthday", StringUtil.NULL_STRING);
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
	        birthday = StringUtil.isEmptyOrNull(birthday) ? StringUtil.NULL_STRING : birthday;
	        sex = StringUtil.isEmptyOrNull(sex) ? StringUtil.NULL_STRING : sex;
	        int userId = userService.getUserIdBySessionKey(sessionkey);
	        userService.modifyUserInfo(userId, nickName, content, fileId, sex, birthday);
	        this.renderData(SUCCESS);
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
            this.renderData(pages, SUCCESS);
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
            this.renderData(list, SUCCESS);
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
	        this.renderData(SUCCESS);
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
			this.renderData(SUCCESS);
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
				this.renderData(SUCCESS);
			} else {
				throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "需上传一张图片");
			}
		} catch (Throwable e) {
			this.renderException("modifyHead", e);
		}
	}

	public void aboutUs() {
		try {
		    Fetcher f = this.fetch();
		    logger.debug("aboutUs params={}", f);
		    Map<String, Object> info = new HashMap<>();
		    info.put("url", Env.getAboutUs());
		    this.renderData(info, SUCCESS);
		} catch (Throwable t) {
			this.renderException("aboutUs", t);
		}
	}

	public void information() {
		try {
			Fetcher f = this.fetch();
			logger.debug("information", f);
			Map<String, Object> information = new HashMap<>();
			information.put("url", Env.getInformation());
			this.renderData(information, SUCCESS);
		} catch (Throwable t) {
			this.renderException("information", t);
		}
	}

	public void sendInformation() {
		try {
			Fetcher f = this.fetch();
			logger.debug("sendInformation, params={}", f);
			String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
			String information = f.s("information");
			InformationService informationService = this.getService(InformationService.class);
			informationService.information(information);
			this.renderData(SUCCESS);
		} catch (Throwable t) {
			this.renderException("sendInformation", t);
		}
	}

	public void bindIDCard() {
		try {
		    Fetcher f = this.fetch();
		    String sessionkey = f.s("sessionkey");
		    String name = f.s("name");
		    String number = f.s("number");
		    UploadFile frontd = this.getFile("idcard_photo1");
			UploadFile reverse = this.getFile("idcard_photo2");
			UploadFile hold = this.getFile("idcard_photo3");
			if (null == frontd || null == reverse || null == hold) {
			    throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "参数不完整");
			}
			UserService userService = this.getService(UserService.class);
			int userId = userService.getUserIdBySessionKey(sessionkey);
			FileStorageService fileStorageService = this.getService(FileStorageService.class);
			int fileStorageProviderId = Env.getFileStorageProviderId();
			int frontdId = fileStorageService.uploadFile(fileStorageProviderId, FILE_PREFIX + userId, frontd.getFile());
			frontd.getFile().delete();
			int reverseId = fileStorageService.uploadFile(fileStorageProviderId, FILE_PREFIX + userId, reverse.getFile());
			reverse.getFile().delete();
			int holdId = fileStorageService.uploadFile(fileStorageProviderId, FILE_PREFIX + userId, hold.getFile());
			hold.getFile().delete();
			BindService bindService = this.getService(BindService.class);
			bindService.bindIdCard(userId, name, number, frontdId, reverseId, holdId);
			this.renderData(SUCCESS);
		} catch (Throwable t) {
			this.renderException("bindIDCard", t);
		}
	}

	public void getBalanceLogLevel() {
        try {
            Fetcher f = this.fetch();
            String sessionkey = f.s("sessionkey", StringUtil.NULL_STRING);
            List<Map<String, Object>> res = new ArrayList<>();
            for (int i: TYPES) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", i);
                m.put("text", 0 == i ? "全部" : 7 == i ? "最近一周" : 14 == i ? "最近两周" : 30 == i ? "最近一个月" : 90 == i ? "最近两个月" : "");
                res.add(m);
            }
            this.renderData(res, SUCCESS);
        } catch (Throwable t) {
            this.renderException("getBalanceLogLevel", t);
        }
    }

	public void getBalanceLog() {
		try {
		    Fetcher f = this.fetch();
		    String sessionkey = f.s("sessionkey");
		    int typeId = f.i("type");
		    int pageNumber = f.i("pageNumber", 10);
		    int pageSize = f.i("pageSize", 1);
		    if (!ArrayUtils.contains(TYPES, typeId)) {
		        throw new ApiException(ErrorCode.PARAMER_ILLEGAL, "选取时间段不正确");
            }
		    UserService userService = this.getService(UserService.class);
		    int userId = userService.getUserIdBySessionKey(sessionkey);
		    PayOrderService payOrderService = this.getService(PayOrderService.class);
		    int id = -1;
		    for(int i = 0; i < TYPES.length ; i ++) {
		        if (typeId == TYPES[i])
		            id = i - 1;
            }
		    this.renderData(payOrderService.getSelfPayorders(userId, pageNumber, pageSize, id));
		} catch (Throwable t) {
			this.renderException("getBalanceLog", t);
		}
	}
}
