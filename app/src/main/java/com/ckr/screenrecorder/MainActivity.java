package com.ckr.screenrecorder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ckr.screenrecorder.util.PermissionRequest;
import com.ckr.screenrecorder.util.RecordLog;
import com.ckr.screenrecorder.util.ScreenRecorder;
import com.ckr.screenrecorder.util.ToastUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.ckr.screenrecorder.util.RecordLog.Logd;

public class MainActivity extends AppCompatActivity implements Runnable, PermissionRequest.PermissionListener, DialogInterface.OnClickListener {
	private static final String TAG = "MainActivity";
	private static final int REQUEST_CODE = 1129;
	@BindView(R.id.button)
	Button button;
	@BindView(R.id.textView)
	TextView textView;
	@BindString(R.string.record_start)
	String startRecord;
	@BindString(R.string.record_stop)
	String stopRecord;
	@BindString(R.string.tips)
	String tips;
	@BindString(R.string.message)
	String message;
	String recordingText;
	String recordedText;
	private Unbinder unbinder;
	private ScheduledExecutorService mTimerExecutor;
	private int count = 0;
	private TimerHandler mHandler = new TimerHandler();
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		unbinder = ButterKnife.bind(this);
		recordingText = getString(R.string.recording);
		recordedText = getString(R.string.recorded);
		RecordLog.debug();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PermissionRequest.REQUEST_RECORD:
				if (PermissionRequest.isPermissionGranted(this, permissions, grantResults)) {
					Logd(TAG, "onRequestPermissionsResult: 开始录制");
					record();
				}
				break;
			case PermissionRequest.REQUEST_STORAGE:
				if (PermissionRequest.isPermissionGranted(this, permissions, grantResults)) {
					if (PermissionRequest.requestPermission(this, PermissionRequest.PERMISSION_RECORD, PermissionRequest.REQUEST_RECORD)) {
						Logd(TAG, "onRequestPermissionsResult: 开始录制");
						record();
					}
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult: requestCode:" + requestCode + ",resultCode:" + resultCode);
		if (requestCode == REQUEST_CODE) {
			if (PermissionRequest.requestPermission(this, PermissionRequest.PERMISSION_STORAGE, PermissionRequest.REQUEST_STORAGE)
					&& PermissionRequest.requestPermission(this, PermissionRequest.PERMISSION_RECORD, PermissionRequest.REQUEST_RECORD)) {
				Logd(TAG, "onActivityResult: 开始录制");
				record();
			} else {
				ToastUtils.toast(message);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
		stopTimer();
		if (mHandler != null) {
			mHandler.removeMessages(TimerHandler.MSG_RECORD_START);
			mHandler.removeMessages(TimerHandler.MSG_RECORD_STOP);
			mHandler = null;
		}
	}

	@OnClick(R.id.button)
	public void onViewClicked() {
		if (PermissionRequest.requestPermission(this, PermissionRequest.PERMISSION_STORAGE, PermissionRequest.REQUEST_STORAGE)
				&& PermissionRequest.requestPermission(this, PermissionRequest.PERMISSION_RECORD, PermissionRequest.REQUEST_RECORD)) {
			Logd(TAG, "onViewClicked: 开始录制");
			record();
		}
	}

	private void record() {
		ScreenRecorder recorder = ScreenRecorder.getInstance();
		int recordState = recorder.getRecordState();
		Logd(TAG, "record: recordState:" + recordState);
		if (recordState == ScreenRecorder.STATE_IDLE || recordState == ScreenRecorder.STATE_RELEASE) {
			ScreenRecordActivity.start(this);
			prepare();
			startTimer();
			ToastUtils.toast(startRecord);
		} else {
			if (recorder.isRecording()) {
				recorder.stop();
				stopTimer();
				sendMessage(TimerHandler.MSG_RECORD_STOP, count);
				resetCount();
				ToastUtils.toast(stopRecord);
			} else {
				recorder.startRecord();
				prepare();
				startTimer();
				ToastUtils.toast(startRecord);
			}
		}
	}

	private void resetCount() {
		count = 0;
	}

	private void prepare() {
		if (View.VISIBLE != textView.getVisibility()) {
			textView.setVisibility(View.VISIBLE);
		}
		if (!stopRecord.equals(button.getText())) {
			button.setText(stopRecord);
		}
	}

	private void startTimer() {
		Logd(TAG, "startTimer: mTimerExecutor:" + mTimerExecutor);
		mTimerExecutor = Executors.newScheduledThreadPool(1);
		mTimerExecutor.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}

	private void stopTimer() {
		Logd(TAG, "stopTimer: mTimerExecutor:" + mTimerExecutor);
		if (mTimerExecutor != null) {
			if (!mTimerExecutor.isShutdown()) {
				mTimerExecutor.shutdown();
			}
			mTimerExecutor = null;
		}
	}

	@Override
	public void run() {
		Logd(TAG, "run: ");
		++count;
		sendMessage(TimerHandler.MSG_RECORD_START, count);
	}

	private void sendMessage(int what, Object obj) {
		if (mHandler != null) {
			Message obtain = Message.obtain();
			obtain.what = what;
			obtain.obj = obj;
			mHandler.sendMessage(obtain);
		}
	}

	@Override
	public void onPermissionPermanentlyDenied() {
		Logd(TAG, "onPermissionPermanentlyDenied: ");
		if (dialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			dialog = builder.setCancelable(true)
					.setTitle(tips)
					.setMessage(message)
					.setPositiveButton(R.string.confirm, this)
					.setNegativeButton(R.string.cancel, this).create();
		}
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog != null) {
			dialog.dismiss();
		}
		switch (which) {
			case Dialog.BUTTON_POSITIVE:
				startActivityForResult(
						new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
								.setData(Uri.fromParts("package", getPackageName(), null)),
						REQUEST_CODE);
				break;
			case Dialog.BUTTON_NEGATIVE:
				break;
		}
	}

	private final class TimerHandler extends Handler {
		private final static int MSG_RECORD_START = 0;
		private final static int MSG_RECORD_STOP = 1;
		private final static int MAX_VALUE = 900;

		public TimerHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_RECORD_START:
					int time = (int) msg.obj;
					if (time == MAX_VALUE) {
						stopTimer();
						MainActivity.this.sendMessage(TimerHandler.MSG_RECORD_STOP, count);
						resetCount();
					} else {
						String minute = "" + time / 60;
						String second = "" + time % 60;
						if (minute.length() < 2) {
							minute = "0" + minute;
						}
						if (second.length() < 2) {
							second = "0" + second;
						}
						if (textView != null) {
							String format = String.format(recordingText, minute, second);
							Logd(TAG, "handleMessage: start  minute:" + minute + ",second:" + second + ",format:" + format);
							textView.setText(format);
						}
					}
					break;
				case MSG_RECORD_STOP:
					time = (int) msg.obj;
					String minute = "" + time / 60;
					String second = "" + time % 60;
					if (minute.length() < 2) {
						minute = "0" + minute;
					}
					if (second.length() < 2) {
						second = "0" + second;
					}
					if (textView != null) {
						Logd(TAG, "handleMessage: stop  minute:" + minute + ",second:" + second);
						textView.setText(String.format(recordedText, minute, second));
					}
					if (button != null && !startRecord.equals(button.getText())) {
						button.setText(startRecord);
					}
					break;
			}
		}
	}
}
