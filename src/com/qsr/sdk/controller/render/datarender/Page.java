package com.qsr.sdk.controller.render.datarender;

public class Page {
	private final int pageindex;
	private final int pagesize;
	private final int total;
	private final int totalpage;
	private final boolean hasmore;

	public Page(int pageindex, int pagesize, int total, int totalpage,
			boolean hasmore) {
		super();
		this.pageindex = pageindex;
		this.pagesize = pagesize;
		this.total = total;
		this.totalpage = totalpage;
		this.hasmore = hasmore;
	}

	public int getPageindex() {
		return pageindex;
	}

	public int getPagesize() {
		return pagesize;
	}

	public int getTotal() {
		return total;
	}

}
