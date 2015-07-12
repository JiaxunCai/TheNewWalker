package com.example.thenewwalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class StatisticView extends View {
	
	private static final String X_KEY = "Xpos";  
    private static final String Y_KEY = "Ypos";
    
    private static float width, rangX, rangY;
    private static float MARGIN = 100;
    private static float ADJUST_MARGIN = 80;
    private static float FONT_SIZE = 40;
    private static float TITLE_SIZE = 50;
    private static float GRAPHIC_SIZE = 25;
    private static float LINE_WIDTH = 3;
    private static float GRAPHIC_MARGIN = 3;
    
    private static final float DAYS_OF_WEEK = 7;
    
    private static final String TITLE_STR = "最近一周行走统计";
    private static final String TODAY_STR = "今日已走步数";
    private static final String TODAY_GOAL = "离目标步数还差";
    
    private static Paint mPaint;
    private static int myRed, myBlue;
    private List<Map<String, Integer>> mListPoint = new ArrayList<Map<String,Integer>>();  
    private List<Map<String, Float>> pointAdjust = new ArrayList<Map<String,Float>>(); 
	public StatisticView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		int height = size.y;
		
		GRAPHIC_MARGIN = height / 630;
		GRAPHIC_SIZE = width / 30;
		MARGIN = width / 12;
		LINE_WIDTH = width / 360;
		ADJUST_MARGIN = MARGIN * 8 / 10;
		FONT_SIZE = MARGIN / 2;
		TITLE_SIZE = MARGIN * 2 / 3;
		rangX = width - MARGIN - MARGIN;
		rangY = width - MARGIN;
		
		if (mPaint == null)
			mPaint = new Paint();
		
		myRed = Color.rgb(210, 80, 60);
		myBlue = Color.rgb(40, 180, 250);
	}
	
	protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
          
        mPaint.setColor(Color.RED);  
        mPaint.setAntiAlias(true);  
        
        canvas.drawLine(MARGIN, MARGIN, MARGIN, rangY + MARGIN, mPaint);
        canvas.drawLine(MARGIN, rangY + MARGIN, rangX + MARGIN, rangY + MARGIN, mPaint);
        
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(MARGIN, MARGIN, rangX + MARGIN, MARGIN, mPaint);
        canvas.drawLine(rangX + MARGIN, rangY + MARGIN, rangX + MARGIN, MARGIN, mPaint);
        
        mPaint.setTextAlign(Paint.Align.LEFT);
    	mPaint.setTextSize(TITLE_SIZE);
    	canvas.drawText(TITLE_STR, MARGIN, MARGIN - FONT_SIZE / 4, mPaint);
    	
        int index;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(FONT_SIZE);
        for (index = 0; index < pointAdjust.size() - 1; index++)  
        {
        	mPaint.setColor(Color.GRAY);
            canvas.drawText(mListPoint.get(index).get(Y_KEY) + "",
            		pointAdjust.get(index).get(X_KEY),
            		pointAdjust.get(index).get(Y_KEY) - 5 * GRAPHIC_MARGIN,
            		mPaint);
            String date = (mListPoint.get(index).get(X_KEY) % 10000) / 100 + "";
            date += "/" + mListPoint.get(index).get(X_KEY) % 100;
            canvas.drawText(date + "", pointAdjust.get(index).get(X_KEY),
            		rangY + MARGIN + ADJUST_MARGIN, mPaint);
         
        	mPaint.setColor(myRed);
        	mPaint.setStrokeWidth(GRAPHIC_SIZE);
       	
        	canvas.drawLine(pointAdjust.get(index).get(X_KEY),
            		pointAdjust.get(index).get(Y_KEY) - GRAPHIC_MARGIN,  
            		pointAdjust.get(index).get(X_KEY), 
            		rangY + MARGIN, mPaint);

            if (index > 0)  
            {  
            	mPaint.setStrokeWidth(LINE_WIDTH);
                canvas.drawLine(pointAdjust.get(index-1).get(X_KEY),
                		pointAdjust.get(index-1).get(Y_KEY),  
                		pointAdjust.get(index).get(X_KEY), 
                		pointAdjust.get(index).get(Y_KEY), mPaint);  
                
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 
                		Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));   
            }  
            
        } 
        mPaint.setStrokeWidth(LINE_WIDTH);
        float goalYAdjust = pointAdjust.get(index).get(Y_KEY);
        mPaint.setColor(myBlue);
        canvas.drawLine(MARGIN, goalYAdjust, rangX + MARGIN, goalYAdjust, mPaint);
        canvas.drawText(StepStatisticFragment.user_goal + "", rangX + MARGIN,
        		goalYAdjust, mPaint);
        
        mPaint.setTextAlign(Paint.Align.LEFT);
    	mPaint.setTextSize(TITLE_SIZE);
    	canvas.drawText(TODAY_STR + mListPoint.get(index - 1).get(Y_KEY),
    			MARGIN, 3 * MARGIN + rangY, mPaint);
    	int different = StepStatisticFragment.user_goal - mListPoint.get(index - 1).get(Y_KEY);
    	different = different > 0 ? different : 0;
    	canvas.drawText(TODAY_GOAL + different, MARGIN, 4 * MARGIN + rangY, mPaint);
    }  
    /** 
     * @param curX  which x position you want to draw. 
     * @param curY  which y position you want to draw. 
     * @see all you put x-y position will connect to a line. 
     */  
    public void setLinePoint(int curX, int curY)  
    {  
        Map<String, Integer> temp = new HashMap<String, Integer>();
        
        temp.put(X_KEY, curX);  
        temp.put(Y_KEY, curY);  
        mListPoint.add(temp);
        invalidate();  
    }  
    
    public void clearPointsList()
    {
    	mListPoint.clear();
    	pointAdjust.clear();
    }
    
    public void adjustPoint() 
    {
    	int bigger = 0, smaller = 100000000;
    	for (int index = 0; index < mListPoint.size(); index++) {
    		if (mListPoint.get(index).get(Y_KEY) > bigger)
    			bigger = mListPoint.get(index).get(Y_KEY);
    		if (mListPoint.get(index).get(Y_KEY) < smaller)
    			smaller = mListPoint.get(index).get(Y_KEY);
    	}
    	int rangeOfSteps;
    	if (bigger < StepStatisticFragment.user_goal)
    		bigger = StepStatisticFragment.user_goal;
    	if (bigger > smaller)
    		rangeOfSteps = (bigger - smaller) * 5 / 4;
    	else
    		rangeOfSteps = 10;
    	for (int index = 0; index<mListPoint.size(); index++) {
    		Map<String, Float> temp = new HashMap<String, Float>();
    		float curXAdjust = (rangX - ADJUST_MARGIN) * index / DAYS_OF_WEEK 
    				+ MARGIN + ADJUST_MARGIN;
    		int tempStep = mListPoint.get(index).get(Y_KEY);
    		float curYAdjust = (rangY - ADJUST_MARGIN) * tempStep / rangeOfSteps;
    		curYAdjust = rangY + MARGIN - curYAdjust;
    		temp.put(X_KEY, curXAdjust);  
            temp.put(Y_KEY, curYAdjust);  
            pointAdjust.add(temp);
    	}
    	Map<String, Float> temp = new HashMap<String, Float>();
    	float curYAdjust = (rangY - ADJUST_MARGIN) * StepStatisticFragment.user_goal
    			/ rangeOfSteps;
    	curYAdjust = rangY + MARGIN - curYAdjust;
    	temp.put(Y_KEY, curYAdjust);
    	pointAdjust.add(temp);
    }
}
