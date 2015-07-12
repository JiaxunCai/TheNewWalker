package com.example.thenewwalker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class StepCountView extends View {
	
	private static final float CYCLE_ANGLE = 360;
	private static final float START_ANGLE = -90;
	private static final String GOAL_STRING = "目标 ";
	private static final String STEP_STRING = "步数";
	private static final int FLASH_FREQ = 50;
	private static final int STEADY_FREQ = 2;
	private static float flashR[] = new float[FLASH_FREQ];
	private static float steadyFlash[] = new float[STEADY_FREQ];
	
	private static final int mColors[] = new int[] { 0x50FF0000, 0x50FFFF00, 0x5000FF00,
		0x5000FFFF, 0x500000FF, 0x50FF00FF, 0x50FF0000  };
	private static final int nAlphaColors[] = new int[] { 0xA0FF0000, 0xA0FFFF00, 0xA000FF00,
		0xA000FFFF, 0xA00000FF, 0xA0FF00FF, 0xA0FF0000  };
	
	private static int myGreen, myAlphaGreen, myBlue, myGray; 

	private static float centerX = 0, centerY = 0, radius = 0;
	private static float MARGIN = 100;
	private static float FONT_SIZE = 270;
	private static float STROKE_WIDTH = 35;
	
	private static Shader mShader, nAlphaShader;
	
	public static boolean isSteady = false;
	
	public StepCountView(Context context) {
		super(context);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		float height = size.y;
		float width = size.x;
		MARGIN = width / 12;
		FONT_SIZE = MARGIN * 5 / 2;
		STROKE_WIDTH = width / 25;
		
		centerX = size.x / 2;
		centerY = centerX;
		radius = centerX - MARGIN;
		myGreen = Color.rgb(50, 200, 80);
		myBlue = Color.rgb(40, 180, 250);
		myGray = Color.rgb(215, 215, 215);
		myAlphaGreen = Color.argb(85, 160, 250, 180);
		
		float rangR = radius * 3 / 4;
		rangR /= FLASH_FREQ;
		for (int i=0; i < FLASH_FREQ; i++) {
			flashR[i] = radius / 4 + rangR * i;
		}
	    steadyFlash[0] = radius;
	    steadyFlash[1] = radius + STROKE_WIDTH * 2 / 3;
	    
	    mShader = new SweepGradient(centerX, centerY, mColors, null);
	    nAlphaShader = new SweepGradient(centerX, centerY, nAlphaColors, null);
	}
	
	@Override  
    public void onDraw(Canvas canvas) {  
        
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);  
        
        paint.setColor(myAlphaGreen);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH); 
        
        Paint mGradientPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    	mGradientPaint.setStyle(Paint.Style.STROKE);  
    	mGradientPaint.setStrokeWidth(STROKE_WIDTH);
    	
        if (isSteady) {
        	mGradientPaint.setShader(mShader);  
        	canvas.drawCircle(centerX, centerY, 
        		steadyFlash[StepDetector.steadyCount % STEADY_FREQ], mGradientPaint);
        } else {
        	canvas.drawCircle(centerX, centerY, 
        		flashR[StepDetector.flashCount % FLASH_FREQ], paint);
        }
        
        paint.setColor(myGray);      
        paint.setStrokeWidth(STROKE_WIDTH);  
        canvas.drawCircle(centerX, centerY, radius, paint);
        
        RectF oval = new RectF(MARGIN, MARGIN, centerX + radius, centerY + radius);
        
        paint.setColor(myGreen); 
        
        float angle;
        if (StepCountFragment.total_step < StepCountFragment.user_goal) {
        	angle = CYCLE_ANGLE * StepCountFragment.total_step / StepCountFragment.user_goal;
        } else {
        	angle = CYCLE_ANGLE;
        }
        
        mGradientPaint.setShader(nAlphaShader);  
        canvas.drawArc(oval, START_ANGLE, angle, false, mGradientPaint);
        
        //画出字符串 drawText(String text, float x, float y, Paint paint)   
        // y 是 基准线 ，不是 字符串的 底部
        
        //设置字体大小  
        paint.setTextSize(FONT_SIZE); 
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL);
        paint.setTypeface(font);
        //让画出的图形是实心的  
        paint.setStyle(Paint.Style.FILL); 
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(StepCountFragment.total_step + "", centerX, centerY + FONT_SIZE / 3, paint);
        
        paint.setColor(myBlue);
        paint.setTextSize(FONT_SIZE / 3);
        
        canvas.drawText(GOAL_STRING + StepCountFragment.user_goal, centerX, 
        		centerY + radius /2 , paint);
        canvas.drawText(STEP_STRING, centerX, 
        		centerY - radius / 2, paint);
        
        canvas.drawText(StepDetector.ACCELRATE+"", centerX, 
        		centerY + centerY + MARGIN, paint);
        canvas.drawText(StepDetector.XY+"", centerX, 
        		centerY + centerY + MARGIN + MARGIN, paint);
        canvas.drawText(StepDetector.Z+"", centerX, 
        		centerY + centerY + MARGIN + MARGIN + MARGIN, paint);
        
        super.onDraw(canvas);  
    }  
  
}
