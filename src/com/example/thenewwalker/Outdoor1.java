package com.example.thenewwalker;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class Outdoor1 extends Observable{

	// 百度地图相关
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode = null;
	private BitmapDescriptor mCurrentMarker;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;

	private MyLocationData locData;
	private float locDirection = 0;

	// BDm Location
	private com.baidu.location.LocationClientOption.LocationMode tempMode = com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy;

	public static boolean isFirstLoc = true;
	public static List<LatLng> points = new ArrayList<LatLng>();
	public static int pointCounts = 0;
	
	private TextView outdoorRadius;
	private static TextView outdoorHi;
	private static TextView outdoorLo;
	static double hi = 0;
	static double lo = 99999;
	public static double runTime;
	public static double runSpeed;
	Context mContext = null;
	FrameLayout container;
	
	boolean showTime;

	public Outdoor1(final Context context) {
		Log.i("Outdoor", "Setting up outdoor.class");
		
		//setUpBDmap(context);
		//setUpSensor(context);
	}

	public void setUpBDmap(final Context context, MapView mMapView, View fragmentView) {
		// 获取父view
		mContext = context;
		Log.i("Outdoor", "why dont you show this line?");

		FrameLayout mFramLayout = (FrameLayout) fragmentView.findViewById(R.id.frameLayout_outdoor);
		outdoorRadius = (TextView) fragmentView.findViewById(R.id._outdoor_radius);
		outdoorLo = (TextView) fragmentView.findViewById(R.id._outdoor_lo);
		outdoorHi = (TextView) fragmentView.findViewById(R.id._outdoor_hi);
		outdoorHi.setText("You are half way there");
		// mMapView = (MapView) layout.findViewById(R.id.bmapView);
		Log.i("tinkerOutdoor", "You should notice this very long scentence " + mFramLayout.getId());
		mFramLayout.addView(mMapView);
		
		mBaiduMap = mMapView.getMap();
		// 设置定位模式为普通
		mCurrentMode = LocationMode.FOLLOWING;
		// 设置指针类型为默认箭头
		mCurrentMarker = null;
		// 应用定位模式与指针类型的设置
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		// mLocationClient = new LocationClient(getApplicationContext()); //
		// 声明LocationClient类
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		// setUpSensor(context); // 设置方向传感器。
	}

	public void setUpSensor(final Context context) {
		// 传感器管理器，百度地图中没有实现手机方向感测，需要通过手机内置陀螺仪感应。
		SensorManager sm = (SensorManager) context
				.getSystemService(context.SENSOR_SERVICE);
		// 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);
		// SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))
		sm.registerListener(
				new SensorEventListener() {
					// 用于传感器监听中，设置灵敏程度
					int mIncrement = 1;

					@Override
					public void onSensorChanged(SensorEvent event) {
						// 方向传感器
						if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
							// x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
							float x = event.values[SensorManager.DATA_X];
							// Log.e("x",x+"");
							mIncrement++;
							if (mIncrement >= 7) {
								locDirection = x;
								if (!isFirstLoc) {
									// 修改定位图标方向
									// locData.
									// =======================================
									/*
									locData = new MyLocationData.Builder()
											.accuracy(locData.accuracy)
											.direction(x)
											.latitude(locData.latitude)
											.longitude(locData.longitude)
											.build();
									// locData.direction = x;
									// 重新设置当前位置数据
									mBaiduMap.setMyLocationData(locData);
									*/
									// =======================================
									Intent intent = new Intent(OutdoorFragment.DIRECTION_CHANGED);
									intent.putExtra("direction", x);
									LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
								}
								// myLocationOverlay.setData(locData);
								// mMapView.refresh();
								mIncrement = 1;
								// Log.i("direction", "" + locDirection);
							}
						}
					}

					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						// TODO Auto-generated method stub

					}

				}, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			runTime++; // 每次收到请求，说明时间度过了一秒
			if (location == null)
				return;
			/*
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			sb.append("\ndirection : ");
			sb.append(location.getDirection());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			Log.i("BDmap", sb.toString());
			*/
			// =============================================================
			// locData = new MyLocationData.Builder()
			// 		.accuracy(location.getRadius()).direction(locDirection)
			//		.latitude(location.getLatitude())
			//		.longitude(location.getLongitude()).build();
			// mBaiduMap.setMyLocationData(locData);
			// =============================================================
			Log.i("Outdoor", "data sent");
			/*
			Log.i("Outdoor", ""+container.getChildCount()+ " 111");
			Log.i("Outdoor", ""+container.getChildAt(0)+ " 111");
			Log.i("Outdoor", ""+container.getChildAt(0).getVisibility()+ " 111");*/
			//container.setVisibility(1);
			// Log.i("Outdoor", ""+container.getChildAt(0).getZ() + " 111");
			
			Intent intent = new Intent(OutdoorFragment.DIRECTION_CHANGED);
			intent.putExtra("accuracy", location.getRadius())
				.putExtra("direction", locDirection)
				.putExtra("latitude", location.getLatitude())
				.putExtra("longitude", location.getLongitude());
			
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

			// =============================================================
			
			if (isFirstLoc) {
				isFirstLoc = false;
				/*
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				
				mBaiduMap.animateMapStatus(u);*/
			}
			// outdoorRadius.setText("" + location.getRadius());

			if (!isFirstLoc /* && pointCounts >= 7 */
					&& location.getRadius() <= 10) {
				// 精度超过9才加入点阵。
				pointCounts++;
				points.add(new LatLng(location.getLatitude(), location
						.getLongitude()));
				if (points.size() >= 3 && pointCounts >= 1) {
					// 加入点阵的数量超过2，才开始绘制新曲线。
					pointCounts = 0;

					OverlayOptions ooArc = new ArcOptions()
							.color(getSpeedColor(runTime,
									points.get(points.size() - 1),
									points.get(points.size() - 2)))
							.width(12)
							.points(points.get(points.size() - 1),
									points.get(points.size() - 2),
									points.get(points.size() - 3));

					/*
					 * OverlayOptions ooArc = new DotOptions().
					 * center(points.get(points.size()-1));
					 */
					OverlayOptions ooPoly = new PolylineOptions()
							.color(getSpeedColor(runTime,
									points.get(points.size() - 1),
									points.get(points.size() - 2)))
							.width(10)
							.points(points.subList(points.size() - 2,
									points.size()));
					mBaiduMap.addOverlay(ooPoly);
					runTime = 0; // 重新计时。
					Log.i("BDmap", "there should be an Arc");
				}
			}
		}

		public void onREceivePoi(BDLocation poiLocation) {

		}
	}

	public static int getSpeedColor(double time, LatLng point1, LatLng point2) { // 1
																			// -
																			// 4
																			// -
																			// 7
		double distance = DistanceUtil.getDistance(point1, point2);
		double speed = (distance / time) * 0.62 + runSpeed * 0.38;
		runSpeed = speed;
		if (speed > hi) {
			hi = speed;
			// outdoorHi.setText("" + hi);
		}
		if (speed < lo) {
			lo = speed;
			// outdoorLo.setText("" + lo);
		}
		if (speed <= 1) // [0, 1]
			return 0xAAFF0000;
		else if (speed >= 7) // [7, ~]
			return 0xAA00FF00;
		else if (speed <= 4) { // [1, 4]
			int ret = (0xAAFF0000 + (int) (21760 * (speed - 1)));
			return ret - ret % 256;
		} else if (speed >= 4) { // [4, 7]
			int ret = (0xAAFFFF00 - (int) (5570560 * (speed - 4)));
			return ret - ret % 65536 - 256;
		}
		return 0xAAFFFF00;
	}
}