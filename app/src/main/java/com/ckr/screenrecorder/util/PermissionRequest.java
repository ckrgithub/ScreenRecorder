package com.ckr.screenrecorder.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.ckr.screenrecorder.util.RecordLog.Logd;

public class PermissionRequest {
	private static final String TAG = "PermissionRequest";
	public static final int REQUEST_CONTACTS = 0;
	public static final int REQUEST_STORAGE = 1;
	public static final int REQUEST_CALENDAR = 2;
	public static final int REQUEST_LOCATION = 3;
	public static final int REQUEST_CAMERA = 4;
	public static final int REQUEST_RECORD = 5;
	public static final String[] PERMISSION_CONTACTS = {READ_CONTACTS};
	public static final String[] PERMISSION_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
	public static final String[] PERMISSION_CALENDAR = {READ_CALENDAR, WRITE_CALENDAR};
	public static final String[] PERMISSION_LOCATION = {ACCESS_FINE_LOCATION};
	public static final String[] PERMISSION_CAMERA = {CAMERA};
	public static final String[] PERMISSION_RECORD = {RECORD_AUDIO};
	public static final String[] PERMISSION_PHONE = {READ_PHONE_STATE, CALL_PHONE};

	public static boolean requestPermission(@NonNull final Activity activity, @NonNull String[] group, int requestCode) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGranted = true;
		for (String s : group) {
			if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGranted = false;
				break;
			}
		}
		if (!isGranted) {
			if (shouldShowRationale(activity, group)) {//仅仅选择了拒绝
				Logd(TAG, "requestPermission: shouldShowRationale=true");
				ActivityCompat.requestPermissions(activity, group, requestCode);
			} else {
				Logd(TAG, "requestPermission: shouldShowRationale=false");
				ActivityCompat.requestPermissions(activity, group, requestCode);
			}
		}
		return isGranted;
	}

	public static boolean requestPermission(@NonNull final Fragment fragment, @NonNull String[] group, int requestCode) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isGrant = true;
		for (String s : group) {
			if (fragment.getActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
				isGrant = false;
				break;
			}
		}
		if (!isGrant) {
			if (shouldShowRationale(fragment, group)) {
				fragment.requestPermissions(group, requestCode);
			} else {
				fragment.requestPermissions(group, requestCode);
			}
		}
		return isGrant;
	}


	private static boolean shouldShowRationale(Activity activity, @NonNull String[] perms) {
		for (String perm : perms) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
				return true;
			}
		}
		return false;
	}

	private static boolean shouldShowRationale(@NonNull Fragment fragment, @NonNull String[] perms) {
		for (String perm : perms) {
			if (fragment.shouldShowRequestPermissionRationale(perm)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPermissionGranted(@NonNull Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
		List<String> denied = new ArrayList<>();
		if (grantResults.length > 0) {
			for (int i = 0; i < grantResults.length; i++) {
				Logd(TAG, "isPermissionGranted: grantResults:" + grantResults[i] + ",i:" + i);
				String perm = permissions[i];
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
					denied.add(perm);
				}
			}
		}
		if (!denied.isEmpty()) {
			boolean always = hasAlwaysDeniedPermission(activity, denied.toArray(new String[denied.size()]));
			Logd(TAG, "isPermissionGranted: always:" + always);
			return false;
		}
		return true;
	}

	public static boolean isPermissionGranted(@NonNull Fragment fragment, @NonNull String[] permissions, @NonNull int[] grantResults) {
		List<String> denied = new ArrayList<>();
		if (grantResults.length > 0) {
			for (int i = 0; i < grantResults.length; i++) {
				Logd(TAG, "isPermissionGranted: grantResults:" + grantResults[i] + ",i:" + i);
				String perm = permissions[i];
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
					denied.add(perm);
				}
			}
		}
		if (!denied.isEmpty()) {
			boolean always = hasAlwaysDeniedPermission(fragment, denied.toArray(new String[denied.size()]));
			Logd(TAG, "isPermissionGranted: always:" + always);
			return false;
		}
		return true;
	}

	private static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, @NonNull String[] perms) {
		if (!shouldShowRationale(activity, perms)) {
			return true;
		}
		return false;
	}

	private static boolean hasAlwaysDeniedPermission(@NonNull Fragment fragment, @NonNull String[] perms) {
		if (!shouldShowRationale(fragment, perms)) {
			return true;
		}
		return false;
	}


}
