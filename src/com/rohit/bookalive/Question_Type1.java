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

import android.util.Log;
import android.view.MotionEvent;


public class Question_Type1 extends Question {
	private List<RegionOfInterest> rois = new ArrayList<RegionOfInterest>();
	private boolean[] done;
	private int countMatch = 0;
	final static String TAG = "Question_Type1";
	
	public void read() {
		/* Reads the question from file */
		try {
			File f = new File(qfname);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			
			Log.v(TAG,doc.getDocumentElement().getNodeName());
			Element objects = (Element) doc.getElementsByTagName("objects").item(0);
			NodeList roiList = objects.getElementsByTagName("roi");
			
			
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
		done = new boolean[rois.size()];
		for(int i=0; i<rois.size(); i++) done[i] = false;
		
	}
	
	public void draw() {
		Log.v(TAG, Double.toString(rois.get(0).P.polyX[1]));
		for(int i=0; i<rois.size(); i++) {
			capImg.drawLine(rois.get(i).P.polyX[0], rois.get(i).P.polyY[0], rois.get(i).P.polyX[1], rois.get(i).P.polyY[1]);
			capImg.drawLine(rois.get(i).P.polyX[1], rois.get(i).P.polyY[1], rois.get(i).P.polyX[2], rois.get(i).P.polyY[2]);
			capImg.drawLine(rois.get(i).P.polyX[2], rois.get(i).P.polyY[2], rois.get(i).P.polyX[3], rois.get(i).P.polyY[3]);
			capImg.drawLine(rois.get(i).P.polyX[3], rois.get(i).P.polyY[3], rois.get(i).P.polyX[0], rois.get(i).P.polyY[0]);
		}
	}
	
		
	public void clicked(double x, double y, MotionEvent event) {
		//show("CLicked at: " + Double.toString(x) + " " + Double.toString(y));
		if(event.getAction() == MotionEvent.ACTION_UP){
			for(int i=0; i<rois.size(); i++) {
				if(!done[i] && rois.get(i).hit(x, y)) {
					done[i] = true;
					countMatch ++;
					if(!checkDone()) {
						show("CORRECT!", 600);
						return;
					}
				}
			}
			show("NO! Try again!", 300);
		}
		
	}
	
	public boolean checkDone() {
		if(countMatch == rois.size()) return true;
		else return false;
	}	
};