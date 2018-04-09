package com.ckr.screenrecorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.ckr.screenrecorder.util.ScreenRecorder;

public class ScreenRecordActivity extends AppCompatActivity {

	private ScreenRecorder mScreenRecorder;

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
		mScreenRecorder = new ScreenRecorder(this.getApplicationContext());
		mScreenRecorder.init(displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi);
		startActivityForResult(mScreenRecorder.getIntent(), ScreenRecorder.REQUEST_MEDIA_PROJECTION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode && ScreenRecorder.REQUEST_MEDIA_PROJECTION == requestCode) {
			mScreenRecorder.setProjection(resultCode, data);
			startRecord();
		}
		finish();
	}

	private void startRecord() {
		mScreenRecorder.startRecord();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mScreenRecorder=null;
	}
}
