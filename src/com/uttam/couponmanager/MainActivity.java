package com.uttam.couponmanager;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uttam.couponmanager.model.ChangeLogEntry;
import com.uttam.couponmanager.model.CouponCountEntry;
import com.uttam.couponmanager.model.MySQLiteHelper;


public class MainActivity extends ActionBarActivity {
	public static final String MY_APP_PREFS = "MyAppPrefs";
	public static final String BUTTON_CLICKS_COUNT = "button_pressed_count";
	
	private MySQLiteHelper dbHelper;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MySQLiteHelper(this);
        
        TextView textView = (TextView)findViewById(R.id.textView1);
        textView.setMovementMethod(new ScrollingMovementMethod());
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
    
    public void buttonClicked(View v){
    	SharedPreferences thePrefs = getSharedPreferences(MY_APP_PREFS, 0);
    	int buttonPressCount = thePrefs.getInt(BUTTON_CLICKS_COUNT, 0);
    	
    	SharedPreferences.Editor prefsEd = thePrefs.edit();
    	prefsEd.putInt(BUTTON_CLICKS_COUNT, buttonPressCount + 1);
    	prefsEd.commit();
    	
    	
    	TextView textView = (TextView)findViewById(R.id.textView1);
    	textView.setText("Preferences updated!");
    }
    
    public void showButtonClicksCount(View v) {
    	SharedPreferences thePrefs = getSharedPreferences(MY_APP_PREFS, 0);
    	int buttonClicksCount = thePrefs.getInt(BUTTON_CLICKS_COUNT, 0);
    	
    	TextView textView = (TextView)findViewById(R.id.textView1);
    	textView.setText("Button clicks count: " + buttonClicksCount);
    }
    
    public void addChangeLogEntry(View v) {
    	ChangeLogEntry newEntry = new ChangeLogEntry();
    	newEntry.setCouponId(1);
    	newEntry.setCount(3);
    	
    	dbHelper.addChangeLogEntry(newEntry);
    	showChangeLogEntries(v);
    }
    
    public void showChangeLogEntries(View v) {
    	List<ChangeLogEntry> changeLogEntries = dbHelper.getAllChangeLogEntries();
    	StringBuilder outputText = new StringBuilder();
    	
    	outputText.append("Total : " + changeLogEntries.size() + "\n");
    	for(ChangeLogEntry entry : changeLogEntries) {
    		outputText.append(entry.toString());
    		outputText.append("\n");
    	}
    	
    	TextView textView = (TextView)findViewById(R.id.textView1);
    	textView.setText(outputText);
    	textView.scrollTo(0, 0);
    }
    
    public void updateCouponCountEntry(View v) {
    	CouponCountEntry newEntry = new CouponCountEntry();
    	newEntry.setCouponId(2);
    	newEntry.setCount(4);
    	
    	dbHelper.updateCouponCountEntry(newEntry);
    	showCouponCountEntries(v);
    }
    
    public void showCouponCountEntries(View v) {
    	List<CouponCountEntry> couponCountEntries = dbHelper.getAllCouponCountEntries();
    	StringBuilder outputText = new StringBuilder();
    	
    	outputText.append("Total : " + couponCountEntries.size() + "\n");
    	for(CouponCountEntry entry : couponCountEntries) {
    		outputText.append(entry.toString());
    		outputText.append("\n");
    	}
    	
    	TextView textView = (TextView)findViewById(R.id.textView1);
    	textView.setText(outputText);
    	textView.scrollTo(0, 0);
    }
}
