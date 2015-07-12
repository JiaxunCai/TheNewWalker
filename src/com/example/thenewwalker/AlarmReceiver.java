package com.example.thenewwalker;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Calendar mCalendar = Calendar.getInstance();
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		int date = year * 10000 + month * 100 + day;
		
		SharedPreferences reader = context.getSharedPreferences("dataOfDate", 0);
		int rDate = reader.getInt("rDate", 0);
		
		if (rDate != date) {

			SharedPreferences.Editor editor = context.getSharedPreferences("dataOfDate", 0).edit();
	        editor.putInt("rDate", date);
	        editor.putInt(rDate+"", StepDetector.CURRENT_STEP);
	        editor.commit();
	        
	        editor = context.getSharedPreferences("tempfile", 0).edit();
	        editor.remove("tempData");
	        editor.commit();

	        Intent stepService = new Intent(context, StepServices.class);
	        context.stopService(stepService);
	        context.startService(stepService);
		}
		
	}

}
