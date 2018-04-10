package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.lang.function.Action1;
import com.qsr.sdk.service.consts.CacheNames;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.Asyned;
import com.qsr.sdk.service.serviceproxy.annotation.CacheRemove;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.ParameterUtil;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Service {

	final static Logger logger = LoggerFactory.getLogger(Service.class);

	protected static final int cause_type_vanish_currency = -6; //货币间兑换消失的小数部分
	protected static final int cause_type_illegality_currency = -5;//无效的天行币
	protected static final int cause_type_exception = -4;//异常
	protected static final int cause_type_test = -3;//test
	protected static final int cause_type_exchange = -2;//exchange
	protected static final int cause_type_unkown = -1;//
	protected static final int cause_type_activation = 1; //razor_product_client_activation	应用激活
	protected static final int cause_type_download = 2; //razor_product_download	应用下载
	protected static final int cause_type_browse = 3; //razor_user_share_browse_reward_instance	活动页面浏览
	protected static final int cause_type_payment = 4; //razor_payment_order	支付额外奖励
	protected static final int cause_type_retention = 5; //razor_product_client_retention	应用留存
	protected static final int cause_type_mail = 6; //razor_user_mail_receipt	邮件奖励
	protected static final int cause_type_task = 7; //razor_user_task_reward_instance	任务奖励
	protected static final int cause_type_share = 8; //razor_user_share_reward_instance	页面分享
	protected static final int cause_type_signin = 9; //razor_user_signin_reward_instance	签到
	protected static final int cause_type_transfer_blacklist = 10; //razor_transfer_blacklist	提现无效数据
	protected static final int cause_type_payment_share = 11; //razor_payment_order_reward_instance	支付额外奖励2
	protected static final int cause_type_vote = 12; //razor_community_article_vote_reward_instance 	点赞奖励
	protected static final int cause_type_task_approve = 13; //razor_user_task_approve_reward_instance 	任务审批
	protected static final int cause_type_lottery_checkin = 14; //razor_entertainment_lottery_instance_item 投注
	protected static final int cause_type_lottery_bonus = 15; //rrazor_entertainment_lottery_instance_item 中奖
	protected static final int cause_type_product_internal = 16;//内部奖励
	protected static final int cause_type_manual = 17; //手动方法奖励
	protected static final int cause_type_aiyou_client_device_activation = 18; //哎呦我趣点击激活按钮得声望
    protected static final int cause_type_client = 19;//razor_product_client
	protected static final int cause_type_transfer = 20;//razor_transfer_request
	protected static final int cause_type_task_instance_instance_item = 21;//razor_user_task_instance_item
    protected static final int cause_type_transfer_item = 22; //razor_transfer_order_item
	protected static final int cause_type_product_install = 23; //razor_product_install	应用安装
	protected static final int cause_type_product_client = 24; //razor_product_client 打开
	protected static final int cause_type_product_event= 25; //razor_product_event
	protected static final int cause_type_sell_receipt = 26; //razor_sell_receipt
	//奖励规则说明，基数之前均为合理范围，基数之后的是基数+razor_sell_product.product_id
	private String serviceName = null;

	public Service() {

	}

	//	protected List<TaskAction> taskActions = new ArrayList<>();
	//
	//	protected List<PushAction> pushActions = new ArrayList<>();
	//
	//	protected List<BalanceAction> balanceActions = new ArrayList<>();
	//
	//	public List<TaskAction> getTaskActions() {
	//		return taskActions;
	//	}
	//
	//	public List<PushAction> getPushActions() {
	//		return pushActions;
	//	}
	//
	//	public List<BalanceAction> getBalanceActions() {
	//		return balanceActions;
	//	}

	//	protected void addBalanceAction(int userId, int balance, int type,
	//			int currencyType, String description, int causeId, int causeType,
	//			String actionValue) {
	//		BalanceAction balanceAction = new BalanceAction(userId, balance, type,
	//				currencyType, description, causeId, causeType, actionValue);
	//		balanceActions.add(balanceAction);
	//	}
	//
	//	protected void addBalanceAction(int userId, int balance, int currencyType,
	//			String description, int causeId, int causeType, String actionValue) {
	//		this.addBalanceAction(userId, balance, 1, currencyType, description,
	//				causeId, causeType, actionValue);
	//	}
	//
	//	protected void addBalanceAction(int userId, Record rewardRecord,
	//			int causeId, int causeType, String actionValue) {
	//		int currencyType = rewardRecord.getNumber("currency_type").intValue();
	//		int reward = rewardRecord.getNumber("reward").intValue();
	//		String description = this.processTemplate(rewardRecord,
	//				"reward_template");
	//		this.addBalanceAction(userId, reward, 1, currencyType, description,
	//				causeId, causeType, actionValue);
	//	}
	//
	//	protected void addPushAction(int userId, String message) {
	//		this.addPushAction(userId, message, 1);
	//	}
	//
	//	public void addPushAction(int userId, String message, int type) {
	//		this.pushActions.add(new PushAction(userId, type, message));
	//	}
	//
	//	protected void addTaskAction(int userId, String actionName) {
	//		this.addTaskAction(userId, actionName, StringUtil.EMPTY_STRING, 1);
	//	}
	//
	//	protected void addTaskAction(int userId, String actionName,
	//			String actionValue) {
	//		this.addTaskAction(userId, actionName, actionValue, 1);
	//	}
	//
	//	protected void addTaskAction(int userId, String actionName,
	//			String actionValue, int times) {
	//		String actionService = this.getClass().getSimpleName();
	//		TaskAction taskAction = new TaskAction(userId, actionValue,
	//				actionService, actionName, times);
	//		taskActions.add(taskAction);
	//	}

	public Class<?> getActualClass() {

		Class<?> clazz = this.getClass();
		while (clazz.getSimpleName().contains("EnhancerByCGLIB")) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				break;
			}
			clazz = superClass;

		}
		return clazz;
	}

	public String getServiceName() {
		if (serviceName == null) {
			serviceName = getActualClass().getSimpleName();
		}
		return serviceName;

	}

	//	protected void postTaskEvent(int userId, String actionValue,
	//			String actionName, String actionService, int times) {
	//
	//		ServiceEvent event = new TaskEvent(userId, actionValue, actionName,
	//				actionService, times);
	//		postEvent(event);
	//	}
	//
	//	public void postTaskEvent(int userId, Object actionValue,
	//			String actionName, Class<? extends Service> clazz) {
	//		String actionValue2 = actionValue != null ? actionValue.toString()
	//				: StringUtil.EMPTY_STRING;
	//		postTaskEvent(userId, actionValue2, actionName, clazz.getSimpleName(),
	//				1);
	//	}

	protected static Map<String, Object> record2map(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return m;
	}

	protected static Parameter record2param(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return new Parameter(m);
	}

	protected static List<Map<String, Object>> record2list(List<Record> list) {

		List<Map<String, Object>> result = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Record r : list) {
				result.add(record2map(r));
			}
		}
		return result;
	}

	protected static <T> Map<T, Map<String, Object>> list2map(List<Record> list,
			String keyField) {
		Map<T, Map<String, Object>> result = new LinkedHashMap<T, Map<String, Object>>();
		//List<Map<String, Object>> list = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Record r : list) {
				Map<String, Object> m = record2map(r);
				T key = (T) m.get(keyField);
				if (key == null) {
					continue;
				}
				result.put(key, m);
			}
		}
		return result;
	}

	protected static PageList<Map<String, Object>> page2PageList(Page<Record> page) {
		PageList<Map<String, Object>> result = new PageList<>(
				record2list(page.getList()), page.getTotalRow(),
				page.getTotalPage(), page.getPageNumber(), page.getPageSize());
		return result;
	}

	protected String processTemplate(String template, Record record) {
		return processTemplate(template, record2map(record));
	}

	protected String processTemplate(Record record, String templatefield) {
		String template = record.getStr(templatefield);
		if (template != null) {
			Map<String, Object> map = record2map(record);
			map.remove(templatefield);
			template = processTemplate(template, map);
		}
		return template;
	}

	protected String processTemplate(String template, Map<String, Object> map) {
		String result = template;
		if (map != null) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String variantName = "{" + entry.getKey() + "}";
				String value = "" + entry.getValue();
				result = result.replace(variantName, value);
			}
		}
		return result;

	}
}
