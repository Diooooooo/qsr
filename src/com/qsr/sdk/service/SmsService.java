package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.SmsSendHelper;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.startup.Startup;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.MapUtil;
import com.qsr.sdk.util.ParameterUtil;
import com.qsr.sdk.util.UtilValidate;

import java.util.*;

public class SmsService extends Service {

	private static Timer timer=new Timer();
	static{
		Startup.registerOnStop("stop for timer for sms",()->{
			timer.cancel();
		});
	}
	//ScheduledThreadPoolExecutor
	private static int template_id_event_verify=1;//活动验证
	private static int template_id_delivery_verify=4;//派件密码
	SmsService() {
		super();

	}

	/**
	 * 更具手机号、验证码，验证是否有效
	 * @param phoneNumber
	 * @param verifyCode
	 * @throws ServiceException
	 */
	public void verifyCode(String phoneNumber, String verifyCode) throws ServiceException {
		String sql="select smssend_id from qsr_smssend where phone_number=? and verify_code=? and expired_time>now() ";
		Integer integer = Db.queryInt(sql, phoneNumber, verifyCode);
		if (integer == null) {
			throw new ServiceException(getServiceName(), ErrorCode.ILLEGAL_DATA, "无效的验证码");
		}
	}

	/**
	 * 根据手机号，短信类型、验证码，验证是否有效
	 * @param typeName
	 * @param phoneNumber
	 * @param verifyCode
	 * @throws ServiceException
	 */
	public void verifyCode(String typeName, String phoneNumber, String verifyCode) throws ServiceException {
		String sql = "select smssend_id from qsr_smssend s "
				+ " inner join qsr_smssend_type st on s.type_id=st.type_id "
				+ " where s.phone_number=? and s.verify_code=? and st.name=? and s.expired_time>now() ";
		Integer integer = null;
		if(UtilValidate.isNotEmpty(phoneNumber) && UtilValidate.isNotEmpty(verifyCode)) {
			integer = Db.queryInt(sql, phoneNumber, verifyCode, typeName);
		}
		if (integer == null) {
			throw new ServiceException(getServiceName(), ErrorCode.ILLEGAL_DATA, "无效的验证码");
		}
	}

	/**
	 * 根据手机号、短信类型发送验证码
	 * @param typeName
	 * @param phoneNumber
	 * @throws ServiceException
	 */
	public void getVerifyCode(String typeName,String phoneNumber) throws ServiceException {
		String verifyCode = (new Random().nextInt(899999)+100000)+"";
		sendPhoneSms(typeName,phoneNumber,verifyCode,null);
	}

	public void delaySendPhoneSms(String typeName,String phoneNumber, String verifyCode, Map<String, String> smsParams){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					sendPhoneSms(typeName,phoneNumber,verifyCode,smsParams);
				} catch (Throwable e) {
					logger.error("delaySendPhoneSms",e);
				}
			}
		},1000*65 );
	}

	public void sendPhoneSms(String typeName,String phoneNumber, String verifyCode, Map<String, String> smsParams)
			throws ServiceException {
		try {
			Map<String, Object> m = getSmsType(typeName);
			if (m == null) {
				throw new ServiceException(getServiceName(), ErrorCode.ILLEGAL_DATA, "无效的短信类型:" + typeName);
			}
			Parameter smsType = new Parameter(m);
			int smsTemplateId = smsType.i("template_id");
			Map<String,String> params = getSmsTypeParams(typeName);
			if(UtilValidate.isNotEmpty(verifyCode)) {
				params.put("code", verifyCode);
			}
			if(UtilValidate.isNotEmpty(smsParams)) {
				params.putAll(smsParams);
			}
			String sql0="update qsr_smssend rs set rs.expired_time=now() where rs.phone_number=? and rs.type_id=? " +
					"and rs.expired_time>now()";
			Db.update(sql0, phoneNumber, smsType.i("type_id"));
			String sql="insert qsr_smssend (type_id, phone_number,template_id,verify_code,template_params,expired_time) " +
					"values(?,?,?,?,?,now()+interval 5 minute)";
			int smssendId=DbUtil.insertIntId(sql, smsType.i("type_id"), phoneNumber,smsTemplateId,verifyCode,
					MapUtil.map2str(params));
			SendResult request = SmsSendHelper.sendSms(phoneNumber, smsTemplateId, params);
			String sql2="insert qsr_smssend_result(smssend_id,success,code,message,others) values(?,?,?,?,?)";
			Db.update(sql2,smssendId,request.isSuccess(),request.getCode(),request.getMessage(),MapUtil.map2str(request.getAttributes()));
		} catch (ServiceException e) {
			throw e;
		} catch (ApiException e) {
			throw new ServiceException(getServiceName(), e);
		} catch (Exception e) {
			throw new ServiceException(getServiceName(), ErrorCode.INTERNAL_EXCEPTION, "获取验证码出现内部错误", e);
		}

	}

	@CacheAdd
	protected Map<String, Object> getSmsType(String typeName) {
		String sql = "select st.type_id,st.name,st.template_id from qsr_smssend_type st where enabled=1 and st.name=? ";
		return record2map(Db.findFirst(sql, typeName));
	}

	@CacheAdd
	protected Map<String, String> getSmsTypeParams(String typeName) {
		String sql = "select sta.param_name, sta.param_value from qsr_smssend_type st "
				+ " left join qsr_smssend_type_param sta on st.type_id = sta.type_id "
				+ " where enabled = 1 and st.name = ? ";
		List<Record> attrRecords = Db.find(sql, typeName);
		Map<String, String> attrInfo = new HashMap<>();
		if(UtilValidate.isNotEmpty(attrRecords)) {
			for (Record r:attrRecords) {
				attrInfo.put(r.getStr("param_name"), r.getStr("param_value"));
			}
		}
		return attrInfo;
	}
}
