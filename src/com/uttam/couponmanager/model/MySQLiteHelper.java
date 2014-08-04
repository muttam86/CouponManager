package com.uttam.couponmanager.model;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "coupons.db";
	private static final int DATABASE_VERSION = 1;
	private static final String COUPON_COUNT_TABLE_NAME = "coupon_count";
	private static final String CHANGE_LOG_TABLE_NAME = "change_log";

	private static final String CREATE_COUPON_COUNT_TABLE = "create table " +
			COUPON_COUNT_TABLE_NAME +
			"(coupon_id integer primary key," +
			" count integer not null," +
			" create_time integer not null," +
			" last_update_time integer not null);";
	private static final String CREATE_CHANGE_LOG_TABLE = "create table " +
			CHANGE_LOG_TABLE_NAME +
			"(column_id integer primary key autoincrement," +
			" coupon_id integer not null," +
			" count integer not null," +
			" create_time integer not null)";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COUPON_COUNT_TABLE);
		database.execSQL(CREATE_CHANGE_LOG_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + COUPON_COUNT_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + CHANGE_LOG_TABLE_NAME);
		onCreate(database);
	}
	
	public void updateCouponCountEntry(CouponCountEntry newEntry) {
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
	
	public void addChangeLogEntry(ChangeLogEntry newEntry) {
		Log.d("addChangeLogEntry", newEntry.toString());

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("coupon_id", newEntry.getCouponId());
		values.put("count", newEntry.getCount());
		values.put("create_time", System.currentTimeMillis());

		db.insert(CHANGE_LOG_TABLE_NAME,
				null, // nullColumnHack
				values);

		db.close();
	}
	
	public List<ChangeLogEntry> getAllChangeLogEntries() {
		List<ChangeLogEntry> changeLogEntries = new LinkedList<ChangeLogEntry>();
		String query = "SELECT  * FROM " + CHANGE_LOG_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		ChangeLogEntry changeLogEntry = null;
		if (cursor.moveToFirst()) {
			do {
				changeLogEntry = new ChangeLogEntry();
				changeLogEntry.setId(Integer.parseInt(cursor.getString(0)));
				changeLogEntry.setCouponId(Integer.parseInt(cursor.getString(1)));
				changeLogEntry.setCount(Integer.parseInt(cursor.getString(2)));
				changeLogEntry.setCreateTime(Long.parseLong(cursor.getString(3)));

				changeLogEntries.add(changeLogEntry);
			} while (cursor.moveToNext());
		}

		//Log.d("getAllChangeLogEntries()", changeLogEntries.toString());
		return changeLogEntries;
	}
	
	public List<CouponCountEntry> getAllCouponCountEntries() {
		List<CouponCountEntry> couponCountEntries = new LinkedList<CouponCountEntry>();
		
		String query = "SELECT  * FROM " + COUPON_COUNT_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		CouponCountEntry couponCountEntry = null;
		if (cursor.moveToFirst()) {
			do {
				couponCountEntry = new CouponCountEntry();
				couponCountEntry.setCouponId(Integer.parseInt(cursor.getString(0)));
				couponCountEntry.setCount(Integer.parseInt(cursor.getString(1)));
				couponCountEntry.setCreateTime(Long.parseLong(cursor.getString(2)));
				couponCountEntry.setLastUpdateTime(Long.parseLong(cursor.getString(3)));

				couponCountEntries.add(couponCountEntry);
			} while (cursor.moveToNext());
		}

		//Log.d("getAllCouponCountEntries()", couponEntries.toString());
		return couponCountEntries;
	}
	
}
