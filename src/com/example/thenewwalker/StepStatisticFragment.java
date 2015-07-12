package com.example.thenewwalker;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StepStatisticFragment extends Fragment {

	public static int total_step = 0;
	public static int user_goal = 0;
	public static final int [] DAYS_OF_MONTH = 
			{31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; 
	private static final float DAYS_OF_WEEK = 7;
	
	private static View mView;
	
	private static Thread thread;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        total_step = StepDetector.CURRENT_STEP;
	        
	        mView.invalidate();

	    }
	 
	};
	
	private void mThread() {
	    if (thread == null) {
	 
	        thread = new Thread(new Runnable() {
	           public void run() {
	                while (true) {
	                    try {
	                        Thread.sleep(200);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                    if (StepServices.flag) {
	                        Message msg = new Message();
	                        handler.sendMessage(msg);
	                    }
	                }
	            }
	        });
	        thread.start();
	    }
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		//View mView = inflater.inflate(R.layout.fragment_step_count, container, false);
		
		getActivity().setTitle("行走统计");
		if (mView == null)
			mView = new StatisticView(getActivity());
		((StatisticView) mView).clearPointsList();
		init();
		mView.invalidate();
		
        return mView;
    }
	
	
	private void init() {
		
		SharedPreferences reader = getActivity().getSharedPreferences("userProfile", 0);
		String goalString = reader.getString("goal", "0");

		if (!goalString.isEmpty())
			user_goal = Integer.parseInt(goalString);
		else
			user_goal = 0;

		loadWeeklyData();
		
        //mThread();
	}
	
	private void loadWeeklyData() {
		Calendar mCalendar = Calendar.getInstance();
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		int date = year * 10000 + month * 100 + day;
		int daysOfMonth = 0;
		
		int lastYear, lastMonth, lastDay, theFirstDay;
		if (day < 7) {
			if (month == 3)
				if ((year % 100 != 0 && year % 4 == 0) || year % 400 == 0)
					daysOfMonth = 29;
				else
					daysOfMonth = 28;
			else
				daysOfMonth = DAYS_OF_MONTH[(month + 10) % 12];
			if (month == 1)
				lastYear = year - 1;
			else
				lastYear = year;
			lastMonth = (month + 10) % 12 + 1;
			theFirstDay = daysOfMonth + (day - 6);
			
		} else {
			lastYear = year;
			lastMonth = month;
			theFirstDay = day - 6;
		}
		SharedPreferences reader = getActivity().getSharedPreferences("dataOfDate", 0);
		int curDate = lastYear * 10000 + lastMonth * 100 + theFirstDay;
		int tempData;
		for (int i = 0; i < DAYS_OF_WEEK - 1; i++) {
			tempData = reader.getInt(curDate+"", 0);
			((StatisticView) mView).setLinePoint(curDate, tempData);
			theFirstDay++;
			if (theFirstDay > DAYS_OF_MONTH[lastMonth - 1]) {
				theFirstDay = 1;
				lastMonth = month;
				curDate = lastYear * 10000 + lastMonth * 100 + theFirstDay;
			} else {
				curDate++;
			}
		}
		reader = getActivity().getSharedPreferences("tempfile", 0);
		tempData = reader.getInt("tempData", 0);
		((StatisticView) mView).setLinePoint(curDate, tempData);
		((StatisticView) mView).adjustPoint();
	}
}
