package com.example.thenewwalker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;

public class StepServices extends Service {

	public static Boolean flag = false;
	
	private static final long ALARM_INTERVAL = 3 * 1000;
	private static final int BACKUP_INTERVAL = 500;
	private static final int REQUEST_CODE = 10086;
	
	private SensorManager sensorManager;
	private StepDetector stepDetector;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	public void onCreate() {
		super.onCreate();
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);  
		PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),  
		        REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		// 5秒后发送广播，然后每个10秒重复发广播。广播都是直接发到AlarmReceiver的  
		long triggerAtTime = SystemClock.elapsedRealtime();  
		
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, ALARM_INTERVAL, pendIntent); 
		
	    //这里开启了一个线程，因为后台服务也是在主线程中进行，这样可以安全点，防止主线程阻塞
	    new Thread(new Runnable() {
	        public void run() {
	            startStepDetector();
	        }
	    }).start();
	        
	    new Thread(new Runnable() {
		    public void run() {
		        while (true) {
		            try {
		                Thread.sleep(BACKUP_INTERVAL);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }

		            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("tempfile", 0).edit();
		            editor.putInt("tempData", StepDetector.CURRENT_STEP);
		            editor.commit();
		        }
		    }
		}).start();

	 }
	
	 private void startStepDetector() {
	        flag = true;
	        stepDetector = new StepDetector(this);
	        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);//获取传感器管理器的实例

	        SharedPreferences reader = getApplicationContext().getSharedPreferences("tempfile", 0);
	        StepDetector.CURRENT_STEP = reader.getInt("tempData", 0);
            
	        Sensor sensor1 = sensorManager
	                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	        Sensor sensor2 = sensorManager
	                .getDefaultSensor(Sensor.TYPE_GRAVITY);
	        Sensor sensor3 = sensorManager
	                .getDefaultSensor(Sensor.TYPE_LIGHT);
	        
	        sensorManager.registerListener(stepDetector, sensor1,
	                SensorManager.SENSOR_DELAY_FASTEST);
	        sensorManager.registerListener(stepDetector, sensor2,
	                SensorManager.SENSOR_DELAY_FASTEST);
	        sensorManager.registerListener(stepDetector, sensor3,
	                SensorManager.SENSOR_DELAY_FASTEST);
	    }
	 
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 return super.onStartCommand(intent, flags, startId);
	 }
	 
	 @Override
	 public void onDestroy() {
		 super.onDestroy();
		 flag = false;
		 if (stepDetector != null) {
			 sensorManager.unregisterListener(stepDetector);
		}
	 }
	 
}

