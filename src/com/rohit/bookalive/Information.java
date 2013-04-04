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

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class Information {
	
	private final String INFO_STOR = CapturedImage.ROOT_PATH + "Info/";
	private int page = 0;
	private String fname;
	private final String TAG = "Information";
	Context context;
	List<InfoSnippet> infos = new ArrayList<InfoSnippet>();
	Information(int page, Context context) {
		// Constructor to read the information associated with this page
		this.page = page;
		fname = INFO_STOR + Integer.toString(page) + ".xml";
		this.context = context;
		read();
	}
	
	private void read() {
		try {
			File f = new File(fname);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			
			Log.v(TAG,doc.getDocumentElement().getNodeName());
			NodeList objects = doc.getElementsByTagName("object");
			
			for(int i=0; i<objects.getLength(); i++) {
				InfoSnippet is = new InfoSnippet();
				is.read((Element) objects.item(i));
				Log.v(TAG, "added");
				infos.add(is);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clicked(double x, double y, MotionEvent event) {
		for(InfoSnippet is : infos) {
			if(event.getAction() == MotionEvent.ACTION_UP && is.contains(x,y)) {
				is.show(context);
				break;
			}
		}
	}
}
