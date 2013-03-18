package com.rohit.bookalive;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import android.view.MotionEvent;

public class Question_Type2 extends Question {
	RegionOfInterest roi;
	private boolean done = false;
	private final static String TAG = "Question_Type2";
	
	private final int MAX_POINTS = 4;
	private double[] px = new double[MAX_POINTS], py = new double[MAX_POINTS];
	private int gotPoints = 0;
	
	private boolean touchDown = false;
	private double downX = 0, downY = 0;
	private MotionEvent downEvent = null;
	
	@Override
	public void read() {
		try {
			File f = new File(qfname);
			double[] px = new double[MAX_POINTS];
			double[] py = new double[MAX_POINTS];
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();

			Log.v(TAG,doc.getDocumentElement().getNodeName());
			Element objects = (Element) doc.getElementsByTagName("objects").item(0);
			NodeList ptList = objects.getElementsByTagName("roi").item(0).getChildNodes();
			int plen = 0;
			for(int i=0; i<ptList.getLength(); i++) {
				Node pt = ptList.item(i);
				if(pt.getNodeType() == Node.ELEMENT_NODE) {
					Element pte = (Element) pt;
					double x = Double.parseDouble(pte.getElementsByTagName("x").item(0).getTextContent());
					double y = Double.parseDouble(pte.getElementsByTagName("y").item(0).getTextContent());
					px[plen] = x; py[plen] = y; plen++;
				}
			}
			Polygon P = new Polygon(px,py,plen);
			roi = new RegionOfInterest("Pickup", P);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw() {
		
		Log.v(TAG, "draw called, pts : " + Integer.toString(gotPoints));
		// TODO remove this, temp for now, key location
		
			capImg.drawLine(roi.P.polyX[0], roi.P.polyY[0], roi.P.polyX[1], roi.P.polyY[1]);
			capImg.drawLine(roi.P.polyX[1], roi.P.polyY[1], roi.P.polyX[2], roi.P.polyY[2]);
			capImg.drawLine(roi.P.polyX[2], roi.P.polyY[2], roi.P.polyX[3], roi.P.polyY[3]);
			capImg.drawLine(roi.P.polyX[3], roi.P.polyY[3], roi.P.polyX[0], roi.P.polyY[0]);
		
		
		// Draw between last 2 points
		if(gotPoints < 2) return;
		capImg.drawLine(px[gotPoints-1], py[gotPoints-1], px[gotPoints-2], py[gotPoints-2]);
		if(gotPoints == MAX_POINTS) {
			capImg.drawLine(px[gotPoints-1], py[gotPoints-1], px[0], py[0]);
		}
	}

	@Override
	public void clicked(double x, double y, MotionEvent event) {
		
		// note, x,y are wrt original image, event is the original event
		double minX = Math.min(x, downX), minY = Math.min(y,  downY);
		double maxX = Math.max(x, downX), maxY = Math.max(y,  downY);
		if( event.getAction() == MotionEvent.ACTION_UP && touchDown == true ) {
			double[] px = new double[4], py = new double[4];
			px[0] = minX; py[0] = minY;
			px[1] = minX; py[1] = maxY;
			px[2] = maxX; py[2] = maxY;
			px[3] = maxX; py[3] = minY;
			Polygon P = new Polygon(px, py, 4);
			if(roi.P.overlap(P) > 0.7) {
				done = true;
			} else {
				show("NO! Try Again", 1000);
			}
			capImg.imageView.stopOverlay();
		} else if( event.getAction() == MotionEvent.ACTION_DOWN) {
			touchDown = true;
			downX = x; downY = y;
			capImg.imageView.startOverlay();
			downEvent = MotionEvent.obtain(event);
		} else if( event.getAction() == MotionEvent.ACTION_MOVE && touchDown == true) {
			capImg.imageView.setCoords(downEvent, event);
		}
	}

	@Override
	public boolean checkDone() {
		return done;
	}
}
