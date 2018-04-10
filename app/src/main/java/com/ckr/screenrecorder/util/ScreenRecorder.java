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

/**
 * Created by supertramp on 16/12/1.
 * 截屏:将ImageReader传入createVirtualDisplay
 * 录屏:将MediaCodec传入createVirtualDisplay
 */
public class ScreenRecorder {
	private static final String TAG = "ScreenRecorder";
	public static final int REQUEST_MEDIA_PROJECTION = 1;
	public static final int STATE_DEFAULT = -1;
	public static final int STATE_INIT = 0;
	public static final int STATE_PREPARE = 1;
	public static final int STATE_START = 2;
	public static final int STATE_RESUME = 3;
	public static final int STATE_PAUSE = 4;
	public static final int STATE_STOP = 5;
	public static final int STATE_RELEASE = 6;
	private int mWidth;
	private int mHeight;
	private int mDensity;
	private MediaProjectionManager mManager;
	private MediaProjection mProjection;
	private VirtualDisplay mVirtualDisplay;
	private MediaRecorder mMediaRecorder;
	private int recordState = STATE_INIT;

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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mManager = (MediaProjectionManager) ScreenRecordApplication.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		}
	}

	public int getRecordState() {
		return recordState;
	}

	public boolean isRecording() {
		return recordState == STATE_START || recordState == STATE_RESUME;
	}

	public boolean isPause() {
		return recordState == STATE_PAUSE;
	}

	public boolean isStop() {
		return recordState == STATE_STOP;
	}

	public boolean isPrepare() {
		return recordState == STATE_PREPARE;
	}

	public Intent getIntent() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (mManager != null) {
				return mManager.createScreenCaptureIntent();
			}
		}
		return null;
	}

	public void setProjection(int resultCode, Intent data) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (mManager != null) {
				this.mProjection = mManager.getMediaProjection(resultCode, data);
			}
		}
	}

	public void pauseRecord() {
		Logd(TAG, "pauseRecord: ");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (mMediaRecorder != null) {
				if (recordState == STATE_START || recordState == STATE_RESUME) {
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
				if (recordState == STATE_PAUSE) {
					recordState = STATE_RESUME;
					mMediaRecorder.resume();
				}
			}
		}
	}

	public void startRecord() {
		Logd(TAG, "startRecord: ");
		initMediaRecorder();
		if (recordState == STATE_PREPARE) {
			createVirtualDisplay();
			recordState = STATE_START;
			mMediaRecorder.start();
		}
	}

	private void initMediaRecorder() {
		Logd(TAG, "initMediaRecorder: ");
//		if (mMediaRecorder == null) {
			Logd(TAG, "initMediaRecorder: init");
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				String parentPath = Environment.getExternalStorageDirectory() + File.separator + ScreenRecordApplication.getContext().getString(R.string.app_name);
				File parentFile = new File(parentPath);
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				String dateName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
				file = new File(parentPath, "录屏_" + dateName + ".mp4");
			} else {
				String parentPath = ScreenRecordApplication.getContext().getCacheDir().getAbsolutePath();
				String dateName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
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
//		}
		try {
			Logd(TAG, "initMediaRecorder: prepare");
			recordState = STATE_PREPARE;
			mMediaRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
//			recordState = STATE_STOP;
			mMediaRecorder.release();
			ToastUtils.toast("录制异常，请从新录制");
		}
	}

	private void createVirtualDisplay() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (mProjection != null) {
				mProjection.stop();
				mVirtualDisplay.release();
				if (mMediaRecorder != null) {
					recordState = STATE_STOP;
					mMediaRecorder.stop();
					mMediaRecorder.release();
				}
			}
		}
	}
}
