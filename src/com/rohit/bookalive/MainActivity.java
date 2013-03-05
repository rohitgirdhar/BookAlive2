package com.rohit.bookalive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	private CapturedImage img;
	private final int CAPTURE_INTENT = 1;
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView = (ImageView) findViewById(R.id.imageViewMain);
		startCapture();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void startCapture() {
		img = new CapturedImage();
		Intent captureIntent = img.createCaptureIntent(this);
		startActivityForResult(captureIntent, CAPTURE_INTENT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final Intent data_final = data;
		if(requestCode == CAPTURE_INTENT && resultCode == RESULT_OK) {
			img.processIntentResult(data_final);
			final ProgressBar pbar = (ProgressBar) findViewById(R.id.progBar);
			pbar.setVisibility(View.VISIBLE);
			img.setImageView(mImageView);
			new Thread(new Runnable() {
				public void run() {
					img.processHomography();
				}
			}).start();
		}
	}
	
	static {
		System.loadLibrary("opencv_java");
		System.loadLibrary("vision");
	}
}
