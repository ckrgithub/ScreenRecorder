package com.ckr.screenrecorder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements Runnable {
	private static final String TAG = "MainActivity";
	@BindView(R.id.button)
	Button button;
	@BindView(R.id.textView)
	TextView textView;
	@BindString(R.string.record_start)
	String startRecord;
	@BindString(R.string.record_stop)
	String stopRecord;
	String recordingText;
	private Unbinder unbinder;
	private ScheduledExecutorService mTimerExecutor;
	private int count = 0;
	private TimerHandler mHandler = new TimerHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		unbinder = ButterKnife.bind(this);
		recordingText = getString(R.string.recording);
		RecordLog.debug();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PermissionRequest.REQUEST_RECORD:
				if (PermissionRequest.isPermissionGranted(grantResults)) {
					Logd(TAG, "onRequestPermissionsResult: 开始录制");
					record();
				}
				break;
			case PermissionRequest.REQUEST_READ:
				if (PermissionRequest.isPermissionGranted(grantResults)) {
					if (PermissionRequest.requestRecordPermission(this)) {
						Logd(TAG, "onRequestPermissionsResult: 开始录制");
						record();
					}
				}
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
	}

	@OnClick(R.id.button)
	public void onViewClicked() {
		if (PermissionRequest.requestReadPermission(this) && PermissionRequest.requestRecordPermission(this)) {
			Logd(TAG, "onViewClicked: 开始录制");
			record();
		}
	}

	private void record() {
		ScreenRecorder recorder = ScreenRecorder.getInstance();
		if (null == recorder) {
			ScreenRecordActivity.start(this);
			prepare();
			startTimer();
			ToastUtils.toast(startRecord);
		} else {
			if (recorder.isError()) {
				recorder.startRecord();
				prepare();
			} else {
				recorder.release();
				ScreenRecorder.setNull();
				stopTimer();
				ToastUtils.toast(stopRecord);
			}
		}
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
		mTimerExecutor = Executors.newScheduledThreadPool(1);
		mTimerExecutor.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}

	private void stopTimer() {
		if (null != mTimerExecutor) {
			if (!mTimerExecutor.isShutdown()) {
				mTimerExecutor.shutdown();
			}
			mTimerExecutor = null;
		}
		if (null != mHandler) {
			Message obtain = Message.obtain();
			obtain.what = TimerHandler.MSG_RECORD_STOP;
			obtain.obj = count;
			mHandler.sendMessage(obtain);
		}
		count = 0;
	}

	@Override
	public void run() {
		++count;
		if (null != mHandler) {
			Message obtain = Message.obtain();
			obtain.what = TimerHandler.MSG_RECORD_START;
			obtain.obj = count;
			mHandler.sendMessage(obtain);
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
							Logd(TAG, "handleMessage: start  minute:" + minute + ",second:" + second);
							textView.setText(String.format(recordingText, minute, second));
						}
					}
					break;
				case MSG_RECORD_STOP:
					time = (int) msg.obj;
//					if (time > 900) {
//						time = 900;
//					}
					String minute = "" + time / 60;
					String second = "" + time % 60;
					if (minute.length() < 2) {
						minute = "0" + minute;
					}
					if (second.length() < 2) {
						second = "0" + second;
					}
					if (null != textView) {
						Logd(TAG, "handleMessage: stop  minute:" + minute + ",second:" + second);
						textView.setText(String.format(recordingText, minute, second));
					}
					if (null != button && !startRecord.equals(button.getText())) {
						button.setText(startRecord);
					}
					break;
			}
		}
	}
}
