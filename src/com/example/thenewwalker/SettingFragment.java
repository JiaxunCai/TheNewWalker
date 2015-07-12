package com.example.thenewwalker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	
	private TextView nameView, dateView, heightView, weightView, goalView;
	private String nameString, dateString, heightString, weightString, goalString;
	private Button changeBtn;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_setting, container, false);
		getActivity().setTitle("个人设置");
		changeBtn = (Button)mView.findViewById(R.id.change);
		nameView = (TextView)mView.findViewById(R.id.name);
		dateView = (TextView)mView.findViewById(R.id.date);
		heightView = (TextView)mView.findViewById(R.id.height);
		weightView = (TextView)mView.findViewById(R.id.weight);
		goalView = (TextView)mView.findViewById(R.id.goal);
		
		SharedPreferences reader = getActivity().getSharedPreferences("userProfile", 0);
		nameString = reader.getString("name", "");
		dateString = reader.getString("date", "");
		heightString = reader.getString("height", "");
		weightString = reader.getString("weight", "");
		goalString = reader.getString("goal", "");
		
		nameView.setText(nameString);
		dateView.setText(dateString);
		heightView.setText(heightString);
		weightView.setText(weightString);
		goalView.setText(goalString);
		
		changeBtn.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//MainActivity.main_viewPager.setCurrentItem(4);
				
				Fragment mFragment = new SettingChangeFragment();
				FragmentManager mFragmentManager = getFragmentManager();
				mFragmentManager.beginTransaction().replace(R.id.main_ViewPager, mFragment).commit();
			}
		});
		return mView;
    }
}
