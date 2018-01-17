package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.PushHelper;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.ParameterUtil;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PushService extends Service {

	final static Logger logger = LoggerFactory.getLogger(PushService.class);

	PushService() {

	}

	public void bindPushUser(int userId, String pushUserId, String pushChannelId)
			throws ServiceException {

		if (pushUserId != null && pushChannelId != null) {

			String sql = "update razor_user_pushuser pu "
					+ "inner join (select ? as user_id,? as push_user_id,? as push_channel_id,now() as now) i  "
					+ "left join razor_user_pushprovider p on p.name=i.push_channel_id "
					+ "set pu.push_user_id=i.push_user_id,"
					+ "pu.provider_id=ifnull(p.provider_id,0) "
					+ "where pu.user_id=i.user_id ";

			Db.update(sql, userId, pushUserId, pushChannelId);
		}

		//ServiceEventManager.postTaskEvent(userId, "", "bindPushUser", actionService, times);
		//this.addTaskAction(userId, "bindPushUser");

	}

	//	private Record getPushUser(int userId) throws ServiceException {
	//		String sql = "SELECT user_id,push_user_id, push_channel_id FROM razor_user_pushuser "
	//				+ " WHERE user_id=?";
	//		Record record = Db.findFirst(sql, userId);
	//		if (record == null) {
	//			throw new ServiceException(ErrorCode.NOT_FOUND_SPEC_DATA,
	//					"没有找到推送用户信息");
	//		}
	//		return record;
	//		// return record.getColumns();
	//	}
	//
	//	private int getPushProviderId(String pushChannelId) {
	//		String sql = "select provider_id from razor_user_pushprovider p where p.name=?";
	//		Integer providerId = Db.queryInt(sql, pushChannelId);
	//		return providerId != null ? providerId : 0;
	//	}

	private int pushSingleMessage(int providerId, String pushUserId,
			String message, int type) throws ServiceException {
		// int providerId = getPushProviderId(pushChannelId);

		//		Push push = ServiceProviderManager.getServiceProvider(providerId,
		//				AbstractPush.class);
		//		if (push == null) {
		//			throw new ServiceException(getServiceName(),
		//					ErrorCode.NOT_FOUND_SPEC_DATA, "没有找到对应的推送服务");
		//		}
		try {
			return PushHelper
					.pushMessage(providerId, pushUserId, message, type, 1);
			//return push.pushSingleMessage(pushUserId, message, type);
		} catch (ApiException e) {
			throw new ServiceException(getServiceName(), e);
		}

	}

	// private void pushMessage(int pushMessageId, long pushChannelId,
	// String pushUserId, int messageType, String messageContent) {
	//
	// // String pushUserId = record.getStr("push_user_id");
	// // String pushChannelIdStr = record.getStr("push_channel_id");
	// // long pushChannelId = Long.parseLong(pushChannelIdStr);
	// //
	// // int messageType = record.getInt("push_messagetype");
	// // String messageContent = record.getStr("push_message");
	//
	// Map<String, Object> messageMap = new HashMap<String, Object>();
	// messageMap.put("type", messageType);
	// messageMap.put("message", messageContent);
	//
	// String jsonMessage = JsonUtil.toJson(messageMap);
	//
	// try {
	// pushUnicastMessage(pushChannelId, pushUserId, jsonMessage);
	// } catch (ServiceException e) {
	// // TODO Auto-generated catch block
	// // e.printStackTrace();
	// Db.update(sql, paras);
	// }
	//
	// }

	private void pushMessage(int pushMessageId) throws ServiceException {
		String sql1 = "select m.provider_id,m.push_user_id,m.push_messagetype,m.push_message from razor_user_pushmessage m "
				+ "inner join (select ? as push_message_id) i "
				+ "where m.push_message_id=i.push_message_id ";
		Record record = Db.findFirst(sql1, pushMessageId);
		if (record == null) {
			throw new ServiceException(getServiceName(),
					ErrorCode.NOT_FOUND_SPEC_DATA, "没有找对应的推送消息");
		}
		Parameter p = new Parameter(record2map(record));

		String pushUserId = p.s("push_user_id", "");
		if (pushUserId.length() < 1) {
			throw new ServiceException(getServiceName(),
					ErrorCode.ILLEGAL_DATA, "无效的推送用户信息");
		}

		// String pushChannelId = record.getStr("push_channel_id");
		int providerId = p.i("provider_id", 0);
		if (providerId == 0) {
			throw new ServiceException(getServiceName(),
					ErrorCode.ILLEGAL_DATA, "无效的推送提供商");
		}
		int messageType = p.i("push_messagetype", 0);
		String messageContent = p.s("push_message", "");

		String sql2 = "update razor_user_pushmessage "
				+ "set push_success_count=push_success_count+?,push_count=push_count+1 "
				+ "where push_message_id=?";
		String sql3 = "update razor_user_pushmessage "
				+ "set push_exceptioncode=?,push_exceptionmessage=? "
				+ "where  push_message_id=?";
		try {
			int successCount = pushSingleMessage(providerId, pushUserId,
					messageContent, messageType);
			Db.update(sql2, successCount, pushMessageId);
		} catch (ServiceException e) {
			Db.update(sql3, e.getCode(), e.getMessage(), pushMessageId);
			throw e;
		}

	}

	public void pushMessage(int userId, int type, String message)
			throws ServiceException {

		try {
			PushHelper.pushMessage(2, null, message +", type=" + type, type, 2);
		} catch (ApiException e) {
		    throw new ServiceException(getServiceName(), ErrorCode.THIRD_SERVICE_EXCEPTIOIN, e.getMessage(), e);
		}
//		String sql0 = "select pu.provider_id,pu.push_user_id from razor_user_pushuser pu "
//				+ "where pu.user_id=? ";
//		int providerId = 0;
//		String pushUserId = StringUtil.EMPTY_STRING;
//		Map<String, Object> data = record2map(Db.findFirst(sql0, userId));
//		int pushSuccessCount = 0;
//		int pushExceptionCode = 0;
//		String pushExceptionMessage = StringUtil.EMPTY_STRING;
//
//		try {
//			if (data == null) {
//				providerId = 0;
//				pushUserId = "";
//				throw new ApiException(ErrorCode.ILLEGAL_STATE, "用户还未绑定推送");
//			} else {
//				providerId = ParameterUtil.integerParam(data, "provider_id");
//				pushUserId = ParameterUtil.stringParam(data, "push_user_id");
//
//				pushSuccessCount = PushHelper.pushMessage(providerId,
//						pushUserId, message, type);
//
//				logger.debug("pushMessage:userId={},{}", userId, message);
//			}
//
//		} catch (ApiException e) {
//			pushExceptionCode = e.getCode();
//			pushExceptionMessage = e.getMessage();
//			throw new ServiceException(getServiceName(), e);
//		} catch (Exception e) {
//			pushExceptionMessage = e.getMessage();
//			pushExceptionCode = ErrorCode.THIRD_SERVICE_EXCEPTIOIN;
//			throw new ServiceException(getServiceName(),
//					ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "第三方服务提供异常", e);
//		} finally {
//			String sql = "insert razor_user_pushmessage(user_id,provider_id,push_user_id,push_messagetype,push_message,"
//					+ "push_success_count,push_exceptioncode,push_exceptionmessage,createtime) "
//					+ "values(?,?,?,?,?,?,?,?,now()) ";
//			Db.update(sql, userId, providerId, pushUserId, type, message,
//					pushSuccessCount, pushExceptionCode, pushExceptionMessage);
//
//		}

	}

	// private void logPushMessage(int userId, String pushUserId,
	// String pushChannelId, int messageType, String messageContent,
	// int successAmount, ServiceException e) {
	//
	// String sql =
	// "INSERT razor_user_pushmessage( user_id, push_user_id,push_channel_id, "
	// + "push_messagetype,push_message, push_success_amount, "
	// + "push_exceptioncode,push_exceptionmessage) "
	// + "VALUES( ?,?,?,?,?,?,?,?)";
	// int exceptionCode = e != null ? e.getCode() : 0;
	// String exceptionMessage = e != null ? e.getMessage() : null;
	// try {
	//
	// DbUtil.update(sql, generated, paras);
	// Db.update(sql, userId, pushUserId, pushChannelId, messageType,
	// messageContent, successAmount, exceptionCode,
	// exceptionMessage);
	// } catch (Exception e1) {
	// logger.error("insert razor_user_pushmessage_log exception:", e1);
	// }
	//
	// }

}
