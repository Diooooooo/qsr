package com.qsr.sdk.component.datastorage;

public interface LongData {

	public Long incr(Object key, long value);

	public Long decr(Object key, long value);

	public Long getValue(Object key);

	public void setValue(Object key, long value);

}
