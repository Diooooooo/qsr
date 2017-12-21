package com.qsr.sdk.lang;

import java.util.ArrayList;
import java.util.Collection;

public class PageList<E> extends ArrayList<E> {

	/**   */
	private static final long serialVersionUID = 829008655060053360L;
	private long total;
	private final int pageIndex;
	private final int pageSize;
	private final int totalPage;
	private final boolean hasMore;

	public PageList(Collection<? extends E> c, PageList<?> pageList) {
		super(c);
		this.total = pageList.getTotal();
		this.totalPage = pageList.getTotalPage();
		this.pageIndex = pageList.getPageIndex();
		this.pageSize = pageList.getPageSize();
		this.hasMore = totalPage > pageIndex;
	}

	public PageList(Collection<? extends E> c, long total, int totalPage,
			int pageIndex, int pageSize) {
		super(c);
		this.total = total;
		this.totalPage = totalPage;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.hasMore = totalPage > pageIndex;
	}

	public PageList(Collection<? extends E> c, int pageIndex, int pageSize) {
		super(c);
		this.total = c.size() < pageSize ? (pageIndex - 1) * pageSize
				+ c.size() : pageIndex * pageSize + 1;
		this.totalPage = total / pageSize + total % pageSize != 0 ? 1 : 0;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.hasMore = totalPage > pageIndex;
	}

	//	public PageList(long total, int pageIndex, int pageSize) {
	//		super();
	//		this();
	//		this.total = total;
	//		this.pageIndex = pageIndex;
	//		this.pageSize = pageSize;
	//	}
	//
	//	public PageList(int pageIndex, int pageSize) {
	//		super();
	//		this.total = 0;
	//		this.pageIndex = pageIndex;
	//		this.pageSize = pageSize;
	//	}

	public long getTotal() {
		return total;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public boolean isHasMore() {
		return hasMore;
	}

}
