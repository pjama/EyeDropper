package com.jamasan.eyedropper;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jamasan.eyedropper.R.id;

public class FullscreenActivity extends Activity {
	
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	
	private PhotoViewAttacher mAttacher;
	private ColorRAL mRAL;
	
	private LinearLayout mMenuLayout;
	private ImageView mMenuCamera;
	private ImageView mMenuGallery;
	private ImageView mMenuSpectrum;
	
	private FrameLayout mMainLayout;
	private TextView mReadoutRed;
	private TextView mReadoutGreen;
	private TextView mReadoutBlue;
	private TextView mReadoutHex;
	private ImageView mImageMain;
	private ImageView mColorSwatch;
	
	private Uri mImageUri;
	
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
		//releaseMainLayout();
		//releaseMenu();
		super.onStop();
	}
	
	private void allocateMainLayout() {
		this.mMainLayout = (FrameLayout)findViewById(R.id.eyedropper_main);
		mImageMain = (ImageView)findViewById(R.id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		
		this.mReadoutRed = (TextView)findViewById(R.id.text_red_readout);
		this.mReadoutGreen = (TextView)findViewById(R.id.text_green_readout);
		this.mReadoutBlue = (TextView)findViewById(R.id.text_blue_readout);
		this.mReadoutHex = (TextView)findViewById(R.id.text_hex_readout);
		this.mColorSwatch = (ImageView)findViewById(R.id.color_sample);
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
		
		this.mMenuCamera.setOnClickListener(handler);
		this.mMenuGallery.setOnClickListener(handler);
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
				return;
			case R.id.menu_gallery:
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_IMAGE_LOAD);
				return;
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
		//this.mRAL = new ColorRAL();
		//mRAL.getClosestColor(color.getR(), color.getG(), color.getB());
	}

    private void dispatchTakePictureIntent() {
        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        //    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //}
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    File photo;
	    try
	    {
	        // place where to store camera taken picture
	        photo = this.createTemporaryFile("picture", ".jpg");
	        photo.delete();
	    }
	    catch(Exception e)
	    {
	        Log.v("CaptureImage", "Can't create file to take picture!");
	        Toast.makeText(this, "Please check SD card! Image shot is impossible!", 10000);
	        return;
	    }
	    mImageUri = Uri.fromFile(photo);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	    //start camera intent
	    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data); 
    	switch(requestCode) { 
        case REQUEST_IMAGE_LOAD:
            if(resultCode == RESULT_OK){  
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                ContentResolver resolver = getContentResolver();
                Cursor cursor;
                cursor = resolver.query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                showSpectrumSelection();
                mImageMain.setImageBitmap(bitmap);
                mAttacher.update();
            }
            return;
        case REQUEST_IMAGE_CAPTURE:
        	if(resultCode == RESULT_OK) {
                //Bundle extras = data.getExtras();
                //Bitmap bitmap = (Bitmap) extras.get("data");
        		
                showSpectrumSelection();
                this.grabImage(mImageMain);
                mAttacher.update();
            }
        }
    }
    	
    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath() + "/.temp/");
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }
    
    public void grabImage(ImageView imageView)
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("GrabImage", "Failed to load", e);
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
        	updateColorReadout(color, false);
        }
    };
}
