package com.rohit.bookalive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class CapturedImage {
	private Mat 	image;
	private Mat 	orig;	// the original db image of the same page
	private Mat		H;		// homography matrix orig to image
	private final String SD_CARD_PATH 	= Environment.getExternalStorageDirectory().toString();
	private final String STOR_PATH 	= SD_CARD_PATH + "/Pictures/BookAlive/" + "test.jpg";
	private final int CAPTURE_IMAGE 	= 1;
	private final static String TAG = "CapturedImage";
	
	public Intent createCaptureIntent(Context context) {
		File photo = new File(STOR_PATH); photo.delete();
		Uri photoUri = Uri.fromFile(photo);
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		captureIntent.putExtra(CameraActivity.OUTPUT_FNAME, photoUri);
		return captureIntent;
	}
	
	public void processIntentResult(Intent data) {
		image = Highgui.imread(STOR_PATH);
		orig = ImageMatcher.match(image);
		H = new Mat();
	}
	
	public void processHomography() {
		computeHomography(orig.getNativeObjAddr(), image.getNativeObjAddr(), H.getNativeObjAddr());
	}
	
	public void setImageView(ImageView mImageView) {
		Bitmap bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(image, bmp);
		mImageView.setImageBitmap(bmp);
		Log.v(TAG, "here");
	}
	
	private void correctOrientation() {
		BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(STOR_PATH, bounds);

        Bitmap bm = BitmapFactory.decodeFile(STOR_PATH);
        ExifInterface exif = null;
		try {
			exif = new ExifInterface(STOR_PATH);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        File file = new File(STOR_PATH);
        
        // TODO : not working, gives permission denied
        if(file.exists()) file.delete();
        try {
        	FileOutputStream out = new FileOutputStream(file);
        	rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        	out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public native void computeHomography(long addrOrig, long addrImage, long addrH);
}
