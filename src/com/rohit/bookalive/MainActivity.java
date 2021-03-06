package com.rohit.bookalive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private CapturedImage img;
	private final int CAPTURE_INTENT = 1;
	private SplImageView mImageView;
	private RelativeLayout mLayout;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView = (SplImageView) findViewById(R.id.imageViewMain);
		mLayout = (RelativeLayout) findViewById(R.id.layoutMain);
		context = this;
		startCapture();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void startCapture() {
		img = new CapturedImage(mImageView, mLayout);
		mImageView.setCapImg(img);
		Intent captureIntent = img.createCaptureIntent(this);
		startActivityForResult(Intent.createChooser(captureIntent, "Click Picture"), CAPTURE_INTENT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO : For now, override checks and call process, for dev, change back
	//	if(requestCode == CAPTURE_INTENT && resultCode == RESULT_OK) {
//			img.processIntentResult();
			new ImageMatchTask().execute();
			
	//	}
	}
	
	protected void computeHomo() {
		img.setImageView();
		new ComputeHomographyTask().execute();
	}
	
	private void askQuestion() {
		img.ask();
		img.drawTemp();
		//img.drawLine(121, 221, 133, 186);
		//img.drawLine(171, 189, 133, 186);
		//img.drawLine(171, 189, 168, 224);
		//img.drawLine(121, 221, 168, 224);
		img.setImageView();
	}
	
	private class ImageMatchTask extends AsyncTask<Void, Void, Void> {
		private ProgressBar pbar;
		
		@Override
		protected void onPreExecute() {
			pbar = (ProgressBar) findViewById(R.id.progBar);			
			pbar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO, remove comment
			//img.processIntentResult();
			img.processDev();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			pbar.setVisibility(View.INVISIBLE);
			computeHomo();
		}
	};
	
	private class ComputeHomographyTask extends AsyncTask<Void, Void, Void> {
		private ProgressBar pbar;
		
		@Override
		protected void onPreExecute() {
			pbar = (ProgressBar) findViewById(R.id.progBar);			
			pbar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			img.processHomography();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			pbar.setVisibility(View.INVISIBLE);
			askQuestion();
		}
	};
	
	static {
		System.loadLibrary("opencv_java");
		System.loadLibrary("vision");
	}
}
