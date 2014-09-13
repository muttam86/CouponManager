package com.uttam.couponmanager.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;

import com.uttam.couponmanager.model.CouponCount;

@SuppressLint("UseSparseArrays") 
public class Calculator {
	public List<CouponCount> checkout(List<CouponCount> allCoupons, int checkoutAmount) {
		// First compute without doing separate computation for odd and even value coupons
		List<CouponCount> couponsToUseSingleCalc = checkoutSimple(allCoupons, checkoutAmount);
		int payByCashSingleCalc = checkoutAmount - CouponCount.getTotalValue(couponsToUseSingleCalc);
		int noOfCouponSingleCalc = CouponCount.getTotalCount(couponsToUseSingleCalc);
		
		// Now compute using separate computation for odd and even value coupons
		List<CouponCount> couponsToUseSplitCalc = checkoutWithSplit(allCoupons, checkoutAmount);
		int payByCashSplitCalc = checkoutAmount - CouponCount.getTotalValue(couponsToUseSplitCalc);
		int noOfCouponSplitCalc = CouponCount.getTotalCount(couponsToUseSplitCalc);
		
		// Check which approach yields best results and return
		if(payByCashSingleCalc < payByCashSplitCalc) {
			return couponsToUseSingleCalc;
		} else if (payByCashSingleCalc > payByCashSplitCalc) {
			return couponsToUseSplitCalc;
		} else { // == case
			if(noOfCouponSingleCalc <= noOfCouponSplitCalc) {
				return couponsToUseSingleCalc;
			} else {
				return couponsToUseSplitCalc;
			}
		}
	}
	
	private List<CouponCount> checkoutSimple(List<CouponCount> allCoupons, int checkoutAmount) {
		Collections.sort(allCoupons, CouponCount.Comparators.COUPON_VALUE_REVERSE);

		List<CouponCount> couponsToUse = new ArrayList<CouponCount>();
		for (CouponCount coupons : allCoupons) {
			int noOfCouponsToUse = checkoutAmount / coupons.getCouponValue();
			if (noOfCouponsToUse == 0 || coupons.getCount() == 0) {
				continue;
			}
			CouponCount couponCount = new CouponCount(coupons);
			if (noOfCouponsToUse < coupons.getCount()) {
				couponCount.setCount(noOfCouponsToUse);
			}
			couponsToUse.add(couponCount);
			int usedCouponsValues = couponCount.getCouponValue() * couponCount.getCount();
			checkoutAmount = checkoutAmount - usedCouponsValues;
		}
		return couponsToUse;

	}
	
	private List<CouponCount> checkoutWithSplit(List<CouponCount> allCoupons, int checkoutAmount) {
		List<CouponCount> oddValueCoupons = new ArrayList<CouponCount>();
		List<CouponCount> evenValueCoupons = new ArrayList<CouponCount>();
		
		for(CouponCount couponCount : allCoupons) {
			if(couponCount.getCouponValue() % 2 == 0) {
				evenValueCoupons.add(couponCount);
			} else {
				oddValueCoupons.add(couponCount);
			}
		}
		
		List<CouponCount> oddCouponsToUse = checkoutSimple(oddValueCoupons, checkoutAmount);
		List<CouponCount> evenCouponsToUse = checkoutSimple(evenValueCoupons, checkoutAmount - CouponCount.getTotalValue(oddCouponsToUse));
		
		List<CouponCount> allCouponsToUse = new ArrayList<CouponCount>();
		allCouponsToUse.addAll(oddCouponsToUse);
		allCouponsToUse.addAll(evenCouponsToUse);
		
		return allCouponsToUse;
	}
}


