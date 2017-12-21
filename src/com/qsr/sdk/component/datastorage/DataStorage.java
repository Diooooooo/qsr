package com.qsr.sdk.component.datastorage;

import com.qsr.sdk.component.Component;

public interface DataStorage extends Component {

	public ObjectData getObjectData(String name);

	public ObjectData addObjectData(String name, StoreStrategy strategy);

	public LongData getLongData(String name);

	public LongData addLongData(String name, StoreStrategy strategy);

	public ListData getListData(String name);

	public ListData addListData(String name, StoreStrategy strategy);

	public SortListData getSortListData(String name);

	public SortListData addSortListData(String name, StoreStrategy strategy);
}
