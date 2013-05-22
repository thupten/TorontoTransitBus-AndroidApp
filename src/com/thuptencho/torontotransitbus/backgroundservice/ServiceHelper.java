package com.thuptencho.torontotransitbus.backgroundservice;

import android.content.Context;
import android.content.Intent;

/**
 * Created by thupten on 5/20/13.
 */
public class ServiceHelper {
	private static volatile ServiceHelper instance = null;
	Context context;

	private ServiceHelper(Context context) {
		this.context = context;
	}

	public static ServiceHelper getInstance(Context context) {
		synchronized (ServiceHelper.class) {
			if (instance != null) {
				return instance;
			}
			else {
				return new ServiceHelper(context);
			}
		}

	}

	public void start(Intent intent) {
		context.startService(intent);
	}
}
