package com.uttam.couponmanager.model;

public class Coupon {
	private int id;
	private String name;
	private int value;
	public static final int couponValue[] = {5, 10, 20, 35, 50, 100, 500};
	
	// FIXME: hack
	public static Integer getId(String name) {
		int value = Integer.parseInt(name.replaceAll("\\D+",""));
		for(int i = 0; i < couponValue.length; i++) {
			if(value == couponValue[i]) {
				return i + 1; 
			}
		}
		throw new IllegalArgumentException("Unrecognized name");
		
	}
	
	public Coupon(int _id, String _name, int _value) {
		id = _id;
		name = _name;
		value = _value;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
	
	public String toString() {
		return "Coupon: [ " +
				"id: " + id + 
				", name: " +  name + 
				", value: " + value + " ]";
	}
}
