package com.uttam.couponmanager.model;

import java.util.Date;

public class ChangeLogEntry {
	private int id;
	private int couponId;
	private int count;
	private long createTime;
	
	public ChangeLogEntry() {}

	public ChangeLogEntry(int _id, int _couponId, int _count, int _createTime) {
		id = _id;
		couponId = _couponId;
		count = _count;
		createTime = _createTime;
	}

	public int getId() {
		return id;
	}

	public int getCouponId() {
		return couponId;
	}

	public int getCount() {
		return count;
	}

	public long getCreateTime() {
		return createTime;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return "ChangeLogEntry [ " +
				"id: " + id + 
				", coupon_id: " + couponId + 
				", count: " + count + 
				", create_item: " + new Date(createTime) + " ]";
	}
}
	