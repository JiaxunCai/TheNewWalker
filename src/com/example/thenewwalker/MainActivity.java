package com.example.thenewwalker;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.baidu.mapapi.SDKInitializer;
import com.example.thenewwalker.Outdoor;
import com.example.thenewwalker.OutdoorFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener
{
	public static ViewPager main_viewPager ;
	private RadioGroup main_tab_RadioGroup ;
	private RadioButton	step_count, statistic, outdoor, setting;
	private ArrayList<Fragment> fragmentList ;
	
	private CharSequence mTitle;
	
	public OutdoorFragment mOutdoorFragment;
	
	public static Outdoor outdoorDataSet;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SDKInitializer.initialize(getApplicationContext());
		InitView();
		InitViewPager();
		
		outdoorDataSet = new Outdoor(getApplicationContext());
    	setUpOutdoor();
		mTitle = getTitle();

	}
	public void setUpOutdoor() {
		outdoorDataSet.setUpBDmapClient(this);
		outdoorDataSet.setUpSensor(this);
	}

	public void InitView()
	{
		main_tab_RadioGroup = (RadioGroup) findViewById(R.id.main_tab_RadioGroup) ;
		
		step_count = (RadioButton) findViewById(R.id.radio_chats) ;
		statistic = (RadioButton) findViewById(R.id.radio_contacts) ;
		outdoor = (RadioButton) findViewById(R.id.radio_discover) ;
		setting = (RadioButton) findViewById(R.id.radio_me) ;
		
		main_tab_RadioGroup.setOnCheckedChangeListener(this);
	}
	
	public void InitViewPager()
	{
		main_viewPager = (ViewPager) findViewById(R.id.main_ViewPager);
		
		fragmentList = new ArrayList<Fragment>() ;
		
		Fragment stepCountFragment = new StepCountFragment() ;
		Fragment stepStatisticFragment = new StepStatisticFragment();
		Fragment outdoorFragment = new OutdoorFragment();
		Fragment settingFragment = new SettingFragment();
		Fragment settingChangeFragment = new SettingChangeFragment();
		
		fragmentList.add(stepCountFragment);
		fragmentList.add(stepStatisticFragment);
		fragmentList.add(outdoorFragment);
		fragmentList.add(settingFragment);
		fragmentList.add(settingChangeFragment);
		
		main_viewPager.setAdapter(new MyAdapter(getSupportFragmentManager() , fragmentList));
		main_viewPager.setCurrentItem(0);
		main_viewPager.setOnPageChangeListener(new MyListner());
	}
	
	public class MyAdapter extends FragmentPagerAdapter
	{
		ArrayList<Fragment> list ;
		public MyAdapter(FragmentManager fm , ArrayList<Fragment> list)
		{
			super(fm);
			this.list = list ;
		}
		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
		@Override
		public int getCount() {
			return list.size();
		}
	}

	public class MyListner implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			int current = main_viewPager.getCurrentItem() ;
			switch(current)
			{
			case 0:
				main_tab_RadioGroup.check(R.id.radio_chats);
				break;
			case 1:
				main_tab_RadioGroup.check(R.id.radio_contacts);
				break;
			case 2:
				main_tab_RadioGroup.check(R.id.radio_discover);
				break;
			case 3:
				main_tab_RadioGroup.check(R.id.radio_me);
				break;
			}
		}
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int CheckedId) 
	{
		int current=0;
		switch(CheckedId)
		{
		case R.id.radio_chats:
			current = 0 ;
			break ;
		case R.id.radio_contacts:
			current = 1 ;
			break;
		case R.id.radio_discover:
			current = 2 ;
			break;
		case R.id.radio_me:
			current = 3 ;
			break ;
		}
		if(main_viewPager.getCurrentItem() != current)
		{
			main_viewPager.setCurrentItem(current);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu) ;
		return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId , Menu menu)
	{
		if(featureId == Window.FEATURE_ACTION_BAR  && menu != null)
		{
			if(menu.getClass().getSimpleName().equals("MenuBuilder"))
			{
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu) ;
	}
}