package com.thuptencho.torontotransitbus.backgroundservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver {
	private Receiver mReceiver;
	
	public MyResultReceiver(Handler handler) {
		super(handler);
	}
	
	public interface Receiver{
		public void onReceiveResult(int resultCode, Bundle resultData);
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if(mReceiver != null){
			mReceiver.onReceiveResult(resultCode, resultData);
		}
		
	}
	
	
	
}
