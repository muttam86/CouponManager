package com.uttam.couponmanager.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CouponCount {
	private Coupon coupon;
	private int count;
	private long createTime;
	private long lastUpdateTime;
	
	public CouponCount() {}
	
	public CouponCount(Coupon _coupon, int _count, int _createTime, int _lastUpdateTime) {
		coupon = _coupon;
		count = _count;
		createTime = _createTime;
		lastUpdateTime = _lastUpdateTime;
	}
	
	public CouponCount(CouponCount couponCount) {
		coupon = couponCount.getCoupon();
		count = couponCount.getCount();
		createTime = couponCount.getCreateTime();
		lastUpdateTime = couponCount.getLastUpdateTime();
	}
	
	public Coupon getCoupon() {
		return coupon;
	}
	
	public int getCouponId() {
		return coupon.getId();
	}
	
	public String getCouponName() {
		return coupon.getName();
	}

	public int getCouponValue() {
		return coupon.getValue();
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
	
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
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
		return "Coupon Count: [ " +
				"coupon: " + coupon + 
				", count: " +  count + 
				", create_time: " + new Date(createTime) + 
				", last_update_time: " + new Date(lastUpdateTime) + " ]";
	}
	
	public static int getTotalValue(List<CouponCount> coupons) {
		int totalValue = 0;
		if(coupons == null) {
			throw new IllegalArgumentException("Input cannot be null");
		}
		
		for(CouponCount couponCount : coupons) {
			totalValue += couponCount.getCount() * couponCount.getCouponValue();
		}
		
		return totalValue;
	}
	
	public static int getTotalCount(List<CouponCount> list) {
		int sum = 0;
		for(CouponCount couponCount : list) {
			sum += couponCount.getCount();
		}
		return sum;
	}
	
	public static class Comparators {
		public static Comparator<CouponCount> COUPON_VALUE_REVERSE = new Comparator<CouponCount>() {
			@Override
			public int compare(CouponCount arg0, CouponCount arg1) {
				if(arg0.getCouponValue() > arg1.getCouponValue()) {
					return -1;
				}
				
				if(arg0.getCouponValue() < arg1.getCouponValue()) {
					return 1;
				}
				
				return 0;
			}
		};
		
		public static Comparator<CouponCount> TOTAL_VALUE_REVERSE = new Comparator<CouponCount>() {
			@Override
			public int compare(CouponCount arg0, CouponCount arg1) {
				if(arg0.getCouponValue() * arg0.getCount() > arg1.getCouponValue() * arg0.getCount()) {
					return -1;
				}
				
				if(arg0.getCouponValue() * arg0.getCount() < arg1.getCouponValue() * arg0.getCount()) {
					return 1;
				}
				
				return 0;
			}
		};
	}
}
