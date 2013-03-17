package com.rohit.bookalive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SplImageView extends ImageView {
	private Paint p = new Paint(	);
	float left, top, right, bottom;
	private boolean overlay = false;
	private CapturedImage capImg;
	Context context;
	
	public SplImageView(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
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
	
	public void setCoords(double minX, double minY, double maxX, double maxY) {
		left = (float) minX;
		right = (float) maxX;
		top = (float) maxY;
		bottom = (float) minY;
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.RED);
        if(overlay) {
        	Log.v("here", "draw calling");
        	canvas.drawRect(left, top, right, bottom, p);
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
