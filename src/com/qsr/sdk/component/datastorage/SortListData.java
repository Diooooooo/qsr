package com.qsr.sdk.component.datastorage;

import java.util.List;

public interface SortListData {

	//public void addData(Object key, double score, Object data);
	public void addData(Object key, SortItem data);

	public void addList(Object key, List<SortItem> list);

	public List<SortItem> getList(Object key);
}
