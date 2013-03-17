package com.rohit.bookalive;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.os.Environment;

public class ImageMatcher {
	private final static String SD_CARD_PATH 	= Environment.getExternalStorageDirectory().toString();
	
	public static int match(Mat given) {
		// TODO : FOR NOW, simply return a standard page number
		return 6;
	}
}
