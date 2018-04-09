package com.ckr.screenrecorder;

import android.app.Application;
import android.content.Context;

/**
 * Created by PC大佬 on 2018/4/9.
 */

public class ScreenRecordApplication extends Application {

	private static ScreenRecordApplication application;

	public static Context getContext() {
		return application.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}
}
