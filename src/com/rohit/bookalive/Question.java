package com.rohit.bookalive;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public abstract class Question {
	public String tipText = ""; 
	public String qsText = "";
	public String qfname = "";
	public Context context;
	public CapturedImage capImg;		// to store the captured image to redraw (eg, in q2)
	int qtype = 0;
	
	private final String TAG = "Question";
	
	public Question readType(String fname, CapturedImage c) {
		String stor_qfname = fname;
		String stor_tipText = null, stor_qsText = null;
		int stor_qtype = 0;
		try {
			File f = new File(fname);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			
			Node meta = doc.getElementsByTagName("meta").item(0);
			Element metaE = (Element) meta; 
			stor_qtype = Integer.parseInt(metaE.getElementsByTagName("qtype").item(0).getTextContent());
			stor_tipText = metaE.getElementsByTagName("tiptext").item(0).getTextContent();
			stor_qsText = metaE.getElementsByTagName("qstext").item(0).getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Question qret = null;
		if(stor_qtype == 1) {
			qret = new Question_Type1();
			qret.qfname = stor_qfname;
			qret.qsText = stor_qsText;
			qret.qtype = stor_qtype;
			qret.tipText = stor_tipText;
			qret.capImg = c;
			((Question_Type1) qret).read();
		} else if(stor_qtype == 2) {
			qret = new Question_Type2();
			qret.qfname = stor_qfname;
			qret.qsText = stor_qsText;
			qret.qtype = stor_qtype;
			qret.tipText = stor_tipText;
			qret.capImg = c;
			((Question_Type2) qret).read();
		} else {
			throw new IllegalArgumentException("Invalid Question Type");
		}
		return qret;
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
	
	protected void show(String inp, int time_ms) {
		final Toast t = Toast.makeText(context, inp, Toast.LENGTH_SHORT);
		t.show();
		// Show toast for shorter time
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               t.cancel(); 
           }
        }, time_ms);	// to show for time_ms
	}
	
	abstract public void read();
	abstract public void draw();
	abstract public void clicked(double x, double y);
	abstract public boolean checkDone();
}
