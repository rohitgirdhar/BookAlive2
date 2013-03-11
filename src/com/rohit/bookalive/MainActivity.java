package com.rohit.bookalive;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private CapturedImage img;
	private final int CAPTURE_INTENT = 1;
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView = (ImageView) findViewById(R.id.imageViewMain);
		
		OnTouchListener l = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				img.getTouch(getApplicationContext(), event.getX(), event.getY());
				return true;
			}
		};
		mImageView.setOnTouchListener(l);
		
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

		startActivityForResult(Intent.createChooser(captureIntent, "Click Picture"), CAPTURE_INTENT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO : For now, override checks and call process, for dev, change back
	//	if(requestCode == CAPTURE_INTENT && resultCode == RESULT_OK) {
//			img.processIntentResult();
			img.processDev();
			img.setImageView(mImageView);
			new ComputeHomographyTask().execute();
	//	}
	}
	
	private void askQuestion() {
		img.ask();
		Toast t = Toast.makeText(getApplicationContext(), "This is the question", Toast.LENGTH_LONG);
		t.show();
		img.drawLine();
		img.setImageView(mImageView);
	}
	
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
