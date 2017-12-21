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

	protected int updateTaskItem(int userId, Object actionValue,
			String actionName, int taskId, int itemId, int times,
			Action1<Integer> action) {

		return updateTaskItem(userId, actionValue, actionName,
				this.getServiceName(), taskId, itemId, 1, null);
	}

	@Asyned(name = "taskthreadpool")
	protected int updateTaskItem(int userId, Object actionValue,
			String actionName, String actionService, int taskId, int itemId,
			int times, Action1<Integer> action) {

		int result = 0;
		String sql1 = "SELECT d.date_id AS task_id, "
				+ "inst.instance_id AS item_id "
				+ "FROM razor_user_task_date d  "
				+ "INNER JOIN (SELECT ? user_id,? AS action_value,? AS action_name,? AS action_service,? as task_id,? as item_id,NOW() AS now) i "
				+ "INNER JOIN razor_user_task_reward rw ON rw.reward_id=d.reward_id and rw.enabled=1 "
				+ "INNER JOIN razor_user_task_action act ON act.action_id=rw.action_id "
				+ "left JOIN razor_user_task_reward_instance inst ON inst.date_id=d.date_id AND inst.user_id=i.user_id "
				+ "WHERE d.start_time<i.now AND d.end_time>i.now "
				+ "and rw.published_datetime<i.now and rw.terminal_datetime >i.now  "
				+ "and d.date_id=i.task_id "
				+ "and (i.item_id=0 or inst.instance_id=i.item_id)  "
				+ "AND act.action_service=i.action_service "
				+ "AND act.action_name=i.action_name "
				+ "and rw.type_id=1 "
				+ "AND (rw.action_value=i.action_value OR rw.action_value='*' ) ";

		Map<String, Object> map = record2map(Db.findFirst(sql1, userId,
				ParameterUtil.stringValue(actionValue), actionName,
				actionService, taskId, itemId));

		if (map != null) {

			Parameter p = new Parameter(map);
			int iTaskId = p.i("task_id");
			int iItemId = p.i("item_id", 0);

			try {
				if (itemId > 0) {
					result = updateTaskItem(iItemId, times);
				} else {
					result = updateTaskItem(userId, iTaskId, times);
				}

				if (result > 0 && action != null) {
					action.call(result);

				}

			} catch (Throwable e) {
				logger.error(
						"updateTaskItemById,userId=" + userId + ",taskId="
								+ iTaskId + ",itemId=" + iItemId + ",itemId2="
								+ result, e);
			}

		}

		return result;
	}

	protected boolean updateTaskItem(int userId, Object actionValue,
			String actionName) {
		return updateTaskItem(userId, actionValue, actionName,
				this.getServiceName(), 1, null);
	}

	@Asyned(name = "taskthreadpool")
	protected boolean updateTaskItem(int userId, Object actionValue,
			String actionName, String actionService, int times,
			Action1<Integer> action) {

		String sql1 = "SELECT d.date_id AS task_id, "
				+ "IFNULL(inst.instance_id,0) AS item_id, "
				+ "rw.action_value as action_value "
				+ "FROM razor_user_task_date d  "
				+ "INNER JOIN (SELECT ? user_id,? AS action_value,? AS action_name,? AS action_service,NOW() AS now) i "
				+ "INNER JOIN razor_user_task_reward rw ON rw.reward_id=d.reward_id and rw.enabled=1 and rw.published_datetime<i.now and rw.terminal_datetime >i.now "
				+ "INNER JOIN razor_user_task_action act ON act.action_id=rw.action_id "
				+ "left JOIN razor_user_task_reward_instance inst ON inst.date_id=d.date_id AND inst.user_id=i.user_id "
				+ "WHERE d.start_time<i.now AND d.end_time>i.now "
				+ "AND act.action_service=i.action_service "
				+ "AND act.action_name=i.action_name "
				+ "and rw.type_id=1 "
				+ "AND (rw.action_value=i.action_value OR rw.action_value='*' ) ";

		List<Map<String, Object>> list = record2list(Db.find(sql1, userId,
				ParameterUtil.stringValue(actionValue), actionName,
				actionService));
		int updated = 0;
		for (Map<String, Object> m : list) {
			Parameter p = new Parameter(m);
			int iTaskId = p.i("task_id");
			int iItemId = p.i("item_id", 0);
			//String iActionValue = p.s("action_value");
			int itemId = 0;
			try {
				if (iItemId > 0) {
					itemId = updateTaskItem(iItemId, times);
				} else {
					itemId = updateTaskItem(userId, iTaskId, times);
				}
				if (itemId > 0 && action != null) {
					action.call(itemId);
					updated++;
				}
			} catch (Throwable e) {
				logger.error(
						"updateTaskItem,userId=" + userId + ",taskId="
								+ iTaskId + ",itemId=" + iItemId + ",itemId2="
								+ itemId, e);
			}

		}

		return updated > 0;

	}

	@CacheRemove(name = CacheNames.task_instance_by_id, keyIndexes = { 0 })
	protected int updateTaskItem(int itemId, int times) {
		int ids[] = { 0 };
		String sql2 = "update  razor_user_task_reward_instance inst "

				+ "INNER JOIN  razor_user_task_date d on d.date_id=inst.date_id "
				+ "INNER JOIN (SELECT ? AS item_id,? times,now() as now ) i "
				+ "INNER JOIN razor_user_task_reward rw  ON rw.reward_id=d.reward_id and rw.enabled=1 "

				+ "set inst.current_times=current_times+IF(inst.finished,0,i.times), "
				+ "inst.finished=((inst.current_times+1)>=rw.max_times) "
				+ "WHERE d.start_time<i.now AND d.end_time> i.now "
				+ "AND inst.instance_id=i.item_id "
				+ "and rw.published_datetime<i.now and rw.terminal_datetime >i.now ";

		if (Db.update(sql2, itemId, times < 0 ? 0 : times) > 0) {
			ids[0] = itemId;
		}
		return ids[0];
	}

	@CacheRemove(name = CacheNames.task_instance_ids_by_users, userKey = CacheNames.task_instance_ids_by_users, keyIndexes = { 0 })
	protected int updateTaskItem(int userId, int taskId, int times) {
		int ids[] = { 0 };
		String sql2 = "INSERT  razor_user_task_reward_instance (user_id, date_id, current_times, finished, createtime) "
				+ "SELECT i.user_id,d.date_id,i.times,(i.times>=rw.max_times),i.now  "
				+ "FROM  razor_user_task_date d "
				+ "INNER JOIN (SELECT ? AS user_id,? as task_id, ? AS times,NOW() AS now ) i "
				+ "INNER JOIN razor_user_task_reward rw ON rw.reward_id=d.reward_id and rw.enabled=1 "
				+ "INNER JOIN razor_user_task_action act ON act.action_id=rw.action_id "
				+ "WHERE d.start_time<i.now AND d.end_time> i.now "
				+ "AND d.date_id=i.task_id "
				+ "and rw.published_datetime<i.now and rw.terminal_datetime >i.now ";

		DbUtil.update(sql2, ids, userId, taskId, times < 0 ? 0 : times);
		return ids[0];
	}

	@CacheRemove(name = CacheNames.task_instance_ids_by_users, userKey = CacheNames.task_instance_ids_by_users, keyIndexes = { 0 })
	protected int updateTaskItemCount(int userId, int taskId, int itemId, int times)
			throws ServiceException {

		int ids[] = { 0 };
		if (itemId > 0) {
			ids[0] = updateTaskItem(itemId, times);

		} else {
			ids[0] = updateTaskItem(userId, taskId, times);

		}
		return ids[0];

	}

	protected void publishStats(String code, String text, ContentType contentType, Charset charset, int outTimer) {
		try {
			HttpUtil.post("http://polling.19w.me/pub?code=" + code, text, contentType, charset, outTimer);
		} catch (IOException e) {
            logger.error("publishStats error. msg={}, exception={}", e.getMessage(), e);
		}
	}
}
