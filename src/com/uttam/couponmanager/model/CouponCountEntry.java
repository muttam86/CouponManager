package com.uttam.couponmanager.model;

import java.util.Date;

public class CouponCountEntry {
	private int couponId;
	private int count;
	private long createTime;
	private long lastUpdateTime;
	
	public CouponCountEntry() {}
	
	public CouponCountEntry(int _couponId, int _count, int _createTime, int _lastUpdateTime) {
		couponId = _couponId;
		count = _count;
		createTime = _createTime;
		lastUpdateTime = _lastUpdateTime;
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

	public long getLastUpdateTime() {
		return lastUpdateTime;
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

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String toString() {
		return "Coupon Entry: [ " +
				"coupon_id: " + couponId + 
				", count: " +  count + 
				", create_time: " + new Date(createTime) + 
				", last_update_time: " + new Date(lastUpdateTime) + " ]";
	}
}
