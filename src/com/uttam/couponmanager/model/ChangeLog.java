package com.uttam.couponmanager.model;

import java.util.Date;

public class ChangeLog {
	private int id;
	private ChangeType changeType;
	private int couponId;
	private int count;
	private long createTime;
	
	public enum ChangeType {SET, ADD, REMOVE};
	
	public ChangeLog() {}

	public ChangeLog(int _id, ChangeType _changeType, int _couponId, int _count, int _createTime) {
		id = _id;
		changeType = _changeType;
		couponId = _couponId;
		count = _count;
		createTime = _createTime;
	}

	public int getId() {
		return id;
	}
	
	public ChangeType getChangeType() {
		return changeType;
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
	
	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
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
				"change_type: " + changeType +
				", coupon_id: " + couponId + 
				", count: " + count + 
				", create_item: " + new Date(createTime) + " ]";
	}
}
	