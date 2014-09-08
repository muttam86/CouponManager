package com.uttam.couponmanager.activities;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uttam.couponmanager.R;
import com.uttam.couponmanager.db.MySQLiteHelper;
import com.uttam.couponmanager.model.ChangeLog;
import com.uttam.couponmanager.model.ChangeLog.ChangeType;
import com.uttam.couponmanager.model.Coupon;
import com.uttam.couponmanager.model.CouponCount;
import com.uttam.couponmanager.util.Calculator;
import com.uttam.couponmanager.util.ViewUtil;


public class MainActivity extends ActionBarActivity {
	private MySQLiteHelper dbHelper;
	private List<CouponCount> checkoutCoupons;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MySQLiteHelper(this);
        showCouponCountEntries(getCurrentFocus());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        
    public void showChangeLogEntries(View v) {
    	TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
    	tableLayout.removeAllViews();
    	Map<Integer, Coupon> coupons = dbHelper.getAllCoupons();
    	List<ChangeLog> changeLogEntries = dbHelper.getAllChangeLogEntries();
    	
    	TableRow headerRow = new TableRow(this);
    	
    	TextView couponIdHeader = new TextView(this);
    	couponIdHeader.setText("Coupon");
    	headerRow.addView(couponIdHeader);
    	
    	TextView changeTypeHeader = new TextView(this);
    	changeTypeHeader.setText("Type");
    	headerRow.addView(changeTypeHeader);
    	
    	TextView couponCountHeader = new TextView(this);
    	couponCountHeader.setText("Count");
    	headerRow.addView(couponCountHeader);
    	
    	TextView activityTimeHeader = new TextView(this);
    	activityTimeHeader.setText("Date");
    	headerRow.addView(activityTimeHeader);
    	
    	tableLayout.addView(headerRow);
    	
    	
    	for(ChangeLog entry : changeLogEntries) {
    		TableRow row = new TableRow(this);
    		
    		TextView couponView = new TextView(this);
    		Coupon coupon = coupons.get(entry.getCouponId());
    		couponView.setText(coupon.getName() + "-" + coupon.getValue());
    		row.addView(couponView);
    		
    		TextView changeTypeView = new TextView(this);
    		changeTypeView.setText(entry.getChangeType().name());
    		row.addView(changeTypeView);
    		
    		TextView couponCountView = new TextView(this);
    		couponCountView.setText(entry.getCount() + "");
    		row.addView(couponCountView);
    		
    		TextView createTimeView = new TextView(this);
    		String dateTime = DateFormat.getDateInstance()
    							.format(new Date(entry.getCreateTime()));
    		createTimeView.setText(dateTime);
    		row.addView(createTimeView);
    		
    		tableLayout.addView(row);
    	}
    }
    
    public void updateCouponCountEntry(View v) {
    	Intent intent = new Intent(this, UpdateActivity.class);
    	startActivity(intent);
    }
    
    public void showCouponCountEntries(View v) {
    	TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
    	List<CouponCount> couponCountEntries = dbHelper.getAllCouponCountEntries();
    	Collections.sort(couponCountEntries, CouponCount.Comparators.COUPON_VALUE_REVERSE);
    	buildCouponCountTable(tableLayout, couponCountEntries);
    	
    }
    
    public void checkout(View v) {
    	ViewUtil.hideSoftKeyboard(this);
    	
    	EditText checkoutText = (EditText) findViewById(R.id.checkoutAmountText);
    	String text = checkoutText.getText().toString();
    	if(text == null || text.length() == 0) {
    		ViewUtil.showToast(this, "Please enter a value for checkout.");
    		return;
    	}
    	
    	List<CouponCount> couponCountEntries = dbHelper.getAllCouponCountEntries();
    	int checkoutAmount = Integer.parseInt(text);
    	// FIXME: Do not use instance variable
    	checkoutCoupons = Calculator.checkout(couponCountEntries, checkoutAmount);
    	TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
    	buildCouponCountTable(tableLayout, checkoutCoupons);
    	
    	int payByCashAmount = checkoutAmount - CouponCount.getTotalValue(checkoutCoupons);
    	// Add footer row for pay by cash amount
    	TableRow footerRow = new TableRow(this);
    	TextView dummy = new TextView(this);
    	dummy.setText("");
    	footerRow.addView(dummy);
    	dummy = new TextView(this);
    	dummy.setText("");
    	footerRow.addView(dummy);
    	dummy = new TextView(this);
    	dummy.setText("Pay by Cash:");
    	footerRow.addView(dummy);
    	TextView grandTotalFooter = new TextView(this);
    	grandTotalFooter.setText(payByCashAmount + "");
    	footerRow.addView(grandTotalFooter);
    	
    	tableLayout.addView(footerRow);
    	
    	// Add button to use these coupons
    	Button useButton = new Button(this);
    	useButton.setText("Use Coupons");
    	useButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				useCoupons();
			}
		});
    	tableLayout.addView(useButton);
    }
    
    // FIXME: do not use instance variable
    private void useCoupons() {
    	if(checkoutCoupons == null || checkoutCoupons.size() == 0) {
    		return;
    	}
    	
    	for(CouponCount usedCouponCount : checkoutCoupons) {
    		CouponCount existingCouponCount = dbHelper.getCouponCount(usedCouponCount.getCoupon().getId());
    		int newCount = existingCouponCount.getCount() - usedCouponCount.getCount();
    		if(newCount < 0) {
    			new RuntimeException("Oops! Something went wrong. Negative count!");
    		}
    		existingCouponCount.setCount(newCount);
    		dbHelper.updateCouponCountEntry(existingCouponCount);
    		
    		// Add change log entry
    		ChangeLog changeLogEntry = new ChangeLog();
        	changeLogEntry.setCouponId(usedCouponCount.getCoupon().getId());
        	changeLogEntry.setChangeType(ChangeType.REMOVE);
        	changeLogEntry.setCount(usedCouponCount.getCount());
        	dbHelper.addChangeLogEntry(changeLogEntry);
    	}
    	showCouponCountEntries(getCurrentFocus());
    }
    
    private void buildCouponCountTable(TableLayout tableLayout, List<CouponCount> couponCounts) {
    	tableLayout.removeAllViews();
    	
    	// Header
    	TableRow headerRow = new TableRow(this);
    	TextView couponNameHeader = new TextView(this);
    	couponNameHeader.setText("Name");
    	headerRow.addView(couponNameHeader);
    	TextView couponValueHeader = new TextView(this);
    	couponValueHeader.setText("Value");
    	headerRow.addView(couponValueHeader);
    	TextView couponCountHeader = new TextView(this);
    	couponCountHeader.setText("Count");
    	headerRow.addView(couponCountHeader);
    	TextView totalValueHeader = new TextView(this);
    	totalValueHeader.setText("Total Value");
    	headerRow.addView(totalValueHeader);
    	
    	tableLayout.addView(headerRow);
    	
    	// Content
    	int grandTotal = 0;
    	for(CouponCount entry : couponCounts) {
    		TableRow row = new TableRow(this);
    		
    		TextView couponNameView = new TextView(this);
    		couponNameView.setText(entry.getCoupon().getName() + "");
    		row.addView(couponNameView);
    		
    		TextView couponValueView = new TextView(this);
    		couponValueView.setText(entry.getCoupon().getValue() + "");
    		row.addView(couponValueView);
    		
    		TextView couponCountView = new TextView(this);
    		couponCountView.setText(entry.getCount() + "");
    		row.addView(couponCountView);
    		
    		TextView totalValueView = new TextView(this);
    		int totalValue = entry.getCoupon().getValue() * entry.getCount();
    		totalValueView.setText(totalValue  + "");
    		row.addView(totalValueView);
    		
    		grandTotal += totalValue;
    		tableLayout.addView(row);
    	}
    	
    	// Footer
    	TableRow footerRow = new TableRow(this);
    	TextView dummy = new TextView(this);
    	dummy.setText("");
    	footerRow.addView(dummy);
    	dummy = new TextView(this);
    	dummy.setText("");
    	footerRow.addView(dummy);
    	dummy = new TextView(this);
    	dummy.setText("Grand Total:");
    	footerRow.addView(dummy);
    	TextView grandTotalFooter = new TextView(this);
    	grandTotalFooter.setText(grandTotal + "");
    	footerRow.addView(grandTotalFooter);
    	
    	tableLayout.addView(footerRow);
    }
}
