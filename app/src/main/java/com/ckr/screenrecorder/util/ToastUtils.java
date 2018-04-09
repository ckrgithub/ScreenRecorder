package com.ckr.screenrecorder.util;

import android.widget.Toast;

import com.ckr.screenrecorder.ScreenRecordApplication;

/**
 * Created by ckr on 2016/6/28.
 */
public class ToastUtils {
	private static Toast makeText;

	public static void toast(String content) {
		if (makeText == null) {
			makeText = Toast.makeText(ScreenRecordApplication.getContext(), "", Toast.LENGTH_SHORT);
		}
		makeText.setText(content);
		makeText.show();
	}
}
