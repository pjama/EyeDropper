package com.jamasan.eyedropper;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamasan.eyedropper.R.id;

public class FullscreenActivity extends Activity {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	
	private PhotoViewAttacher mAttacher;
	private ColorRAL mRAL;
	
	private LinearLayout mMenuLayout;
	private ImageView mMenuCamera;
	private ImageView mMenuGallery;
	private ImageView mMenuSpectrum;
	
	private LinearLayout mMainLayout;
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
		this.allocateMainLayout();
		this.allocateMenu();
	}
	
	@Override
	protected void onStop() {
		releaseMainLayout();
		releaseMenu();
		super.onStop();
	}
	
	private void allocateMainLayout() {
		this.mMainLayout = (LinearLayout)findViewById(id.eyedropper_main);
		mImageMain = (ImageView)findViewById(id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		
		this.mReadoutRed = (TextView)findViewById(id.text_red_readout);
		this.mReadoutGreen = (TextView)findViewById(id.text_green_readout);
		this.mReadoutBlue = (TextView)findViewById(id.text_blue_readout);
		this.mReadoutHex = (TextView)findViewById(id.text_hex_readout);
		this.mColorSwatch = (ImageView)findViewById(id.color_sample);
	}
	
	private void releaseMainLayout() {
		this.mMainLayout = null;
		this.mReadoutRed = null;
		this.mReadoutGreen = null;
		this.mReadoutBlue = null;
		this.mReadoutHex = null;
		this.mColorSwatch = null;
	}
	
	private void setMainLayoutVisibility(Boolean visible) {
		if(visible) {
			this.mMainLayout.setVisibility(View.VISIBLE);
		} else {
			this.mMainLayout.setVisibility(View.GONE);
		}
	}
	
	private void allocateMenu() {
		this.mMenuLayout = (LinearLayout)findViewById(id.linear_main_menu);
		this.mMenuCamera = (ImageView)findViewById(id.menu_camera);
		this.mMenuGallery = (ImageView)findViewById(id.menu_gallery);
		this.mMenuSpectrum = (ImageView)findViewById(id.menu_spectrum);
		
		this.mMenuSpectrum.setOnClickListener(handler);
	}
	
	private void releaseMenu() {
		this.mMenuCamera = null;
		this.mMenuGallery = null;
		this.mMenuSpectrum = null;
	}
	
	private void setMenuVisibility(Boolean visible) {
		if(visible) {
			this.mMenuLayout.setVisibility(View.VISIBLE);
		} else {
			this.mMenuLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    
	    mAttacher.cleanup();
	}
	
	public OnClickListener handler = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch(view.getId()) {
			case R.id.menu_camera:
				dispatchTakePictureIntent();
			case R.id.menu_gallery:
				//
			case R.id.menu_spectrum:
				showSpectrumSelection();
			}
		}
	};
	
	private void showSpectrumSelection() {
		this.setMenuVisibility(false);
		this.setMainLayoutVisibility(true);
	}
	
	private void updateColorReadout(ColorPoint color, Boolean websafe) {
		if (websafe) {
			color = color.getWebSafeColor();
		}
		this.mReadoutRed.setText(String.format("R: %d", color.getR()));
		this.mReadoutGreen.setText(String.format("G: %d", color.getG()));
		this.mReadoutBlue.setText(String.format("B: %d", color.getB()));
		
		this.mReadoutHex.setText(color.getHex());
		this.mColorSwatch.setBackgroundColor(color.getARGB());
		
		this.mRAL = new ColorRAL();
		mRAL.getClosestColor(color.getR(), color.getG(), color.getB());
	}

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageMain.setImageBitmap(imageBitmap);
        }
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
			ColorPoint color = new ColorPoint(pixelColor);
        	updateColorReadout(color, true);
        }
    };
}
