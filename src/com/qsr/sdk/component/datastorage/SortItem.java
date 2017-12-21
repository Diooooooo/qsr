package com.qsr.sdk.component.datastorage;

public class SortItem {
	private final String itemId;
	private final Object data;
	private final double score;

	public SortItem(String itemId, Object data, double score) {
		super();
		this.itemId = itemId;
		this.data = data;
		this.score = score;
	}

	public Object getData() {
		return data;
	}

	public double getScore() {
		return score;
	}

	public String getItemId() {
		return itemId;
	}

}
