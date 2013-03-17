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
	int qtype = 0;
	
	private final String TAG = "Question";
	
	public Question readType(String fname) {
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
		
		Question q1 = null;
		if(stor_qtype == 1) {
			q1 = new Question_Type1();
			q1.qfname = stor_qfname;
			q1.qsText = stor_qsText;
			q1.qtype = stor_qtype;
			q1.tipText = stor_tipText;
			((Question_Type1) q1).read();
		} else {
			throw new IllegalArgumentException("Invalid Question Type");
		}
		return q1;
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
	
	protected void show(String inp) {
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
	
	abstract public void draw(CapturedImage capturedImage);
	abstract public void clicked(double x, double y);
	abstract public boolean checkDone();
}
