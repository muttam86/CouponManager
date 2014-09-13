package com.uttam.couponmanager.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.uttam.couponmanager.model.ChangeLog;
import com.uttam.couponmanager.model.ChangeLog.ChangeType;
import com.uttam.couponmanager.model.Coupon;
import com.uttam.couponmanager.model.CouponCount;


@SuppressLint("UseSparseArrays") public class MySQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "coupons.db";
	private static final int DATABASE_VERSION = 9;
	private static final String COUPON_TABLE_NAME = "coupon";
	private static final String COUPON_COUNT_TABLE_NAME = "coupon_count";
	private static final String CHANGE_LOG_TABLE_NAME = "change_log";
	private static final Integer MAX_CHANGE_LOG_ENTRIES = 30;
	private static final String CREATE_COUPON_TABLE = "create table " +
			COUPON_TABLE_NAME +
			"(coupon_id integer primary key," +
			" name string not null," +
			" value integer not null)";
	private static final String CREATE_COUPON_COUNT_TABLE = "create table " +
			COUPON_COUNT_TABLE_NAME +
			"(coupon_id integer primary key," +
			" count integer not null," +
			" create_time integer not null," +
			" last_update_time integer not null);";
	private static final String CREATE_CHANGE_LOG_TABLE = "create table " +
			CHANGE_LOG_TABLE_NAME +
			"(id integer primary key autoincrement," +
			"change_type string not null," +
			" coupon_id integer not null," +
			" count integer not null," +
			" create_time integer not null)";

	private final Map<Integer, Coupon> coupons;
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		coupons = new HashMap<Integer, Coupon>();
		for(int i = 0; i < Coupon.couponValue.length; i++) {
			Coupon coupon = new Coupon(i + 1, "SUDEXO", Coupon.couponValue[i]);
			coupons.put(coupon.getId(), coupon);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COUPON_TABLE);
		database.execSQL(CREATE_COUPON_COUNT_TABLE);
		database.execSQL(CREATE_CHANGE_LOG_TABLE);
		
		// Pre-populate supported coupons
		for(int i = 0; i < Coupon.couponValue.length; i++) {
			ContentValues values = new ContentValues();
			values.put("coupon_id", i + 1);
			values.put("name", "SUDEXO");
			values.put("value", Coupon.couponValue[i]);
			database.insert(COUPON_TABLE_NAME,
					null, // nullColumnHack
					values);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CHANGE_LOG_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + COUPON_COUNT_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + COUPON_TABLE_NAME);
		onCreate(database);
	}
	
	public void updateCouponCountEntry(CouponCount newEntry) {
		Log.d("addCouponCountEntry", newEntry.toString());
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		String getQuery = "SELECT * FROM " + COUPON_COUNT_TABLE_NAME + " WHERE coupon_id = " + newEntry.getCouponId();
		Cursor cursor = db.rawQuery(getQuery, null);
		
		Log.d("Get query result count: ", "" + cursor.getCount());
		
		ContentValues values = new ContentValues();
		values.put("count", newEntry.getCount());
		values.put("last_update_time", System.currentTimeMillis());
		
		if(cursor.getCount() > 0) {
			db.update(COUPON_COUNT_TABLE_NAME, values, "coupon_id = " + newEntry.getCouponId(), null);
		} else {
			values.put("coupon_id", newEntry.getCouponId());
			values.put("create_time", System.currentTimeMillis());
			db.insert(COUPON_COUNT_TABLE_NAME,
					null, // nullColumnHack
					values);

		}
		db.close();
	}
	
	public void addChangeLogEntry(ChangeLog newEntry) {
		Log.d("addChangeLogEntry", newEntry.toString());

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("coupon_id", newEntry.getCouponId());
		values.put("change_type", newEntry.getChangeType().name());
		values.put("count", newEntry.getCount());
		values.put("create_time", System.currentTimeMillis());

		db.insert(CHANGE_LOG_TABLE_NAME,
				null, // nullColumnHack
				values);

		db.close();
	}
	
	public List<ChangeLog> getAllChangeLogEntries() {
		List<ChangeLog> changeLogEntries = new LinkedList<ChangeLog>();
		String query = "SELECT  * FROM " + CHANGE_LOG_TABLE_NAME + " ORDER BY id desc limit " + MAX_CHANGE_LOG_ENTRIES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		ChangeLog changeLogEntry = null;
		if (cursor.moveToFirst()) {
			do {
				changeLogEntry = new ChangeLog();
				changeLogEntry.setId(Integer.parseInt(cursor.getString(0)));
				changeLogEntry.setChangeType(ChangeType.valueOf(cursor.getString(1)));
				changeLogEntry.setCouponId(Integer.parseInt(cursor.getString(2)));
				changeLogEntry.setCount(Integer.parseInt(cursor.getString(3)));
				changeLogEntry.setCreateTime(Long.parseLong(cursor.getString(4)));

				changeLogEntries.add(changeLogEntry);
			} while (cursor.moveToNext());
		}
		return changeLogEntries;
	}
	
	public List<CouponCount> getAllCouponCountEntries() {
		List<CouponCount> couponCountEntries = new LinkedList<CouponCount>();
		
		String query = "SELECT  * FROM " + COUPON_COUNT_TABLE_NAME + " WHERE count > 0";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		CouponCount couponCountEntry = null;
		if (cursor.moveToFirst()) {
			do {
				couponCountEntry = read(cursor);
				couponCountEntries.add(couponCountEntry);
			} while (cursor.moveToNext());
		}

		return couponCountEntries;
	}
	
	@SuppressLint("UseSparseArrays") 
	public Map<Integer, Coupon> getAllCoupons() {
		Map<Integer, Coupon> coupons = new HashMap<Integer, Coupon>();
		
		String query = "SELECT  * FROM " + COUPON_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Integer id = Integer.parseInt(cursor.getString(0));
				String name = cursor.getString(1);
				Integer value = Integer.parseInt(cursor.getString(2));
				Coupon coupon = new Coupon(id, name, value);
				coupons.put(coupon.getId(), coupon);
			} while (cursor.moveToNext());
		}
		return coupons;
	}
	
	public Coupon getCoupon(int couponId) {
		return coupons.get(couponId);
	}
	
	public CouponCount getCouponCount(int couponId) {
		String query = "SELECT  * FROM " + COUPON_COUNT_TABLE_NAME + " WHERE coupon_id = " + couponId ;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst()) {
			return read(cursor);
		}
		return null;
	}
	
	private CouponCount read(Cursor cursor) {
		CouponCount couponCountEntry = new CouponCount();
		couponCountEntry.setCoupon(coupons.get(Integer.parseInt(cursor.getString(0))));
		couponCountEntry.setCount(Integer.parseInt(cursor.getString(1)));
		couponCountEntry.setCreateTime(Long.parseLong(cursor.getString(2)));
		couponCountEntry.setLastUpdateTime(Long.parseLong(cursor.getString(3)));
		return couponCountEntry;
	}
}
