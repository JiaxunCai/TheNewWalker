package com.example.thenewwalker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
 
public class StepDetector implements SensorEventListener {
 
    private static final float GRAVITY = 9.08665f;
    private static final float NOISE = 0.0001f;
    private static final float ZSCALE = 0.8660f;       //用于消除Z轴的影响，检测手机3轴状态
    private static final long MININTERVAL = 200;    //两次计数之间的最小时间间隔
    private static final long MAXINTERVAL = 2000;    //两次计数之间的最大时间间隔
    private static final long DETECTTOLERANCE = 800;

	public static int CURRENT_STEP = 0;
	
	private static float THREDHOLD = 5.0f;
	
	public static double ACCELRATE = 0;
	public static double XY = 0;
	public static double Z = 0;
	
    private ArrayList<Double> detectedDataOfOneStep = new ArrayList<Double>();
    private ArrayList<Double> filtedDataOfOneStep = new ArrayList<Double>();
    
    private ArrayList<Double> thredholdBackup = new ArrayList<Double>();
    private ArrayList<Double> biggestValue = new ArrayList<Double>();
    
    private static long start = 0;   //用于计算两次计步之间的时间间隔，消除噪点
    private static long end = 0;
    private static long lastDetect = 0;
    
    private static boolean gravityRead = false;
    private static boolean accelerationRead = false;
    private static boolean isInHand = false;
    
    private float xGravity;
    private float yGravity;
    private float zGravity;
    private float xAcceleration;
    private float yAcceleration;
    private float zAcceleration;
    
    
    public static double lastBigger;
    public static double lastSmaller;
    public static double zBigger;
    public static double zSmaller;
    
    public static Context mContext;
    
    private static final double TOLERANCE = 0.2;
    
    private double vertical;
    private static double lastInterval;
    private static double intervalSum = 0;
    private static int pendingCount = 0;
    private static int pendingSteps = 0; 
    
    public static int flashCount = 0;
    public static int steadyCount = 0;
    
    private static double pin;
    
    private float light;
    /**
     * 传入上下文的构造函数
     * 
     * @param context
     */
    public StepDetector(Context context) {
        super();
        mContext = context;
    }
    
    //当传感器检测到的数值发生变化时就会调用这个方法
    public void onSensorChanged(SensorEvent event) {
    	
        Sensor sensor = event.sensor;
        
        synchronized (this) {
        	
        	if (sensor.getType() == Sensor.TYPE_LIGHT) {
        		light = event.values[0];
        		if (light > 5) {
        			pin = 6;
        			isInHand = true;
        		} else {
        			pin = 3;
        			isInHand = false;
        		}
        	}	
        	
        	if (sensor.getType() == Sensor.TYPE_GRAVITY) {
        		xGravity = event.values[0];
        		yGravity = event.values[1];
        		zGravity = event.values[2];
        		gravityRead = true;
        	}
        	
        	if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
        		xAcceleration = event.values[0];
        		yAcceleration = event.values[1];
        		zAcceleration = event.values[2];
        		accelerationRead = true;
        	}
        	
        	if (gravityRead && accelerationRead) {

        		
        		if (zGravity < GRAVITY*ZSCALE) {
        			double lenA = Math.sqrt(xAcceleration * xAcceleration + 
        					yAcceleration * yAcceleration +
        					zAcceleration * zAcceleration);
            		
        			double lenG = Math.sqrt(xGravity*xGravity + yGravity*yGravity 
            				+ zGravity*zGravity);
            		double AG = xGravity*xAcceleration + yGravity*yAcceleration
            				+ zGravity*zAcceleration;
            		double cosAG = AG / (lenA * lenG);
            		vertical = - lenA * cosAG;
            		
            		int stepIncrease;	
					
					if (!isInHand) {
						stepIncrease = howManyStep(vertical);
					} else {
						stepIncrease = countInHand(vertical);
					}
					
					if (stepIncrease > 0) {
                		end = System.currentTimeMillis();
                		if (start == 0)
                			start = end;
                		if (lastDetect == 0)
                			lastDetect = end;
                		long interval = end - start;
                		if (interval > MAXINTERVAL){
                    		lastInterval = 0;
                    		pendingSteps = 0;
            				pendingCount = 0;
            				intervalSum = 0;
            				start = end;
            				lastDetect = end;
                    	} else if (interval >= MININTERVAL) {
                    		if (!isInHand) {
                    			CURRENT_STEP += stepIncrease;
                    			start = end;
                    		}
                    	}
//                			if (isInHand && (interval > lastInterval * (1 + TOLERANCE) ||
//                				(interval < lastInterval * (1 - TOLERANCE)))) {
//                				if (end - lastDetect <= DETECTTOLERANCE) {
//                					pendingSteps += stepIncrease;
//                    				intervalSum += interval;
//                    				pendingCount++;
//                    				StepCountView.isSteady = false;
//                    				flashCount += 5;
//                    				if (flashCount > 1000000000)
//                    		        	flashCount = 0;
//                    				lastDetect = end;
//                				} else {
//                					pendingSteps = 0;
//                    				pendingCount = 0;
//                    				intervalSum = 0;
//                    				lastDetect = end;
//                    				flashCount = 0;
//                				}
//                				
//                				if (pendingCount >= 10) {
//                					StepCountView.isSteady = true;
//                					steadyCount = 1;
//                    				new Timer().schedule(new TimerTask() {
//                    			        @Override
//                    			        public void run() {
//                    			            steadyCount = 0;
//                    			            cancel();
//                    			        }
//                    			    }, 100);
//                    				
//                    				CURRENT_STEP += pendingSteps;
//                    				lastInterval = intervalSum / pendingCount;
//                    				pendingSteps = 0;
//                    				pendingCount = 0;
//                    				intervalSum = 0;
//                    				flashCount = 0;
//                    			}
//                				
//                			} else {
//            					StepCountView.isSteady = true;
//                				steadyCount = 1;
//                				new Timer().schedule(new TimerTask() {
//                			        @Override
//                			        public void run() {
//                			            steadyCount = 0;
//                			            cancel();
//                			        }
//                			    }, 100);
//                			     
//                    			CURRENT_STEP += stepIncrease;
//                    			lastInterval = interval;
//                    			pendingSteps = 0;
//                				pendingCount = 0;
//                				intervalSum = 0;
//                			}
//                			start = end;
//                		} 
                		

                	} 
        		}
        		
        	}
 
        }
    }
    
    double llStep, lStep, cStep, maxMax = 0, minMin = 0;
    int maxInterval = 0, minInterval = 0;
    
    private int howManyStep(double inputStep) {
    	llStep = lStep;
    	lStep = cStep;
    	cStep = inputStep;
    	int count = 0;
    	
    	if (lStep > cStep && lStep > llStep && lStep > 0) {
			maxInterval++;
			if (lStep > maxMax) {
				maxMax = lStep;
				maxInterval = 0;
			}
		}
    	
		
		if (lStep < cStep && lStep < llStep && lStep < 0) {
			minInterval++;
			if (lStep < minMin) {
				minMin = lStep;
				minInterval = 0;
			}
		}
		
		if (maxInterval >= pin && minInterval >= pin) {
			if (maxInterval >= pin*2 || minInterval >= pin*2)
				count++;
			count++;
			maxInterval=0;
			minInterval=0;
			maxMax = 0;
			minMin = 0;
		}
		return count;
    }
    
    double cInStep, lInStep, llInStep, smallValue, lsmallValue, llsmallValue;
    int inMaxInterval = 0, inMinInterval = 0;
    
    private int countInHand(double inputStep) {

    	llInStep = lInStep;
    	lInStep = cInStep;
    	cInStep = inputStep;
    	int count = 0;
    	
    	if (lInStep < cInStep - NOISE && lInStep < llInStep + NOISE) {
    		llsmallValue = smallValue;
			lsmallValue = smallValue;
			smallValue = lInStep;
		}
    	if (llsmallValue > NOISE && lsmallValue > NOISE && smallValue < NOISE) {
			count = 1;
		}
    	
		return count;
    }
    
    public void onAccuracyChanged(Sensor arg0, int arg1) {
 
    }
    
}
