package com.example.thenewwalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent stepCountService = new Intent(context, StepServices.class);
		context.startService(stepCountService);
	}

}
