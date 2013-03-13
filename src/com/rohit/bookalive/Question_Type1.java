package com.rohit.bookalive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class Question_Type1 {
	private List<RegionOfInterest> rois = new ArrayList<RegionOfInterest>();
	private boolean[] done;
	private int countMatch = 0;
	Context context;
	final static String TAG = "Question_Type1";
	private String tipText = ""; 
	private String qsText = "";
	
	public void read() {
		/* Reads the question from file */
		
		try {
			File f = new File("/mnt/sdcard/Pictures/BookAlive/qt1_img1.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			
			NodeList roiList = doc.getElementsByTagName("roi");
			
			
			for(int i=0; i<roiList.getLength(); i++) {
				NodeList ptList = roiList.item(i).getChildNodes();
				double[] px = new double[ptList.getLength()]; double[] py = new double[ptList.getLength()];
				int plen = 0;
				for(int j=0; j<ptList.getLength(); j++) {
					Node pt = ptList.item(j);
					if(pt.getNodeType() == Node.ELEMENT_NODE) {
						Element pte = (Element) pt;
						double x = Double.parseDouble(pte.getElementsByTagName("x").item(0).getTextContent());
						double y = Double.parseDouble(pte.getElementsByTagName("y").item(0).getTextContent());
						px[plen] = x; py[plen] = y; plen++;
					}
				}
				Polygon P = new Polygon(px,py,plen);
				rois.add(new RegionOfInterest("Snowy", P));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO :  remove hard codes
		tipText = "This is the tip text";
		qsText = "This is the question";
		done = new boolean[rois.size()];
		for(int i=0; i<rois.size(); i++) done[i] = false;
		
	}
	
	public void draw(CapturedImage I) {
		Log.v(TAG, Double.toString(rois.get(0).P.polyX[1]));
		for(int i=0; i<rois.size(); i++) {
			I.drawLine(rois.get(i).P.polyX[0], rois.get(i).P.polyY[0], rois.get(i).P.polyX[1], rois.get(i).P.polyY[1]);
			I.drawLine(rois.get(i).P.polyX[1], rois.get(i).P.polyY[1], rois.get(i).P.polyX[2], rois.get(i).P.polyY[2]);
			I.drawLine(rois.get(i).P.polyX[2], rois.get(i).P.polyY[2], rois.get(i).P.polyX[3], rois.get(i).P.polyY[3]);
			I.drawLine(rois.get(i).P.polyX[3], rois.get(i).P.polyY[3], rois.get(i).P.polyX[0], rois.get(i).P.polyY[0]);
		}
	}
	
	public void ask(Context context) {
		this.context = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(qsText)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do things
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void clicked(double x, double y) {
		//show("CLicked at: " + Double.toString(x) + " " + Double.toString(y));
		for(int i=0; i<rois.size(); i++) {
			if(!done[i] && rois.get(i).hit(x, y)) {
				done[i] = true;
				countMatch ++;
				if(!checkDone()) {
					show("CORRECT!");
				}
				break;
			}
		}
		show("NO! Try again!");
	}
	
	private void show(String inp) {
		final Toast t = Toast.makeText(context, inp, Toast.LENGTH_SHORT);
		t.show();
		// Show toast for shorter time
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               t.cancel(); 
           }
        }, 300);	// to show for 300 ms
	}
	
	public boolean checkDone() {
		if(countMatch == rois.size()) return true;
		else return false;
	}
	
	public void showTip(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(tipText)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do things
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
};