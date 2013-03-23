package com.rohit.bookalive;

import org.opencv.core.Mat;

import android.os.Environment;
import android.util.Log;

public class ImageMatcher {
	private final static String SD_CARD_PATH 	= Environment.getExternalStorageDirectory().toString();
	private final static String TAG = "ImageMatcher";
	public static int match(Mat given) {
		int pg = getPageNum(given.getNativeObjAddr());
		Log.v(TAG,"Returning : " + Integer.toString(pg));
		return pg;
	}
	public static native int getPageNum(long addrTest);
}
