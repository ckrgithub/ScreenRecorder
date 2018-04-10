package com.ckr.screenrecorder.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CALENDAR;
import static android.Manifest.permission_group.STORAGE;
import static com.ckr.screenrecorder.util.RecordLog.Logd;

public class PermissionRequest {
	private static final String TAG = "PermissionRequest";
	public static final int REQUEST_PHONE = 0;
	public static final int REQUEST_READ = 1;
	public static final int REQUEST_CALENDAR = 2;
	public static final int REQUEST_LOCATION = 3;
	public static final int REQUEST_CAMERA = 4;
	public static final int REQUEST_RECORD = 5;
	private static final String[] PERMISSION_PHONE = {READ_CONTACTS};
	private static final String[] PERMISSION_READ = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
	private static final String[] PERMISSION_CALENDAR = {READ_CALENDAR, WRITE_CALENDAR};
	private static final String[] PERMISSION_LOCATION = {ACCESS_FINE_LOCATION};
	private static final String[] PERMISSION_CAMERA = {CAMERA};
	private static final String[] PERMISSION_RECORD = {RECORD_AUDIO};

	public static boolean requestPhonePermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_PHONE) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
				activity.requestPermissions(PERMISSION_PHONE, REQUEST_PHONE);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启手机联系人的读写权限");
				activity.requestPermissions(PERMISSION_PHONE, REQUEST_PHONE);
			}
		}
		return isGrant;
	}

	public static boolean requestPhonePermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_PHONE) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
				fragment.requestPermissions(PERMISSION_PHONE, REQUEST_PHONE);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启手机联系人的读写权限");
				fragment.requestPermissions(PERMISSION_PHONE, REQUEST_PHONE);
			}
		}
		return isGrant;
	}

	public static boolean requestReadPermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_READ) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {//检查权限
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(STORAGE)) {//选择了拒绝  return true
				activity.requestPermissions(PERMISSION_READ, REQUEST_READ);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用了开启文件读写权限");
				activity.requestPermissions(PERMISSION_READ, REQUEST_READ);
			}
		}
		return isGrant;
	}

	public static boolean requestReadPermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_READ) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(STORAGE)) {//选择了拒绝并且不再提醒  return true
				fragment.requestPermissions(PERMISSION_READ, REQUEST_READ);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用了开启文件读写权限");
				fragment.requestPermissions(PERMISSION_READ, REQUEST_READ);
			}
		}
		return isGrant;
	}

	public static boolean requestCalendarPermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_CALENDAR) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(CALENDAR)) {//选择了拒绝并且不再提醒  return true
				activity.requestPermissions(PERMISSION_CALENDAR, REQUEST_CALENDAR);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用了开启日历读写权限");
				activity.requestPermissions(PERMISSION_CALENDAR, REQUEST_CALENDAR);
			}
		}
		return isGrant;
	}

	public static boolean requestCalendarPermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_CALENDAR) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(CALENDAR)) {//选择了拒绝并且不再提醒  return true
				fragment.requestPermissions(PERMISSION_CALENDAR, REQUEST_CALENDAR);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用了开启日历读写权限");
				fragment.requestPermissions(PERMISSION_CALENDAR, REQUEST_CALENDAR);
			}
		}
		return isGrant;
	}

	public static boolean requestLocationPermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_LOCATION) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
				activity.requestPermissions(PERMISSION_LOCATION, REQUEST_LOCATION);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启GPS定位权限");
				activity.requestPermissions(PERMISSION_LOCATION, REQUEST_LOCATION);
			}
		}
		return isGrant;
	}

	public static boolean requestLocationPermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_LOCATION) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
				fragment.requestPermissions(PERMISSION_LOCATION, REQUEST_LOCATION);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启GPS定位权限");
				fragment.requestPermissions(PERMISSION_LOCATION, REQUEST_LOCATION);
			}
		}
		return isGrant;
	}

	public static boolean requestCameraPermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_CAMERA) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(CAMERA)) {
				activity.requestPermissions(PERMISSION_CAMERA, REQUEST_CAMERA);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启拍照权限");
				activity.requestPermissions(PERMISSION_CAMERA, REQUEST_CAMERA);
			}
		}
		return isGrant;
	}

	public static boolean requestCameraPermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_CAMERA) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(CAMERA)) {
				fragment.requestPermissions(PERMISSION_CAMERA, REQUEST_CAMERA);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启拍照权限");
				fragment.requestPermissions(PERMISSION_CAMERA, REQUEST_CAMERA);
			}
		}
		return isGrant;
	}

	public static boolean requestRecordPermission(@NonNull final Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_RECORD) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (activity.shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
				activity.requestPermissions(PERMISSION_RECORD, REQUEST_RECORD);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启拍照权限");
				activity.requestPermissions(PERMISSION_RECORD, REQUEST_RECORD);
			}
		}
		return isGrant;
	}

	public static boolean requestRecordPermission(@NonNull final Fragment fragment) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : PERMISSION_RECORD) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (fragment.shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
				fragment.requestPermissions(PERMISSION_RECORD, REQUEST_RECORD);
			} else {//选择了拒绝并且不再提醒 ,return false
				ToastUtils.toast("请在系统应用开启拍照权限");
				fragment.requestPermissions(PERMISSION_RECORD, REQUEST_RECORD);
			}
		}
		return isGrant;
	}

	public static boolean isPermissionGranted(@NonNull int[] grantResults) {
		if (grantResults.length > 0) {
			for (int i = 0; i < grantResults.length; i++) {
				Logd(TAG, "isPermissionGranted: grantResults:" + grantResults[i] + ",i:" + i);
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
}
