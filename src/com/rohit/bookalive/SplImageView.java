package com.rohit.bookalive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SplImageView extends ImageView {
	private Paint p = new Paint(	);
	int left, top, right, bottom;
	private boolean overlay = false;
	private CapturedImage capImg;
	Context context;
	Rect above1 = new Rect();
	Rect left1 = new Rect();
	Rect bottom1 = new Rect();
	Rect right1 = new Rect();
	
	public SplImageView(Context context) {
		super(context);
		this.context = context;
		setWillNotDraw(false);
	}
	
	public SplImageView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	 }
	
	public void startOverlay() {
		overlay = true;
	}
	
	public void stopOverlay() {
		overlay = false;
	}
	
	public void setCoords(MotionEvent down, MotionEvent up) {
		// using the original event objects to plot at proper positions on screen
		left = (int) Math.min(down.getX(), up.getX());
		right = (int) Math.max(down.getX(), up.getX());
		top = (int) Math.min(down.getY(), up.getY());
		bottom = (int) Math.max(down.getY(), up.getY());
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setARGB(80,0,0,0);
        if(overlay) {
        	//Log.v("here", "draw calling" + Integer.toString(left) + " "+  Integer.toString(right) +" "+  Integer.toString(top) );
        	
        	above1.set(0, 0, canvas.getWidth(), top);
        	left1.set(0, top, left, bottom);
        	right1.set(right, top, canvas.getWidth(), bottom);
        	bottom1.set(0, bottom, canvas.getWidth(), canvas.getHeight());
        	
        	canvas.drawRect(above1, p);
        	canvas.drawRect(bottom1, p);
        	canvas.drawRect(left1, p);
        	canvas.drawRect(right1, p);
        	
        	//r.set(left, top, right, bottom);
        	//canvas.clipRect(r);
        	//canvas.drawARGB(50, 0, 0, 0);
        	//canvas.drawRect(left, top, right, bottom, p);
        }
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        capImg.getTouch(event);
        if(overlay) {
        	invalidate();
        }
        return true;
    }

	public void setCapImg(CapturedImage img) {
		capImg = img;
	}
}
