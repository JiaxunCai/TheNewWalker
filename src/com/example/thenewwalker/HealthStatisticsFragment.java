package com.example.thenewwalker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HealthStatisticsFragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		/*
		 View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
         int i = getArguments().getInt(ARG_PLANET_NUMBER);
         String planet = getResources().getStringArray(R.array.planets_array)[i];

         int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                         "drawable", getActivity().getPackageName());
         ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
         getActivity().setTitle(planet);
         return rootView;*/
		View mView = inflater.inflate(R.layout.fragment_health_statistics, container, false);
		//mView.findViewById(id)
		getActivity().setTitle("健康信息统计");
		
		Button btn = (Button)mView.findViewById(R.id.test_button_health_statistics);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FileOutputStream fos;
				try {
					fos = getActivity().openFileOutput("inHandData.txt", Context.MODE_APPEND);
					fos.write("\n".getBytes());
					fos.write("+++++++++++++++++++".getBytes());
					fos.write("\n".getBytes());
            	    fos.close(); 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				try {
//					fos = getActivity().openFileOutput("WodeWode.txt", Context.MODE_APPEND);
//					fos.write("\n".getBytes());
//					fos.write("+++++++++++++++++++".getBytes());
//					fos.write("\n".getBytes());
//            	    fos.close(); 
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
		});
		
		return mView;
    }
}