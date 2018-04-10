package com.ckr.screenrecorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.ckr.screenrecorder.util.ScreenRecorder;

public class ScreenRecordActivity extends AppCompatActivity {

	public static void start(Context context) {
		Intent starter = new Intent(context, ScreenRecordActivity.class);
		starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(starter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		ScreenRecorder mScreenRecorder = ScreenRecorder.getInstance();
		mScreenRecorder.init(displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi);
		startActivityForResult(mScreenRecorder.getIntent(), ScreenRecorder.REQUEST_MEDIA_PROJECTION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode && ScreenRecorder.REQUEST_MEDIA_PROJECTION == requestCode) {
			ScreenRecorder mScreenRecorder = ScreenRecorder.getInstance();
			mScreenRecorder.setProjection(resultCode, data);
			mScreenRecorder.startRecord();
		}
		finish();
	}
}
