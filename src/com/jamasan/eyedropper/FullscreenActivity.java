package com.jamasan.eyedropper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamasan.eyedropper.R.id;
import com.jamasan.eyedropper.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	
	private TextView mReadoutRed;
	private TextView mReadoutGreen;
	private TextView mReadoutBlue;
	private TextView mReadoutHex;
	private ImageView mColorSwatch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		ImageView imageMain = (ImageView)findViewById(id.image_main);
		imageMain.setOnTouchListener(onTouchImage);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		this.mReadoutRed = (TextView)findViewById(id.text_red_readout);
		this.mReadoutGreen = (TextView)findViewById(id.text_green_readout);
		this.mReadoutBlue = (TextView)findViewById(id.text_blue_readout);
		this.mReadoutHex = (TextView)findViewById(id.text_hex_readout);
		this.mColorSwatch = (ImageView)findViewById(id.color_sample);
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
	View.OnTouchListener onTouchImage = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!(v instanceof ImageView)) {
				return false;
			}
			int action = event.getAction();
			if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
				float screenX = event.getX() * event.getXPrecision();
	            float screenY = event.getY() * event.getYPrecision();
	            
	            ImageView im = (ImageView)v;
				BitmapDrawable drawable = ((BitmapDrawable)im.getDrawable());
				Bitmap bitmap = drawable.getBitmap();
				
				int[] viewCoords = new int[2];
				v.getLocationOnScreen(viewCoords);;
				
	            try {
	            	int pixelColor = bitmap.getPixel((int)screenX, (int)screenY);
	            	updateColorReadout(pixelColor, true);
	            } catch (Exception e) {
	            	Log.e("UpdateColor", e.getMessage());
	            	return false;
	            }
				
				return true;
			}
			return false;
		}
	};
}
