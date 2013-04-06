package com.rohit.bookalive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

public class Animator {
	final private String TAG = "Animator";
	private RelativeLayout relLayout;
	private int page;
	private String fname;
	private Context context;
	final static private String ANIM_STOR = CapturedImage.ROOT_PATH + "Anim/";
	List<Anime> animes = new ArrayList<Anime>();
	
	public Animator(RelativeLayout layout, Context context, int pno) {
		relLayout = layout;
		page = pno;
		this.context = context;
		fname = ANIM_STOR + Integer.toString(page) + ".xml";
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
			NodeList anims = doc.getElementsByTagName("anim");
			Element anim;
			for(int i=0; i<anims.getLength(); i++) {
				anim = (Element) anims.item(i);
				animes.add(new Anime(relLayout, context, anim));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void animate() {
		for(Anime anime : animes) {
			anime.animate();
		}
	}
}
