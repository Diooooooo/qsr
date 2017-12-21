package com.qsr.sdk.controller.render.datarender;

import com.qsr.sdk.lang.PageList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {

	private final Page page;

	private final Status status;

	private final Object data;

	private final List<?> datalist;

	private Task task;

	private Map<String, Object> conf;

	public Result(List<?> list) {
		this(list, null);
	}

	public Result(List<?> list, String statusMessage) {
		super();
		this.status = statusMessage == null ? Status.STATUS_SUCCESS
				: new Status(statusMessage);
		this.data = null;
		this.datalist = list;
		this.page = null;
	}

	public Result(PageList<?> pagelist) {
		this(pagelist, null);
	}

	public Result(PageList<?> pagelist, String statusMessage) {
		super();
		this.status = statusMessage == null ? Status.STATUS_SUCCESS
				: new Status(statusMessage);
		this.data = null;
		this.datalist = pagelist;
		this.page = new Page(pagelist.getPageIndex(), pagelist.getPageSize(),
				(int) pagelist.getTotal(), pagelist.getTotalPage(),
				pagelist.isHasMore());

	}

	public Result(Object object) {
		this(object, null);
	}

	public Result(Object object, String statusMessage) {
		super();
		this.status = statusMessage == null ? Status.STATUS_SUCCESS
				: new Status(statusMessage);
		this.data = object;
		this.page = null;
		this.datalist = null;

	}

	public Result(int dummy, Object[]... data) {
		super();
		this.page = null;
		this.status = Status.STATUS_SUCCESS;
		this.datalist = null;
		Map<String, Object> map = new HashMap<String, Object>();
		this.data = map;
		for (Object[] o : data) {
			if (o.length == 2 && o[0] != null && o[1] != null) {
				map.put(o[0].toString(), o[1]);
			}
		}
	}

	public Result(int code, String message) {
		super();
		this.status = new Status(code, message);
		this.data = null;
		this.datalist = null;
		this.page = null;
	}

	public Map<String, Object> getConf() {
		return conf;
	}

	public void setConf(Map<String, Object> conf) {
		this.conf = conf;
	}

	public Page getPage() {
		return page;
	}

	public Status getStatus() {
		return status;
	}

	public Object getData() {
		return data;
	}

	public List<?> getDatalist() {
		return datalist;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
