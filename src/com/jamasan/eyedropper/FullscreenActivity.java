package com.jamasan.eyedropper;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamasan.eyedropper.R.id;

public class FullscreenActivity extends Activity {
	
	private PhotoViewAttacher mAttacher;
	private ColorRAL mRAL;
	
	private TextView mReadoutRed;
	private TextView mReadoutGreen;
	private TextView mReadoutBlue;
	private TextView mReadoutHex;
	private ImageView mImageMain;
	private ImageView mColorSwatch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		mImageMain = (ImageView)findViewById(id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		
		this.mReadoutRed = (TextView)findViewById(id.text_red_readout);
		this.mReadoutGreen = (TextView)findViewById(id.text_green_readout);
		this.mReadoutBlue = (TextView)findViewById(id.text_blue_readout);
		this.mReadoutHex = (TextView)findViewById(id.text_hex_readout);
		this.mColorSwatch = (ImageView)findViewById(id.color_sample);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    
	    mAttacher.cleanup();
	}
	
	private void updateColorReadout(int color, Boolean websafe) {
		if (websafe) {
			color = getWebSafeColor(color);
		}
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color >>  0) & 0xFF;
		this.mReadoutRed.setText(String.format("R: %d", r));
		this.mReadoutGreen.setText(String.format("G: %d", g));
		this.mReadoutBlue.setText(String.format("B: %d", b));
		String hex = String.format("0x%06X", color  & 0xFFFFFF);
		this.mReadoutHex.setText(hex);
		this.mColorSwatch.setBackgroundColor(color);
		
		this.mRAL = new ColorRAL();
		mRAL.getClosestColor(r, g, b);
	}
	
	private int getWebSafeColor(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color >>  0) & 0xFF;
		
		int rw = 51 * ((r+25)/51);
		int gw = 51 * ((g+25)/51);
		int bw = 51 * ((b+25)/51);
		
		return (0xFF<<24) + ((rw & 0xFF)<<16) + ((gw & 0xFF)<<8) + ((bw & 0xFF)<<0); 
	}
	
	private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            ImageView im = ((ImageView)view);
            
			BitmapDrawable drawable = ((BitmapDrawable)im.getDrawable());
			Bitmap bitmap = drawable.getBitmap();
			int posX = (int)(bitmap.getWidth() * x);
			int posY = (int)(bitmap.getHeight() * y);
			int pixelColor = bitmap.getPixel(posX, posY);
        	updateColorReadout(pixelColor, true);
        }
    };
}
