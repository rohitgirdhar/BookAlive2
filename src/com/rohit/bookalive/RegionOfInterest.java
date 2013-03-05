package com.rohit.bookalive;

import org.opencv.core.Mat;

public class RegionOfInterest {
	private String name;
	private Polygon P;
	Mat H;
	
	public RegionOfInterest(String nm, Polygon poly, Mat homo) {
		name = nm; P = poly; H = homo;
	}
	
	public boolean selected(String key, double x, double y) {
		if(name.equals(key) && P.contains(x, y)) {
			return true;
		} else return false;
	}
}
