package com.rohit.bookalive;

public class RegionOfInterest {
	private String name;
	// TODO make private after draw in QT1
	public Polygon P;
	
	public RegionOfInterest(String nm, Polygon poly) {
		name = nm; P = poly;
	}
	
	public boolean hit(double x, double y) {
		if(P.contains(x, y)) {
			return true;
		} else return false;
	}
}
