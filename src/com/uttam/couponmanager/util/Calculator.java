package com.uttam.couponmanager.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.uttam.couponmanager.model.CouponCount;

public class Calculator {
	public static List<CouponCount> checkout(List<CouponCount> allCouponCountEntries, int checkoutAmount) {
		Collections.sort(allCouponCountEntries, CouponCount.Comparators.COUPON_VALUE_REVERSE);
		
		List<CouponCount> couponsToUse = new ArrayList<CouponCount>();
		for(CouponCount coupons : allCouponCountEntries) {
			int noOfCouponsToUse = checkoutAmount / coupons.getCoupon().getValue();
			if(noOfCouponsToUse == 0 || coupons.getCount() == 0) {
				continue;
			}
			if(noOfCouponsToUse < coupons.getCount()) {
				coupons.setCount(noOfCouponsToUse);
			}
			couponsToUse.add(coupons);
			int usedCouponsValues = coupons.getCoupon().getValue() * coupons.getCount();
			checkoutAmount = checkoutAmount - usedCouponsValues;
		}
		return couponsToUse;
	}
}
