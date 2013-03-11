package com.rohit.bookalive;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.os.Environment;

public class ImageMatcher {
	private final static String SD_CARD_PATH 	= Environment.getExternalStorageDirectory().toString();
	
	public static Mat match(Mat given) {
		// TODO : FOR NOW, simply return a standard image
		Mat res = Highgui.imread(SD_CARD_PATH + "/Pictures/BookAlive/" + "act.jpg");
		return res;
	}
}
