package com.qsr.sdk.jfinal;

import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.lang.Parameter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RecordBuilder {

	Parameter parameter;
	Map<String, Object> columns = new LinkedHashMap<>();

	Set<String> accessKeys = new HashSet<String>();

	public RecordBuilder(Map<?, ?> map) {
		this.parameter = new Parameter(map);
	}

	public RecordBuilder addColumn(String field, Object value) {
		return addColumn(field, value, false);
	}

	public RecordBuilder addColumn(String field, Object value, boolean allowNull) {

		if (value != null || allowNull) {
			columns.put(field, value);
		}
		return this;
	}

	public RecordBuilder bindColumn(String field, String key) {
		accessKeys.add(key);
		return addColumn(field, parameter.o(key, null), false);
	}

	public RecordBuilder bindColumn(String field, String key,
			Object defaultValue) {
		accessKeys.add(key);
		return addColumn(field, parameter.o(key, defaultValue), true);
	}

	public RecordBuilder bindIntColumn(String field, String key) {
		accessKeys.add(key);
		return addColumn(field, parameter.i(key), false);
	}

	public RecordBuilder bindIntColumn(String field, String key,
			int defaultValue) {
		accessKeys.add(key);
		return addColumn(field, parameter.i(key, defaultValue), false);
	}

	public Record create() {
		Record record = new Record();
		record.setColumns(columns);
		return record;
	}

	public Set<String> getAccessKeys() {
		return accessKeys;
	}
}
