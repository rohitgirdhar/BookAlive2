package com.rohit.bookalive;

import org.opencv.core.Point;
import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Anime {
	public String type;
	public double tlx, tly;
	public double wd, ht;
	private RelativeLayout relLayout;
	private Context context;
	
	public Anime(RelativeLayout layout, Context context, Element anim) {
		relLayout = layout;
		this.context = context;
		type = anim.getAttribute("type");
		tlx = Double.parseDouble(anim.getAttribute("tlx"));
		tly = Double.parseDouble(anim.getAttribute("tly"));
		wd = Double.parseDouble(anim.getAttribute("width"));
		ht = Double.parseDouble(anim.getAttribute("height"));
	}
	
	public void animate() {
		ImageView anime = new ImageView(context);
		anime.setVisibility(View.VISIBLE);
		if(type.equals("fire")) {
			anime.setBackgroundResource(R.drawable.anime_fire);
		} else {
			throw new IllegalArgumentException("Invalid Anime Type : " + type);
		}
		AnimationDrawable frameAnime = (AnimationDrawable) anime.getBackground();
		RelativeLayout.LayoutParams animParams = new RelativeLayout.LayoutParams((int)wd, (int)ht);
		Point p = new Point(tlx, tly);
		Util.getPointOnOrig(CapturedImage.H.inv(), p);
		animParams.topMargin = (int) p.x;
		animParams.leftMargin = (int) p.y;
		anime.setLayoutParams(animParams);	
		relLayout.addView(anime);
		frameAnime.start();
	}
}
