package com.uttam.couponmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.uttam.couponmanager.R;
import com.uttam.couponmanager.db.MySQLiteHelper;
import com.uttam.couponmanager.model.ChangeLog;
import com.uttam.couponmanager.model.ChangeLog.ChangeType;
import com.uttam.couponmanager.model.Coupon;
import com.uttam.couponmanager.model.CouponCount;
import com.uttam.couponmanager.util.ViewUtil;

public class UpdateActivity extends ActionBarActivity {
	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		Spinner spinner = (Spinner) findViewById(R.id.coupons_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.coupons_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner) findViewById(R.id.change_type_spinner);
		adapter = ArrayAdapter.createFromResource(this,
		        R.array.change_type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		dbHelper = new MySQLiteHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateCouponCount(View v) {
		Spinner spinner = (Spinner) findViewById(R.id.change_type_spinner);
		ChangeType changeType = ChangeType.valueOf((String) spinner.getSelectedItem());
		
		spinner = (Spinner) findViewById(R.id.coupons_spinner);
		Integer couponId = Coupon.getId((String) spinner.getSelectedItem());
		
		EditText editText = (EditText) findViewById(R.id.checkoutAmountText);
		Integer count;
		try {
			count = Integer.parseInt(editText.getText().toString());
		} catch (NumberFormatException e) {
			ViewUtil.showToast(this, "Please enter a value for count.");
			return;
		}
		
		
    	CouponCount entry = dbHelper.getCouponCount(couponId);
    	if(entry == null) {
    		entry = new CouponCount();
    		entry.setCoupon(dbHelper.getCoupon(couponId));
        	entry.setCount(0);
    	}
    	
    	int newCount = 0;
    	if(ChangeType.ADD.equals(changeType)) {
    		newCount = entry.getCount() + count;
    	} else if(ChangeType.REMOVE.equals(changeType)) {
    		newCount = entry.getCount() - count;
    		if(newCount < 0) {
    			newCount = 0;
    		}
    	} else if(ChangeType.SET.equals(changeType)) {
    		newCount = count;
    	} else {
    		throw new UnsupportedOperationException("Unrecognized change type: " + changeType);
    	}
    	
		entry.setCount(newCount);
    	dbHelper.updateCouponCountEntry(entry);
    	
    	ChangeLog changeLogEntry = new ChangeLog();
    	changeLogEntry.setCouponId(couponId);
    	changeLogEntry.setChangeType(changeType);
    	changeLogEntry.setCount(count);
    	dbHelper.addChangeLogEntry(changeLogEntry);
    	
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}
}
