package com.rohit.bookalive;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;


public class Question_Type1 {
	private List<RegionOfInterest> rois = new ArrayList<RegionOfInterest>();
	private boolean[] done;
	private int countMatch = 0;
	Context context;
	
	public void read() {
		/* Reads the question from file */
		// TODO  For now, simply add the data
		int[] px = new int[4], py = new int[4];
		px[0] = 0; py[0] = 0;
		px[1] = 0; py[1] = 100;
		px[2] = 100; py[2] = 100;
		px[3] = 100; py[3] = 0;
		Polygon P = new Polygon(px,py,4);
		rois.add(new RegionOfInterest("Box", P));
		
		done = new boolean[rois.size()];
		
	}
	
	public void ask(Context context) {
		this.context = context;
		Toast t = Toast.makeText(context, "Find the box in the image", Toast.LENGTH_LONG);
		t.show();
	}
	
	public void clicked(float x, float y) {
		for(int i=0; i<rois.size(); i++) {
			if(!done[i] && rois.get(i).hit(x, y)) {
				done[i] = true;
				countMatch ++;
				show("CORRECT!");
				break;
			}
		}
		checkDone();
		show("NO! Try again!");
	}
	
	private void show(String inp) {
		Toast t = Toast.makeText(context, inp, Toast.LENGTH_SHORT);
		t.show();
	}
	
	public boolean checkDone() {
		if(countMatch == rois.size()) return true;
		else return false;
	}
};