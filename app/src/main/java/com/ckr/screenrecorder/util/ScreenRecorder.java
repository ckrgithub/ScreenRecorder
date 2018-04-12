package com.ckr.screenrecorder.util;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;

import com.ckr.screenrecorder.R;
import com.ckr.screenrecorder.ScreenRecordApplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ckr.screenrecorder.util.RecordLog.Logd;

public class ScreenRecorder {
	private static final String TAG = "ScreenRecorder";
	public static final int REQUEST_MEDIA_PROJECTION = 1;
	public static final int STATE_IDLE = -1;
	public static final int STATE_INIT = 0;
	public static final int STATE_PREPARING = 10;
	public static final int STATE_PREPARED = 11;
	public static final int STATE_START = 110;
	public static final int STATE_STOP = 111;
	public static final int STATE_RELEASE = 112;
	public static final int STATE_RESUME = 113;
	public static final int STATE_PAUSE = 114;
	private int mWidth;
	private int mHeight;
	private int mDensity;
	private MediaProjectionManager mManager;
	private MediaProjection mProjection;
	private VirtualDisplay mVirtualDisplay;
	private MediaRecorder mMediaRecorder;
	private int recordState = STATE_IDLE;

	private ScreenRecorder() {
	}

	public static ScreenRecorder getInstance() {
		return ScreenRecorderHolder.singleton;
	}

	private static class ScreenRecorderHolder {
		private static ScreenRecorder singleton = new ScreenRecorder();
	}

	public void init(int width, int height, int density) {
		this.mWidth = width;
		this.mHeight = height;
		this.mDensity = density;
		if (isLollipopOrAbove()) {
			mManager = (MediaProjectionManager) ScreenRecordApplication.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		}
	}

	private boolean isLollipopOrAbove() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public int getRecordState() {
		return recordState;
	}

	public final boolean isRecording() {
		return recordState == STATE_START || recordState == STATE_RESUME;
	}

	public final boolean isPause() {
		return recordState == STATE_PAUSE;
	}

	public final boolean isStop() {
		return recordState == STATE_STOP;
	}

	public final boolean isPrepare() {
		return recordState == STATE_PREPARED;
	}

	public Intent getIntent() {
		if (isLollipopOrAbove()) {
			if (mManager != null) {
				return mManager.createScreenCaptureIntent();
			}
		}
		return null;
	}

	public void setProjection(int resultCode, Intent data) {
		if (isLollipopOrAbove()) {
			if (mManager != null) {
				this.mProjection = mManager.getMediaProjection(resultCode, data);
			}
		}
	}

	public void pauseRecord() {
		Logd(TAG, "pauseRecord: ");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (mMediaRecorder != null) {
				if (isRecording()) {
					recordState = STATE_PAUSE;
					mMediaRecorder.pause();
				}
			}
		}
	}

	public void resumeRecord() {
		Logd(TAG, "pauseRecord: ");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (mMediaRecorder != null) {
				if (isPause()) {
					recordState = STATE_RESUME;
					mMediaRecorder.resume();
				}
			}
		}
	}

	public void startRecord() {
		Logd(TAG, "startRecord: ");
		prepare();
		start();
	}

	public void start() {
		if (isPrepare()) {
			createVirtualDisplay();
			recordState = STATE_START;
			mMediaRecorder.start();
		}
	}

	private void initMediaRecorder() {
		Logd(TAG, "initMediaRecorder: ");
		recordState = STATE_INIT;
		File file = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String parentPath = Environment.getExternalStorageDirectory() + File.separator + ScreenRecordApplication.getContext().getString(R.string.app_name);
			File parentFile = new File(parentPath);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			String dateName = new SimpleDateFormat("HHmmss").format(new Date());
			file = new File(parentPath, "录屏_" + dateName + ".mp4");
		} else {
			String parentPath = ScreenRecordApplication.getContext().getCacheDir().getAbsolutePath();
			String dateName = new SimpleDateFormat("HHmmss").format(new Date());
			file = new File(parentPath, "录屏_" + dateName + ".mp4");
		}
		String path = file.getAbsolutePath();
		Logd(TAG, "initMediaRecorder: path:" + path);
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setOutputFile(path);
		mMediaRecorder.setVideoSize(mWidth, mHeight);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
		mMediaRecorder.setVideoFrameRate(10);
	}

	public void prepare() {
		if (recordState == STATE_IDLE || recordState == STATE_STOP || recordState == STATE_RELEASE) {
			initMediaRecorder();
		}
		try {
			Logd(TAG, "initMediaRecorder: prepare");
			recordState = STATE_PREPARING;
			mMediaRecorder.prepare();
			recordState = STATE_PREPARED;
		} catch (IOException e) {
			e.printStackTrace();
			ToastUtils.toast("录制异常，请从新录制");
		}
	}

	private void createVirtualDisplay() {
		if (isLollipopOrAbove()) {
			if (mProjection != null) {
				mVirtualDisplay = mProjection.createVirtualDisplay(
						"ScreenRecorder",
						mWidth,
						mHeight,
						mDensity,
						DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
						mMediaRecorder.getSurface(),
						null, null
				);
			}
		}
	}

	public void release() {
		if (isLollipopOrAbove()) {
			if (mProjection != null) {
				mProjection.stop();
				mProjection = null;
			}
			if (mVirtualDisplay != null) {
				mVirtualDisplay.release();
				mVirtualDisplay = null;
			}
		}
		if (mMediaRecorder != null) {
			recordState = STATE_RELEASE;
			if (isRecording()) {
				mMediaRecorder.stop();
			}
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	public void stop() {
		if (mMediaRecorder != null && isRecording()) {
			recordState = STATE_STOP;
			mMediaRecorder.stop();
		}
	}
}
