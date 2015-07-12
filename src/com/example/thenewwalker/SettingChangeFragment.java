package com.example.thenewwalker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingChangeFragment extends Fragment {
	
	private String nameString, dateString, heightString, weightString, goalString;
	private EditText nameView, dateView, heightView, weightView, goalView;
	private Button saveBtn, cancelBtn;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_setting_change, container, false);
		getActivity().setTitle("修改设置");
		
		saveBtn = (Button)mView.findViewById(R.id.save);
		cancelBtn = (Button)mView.findViewById(R.id.cancel);
		nameView = (EditText)mView.findViewById(R.id.nameText);
		dateView = (EditText)mView.findViewById(R.id.dateText);
		heightView = (EditText)mView.findViewById(R.id.heightText);
		weightView = (EditText)mView.findViewById(R.id.weightText);
		goalView = (EditText)mView.findViewById(R.id.goalText);
		
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
		
		saveBtn.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				nameString = nameView.getText().toString().trim();
				dateString = dateView.getText().toString().trim();
				heightString = heightView.getText().toString().trim();
				weightString = weightView.getText().toString().trim();
				goalString = goalView.getText().toString().trim();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("userProfile", 0).edit();
                //步骤2-2：将获取过来的值放入文件
                editor.putString("name", nameString);
            	editor.putString("date", dateString);
            	editor.putString("height", heightString);
            	editor.putString("weight", weightString);
            	editor.putString("goal", goalString);
                //步骤3：提交
                editor.commit();
                if (goalString.isEmpty())
                	goalString = "0";
                StepCountFragment.user_goal = Integer.parseInt(goalString);
				
                MainActivity.main_viewPager.setCurrentItem(3);
			}
		});

		
		cancelBtn.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.main_viewPager.setCurrentItem(3);

			}
		});
		
		
		return mView;
	}
}
