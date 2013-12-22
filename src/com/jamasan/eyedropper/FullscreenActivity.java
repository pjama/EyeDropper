package com.jamasan.eyedropper;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.jamasan.eyedropper.R.id;

public class FullscreenActivity extends Activity {
	
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	
	private PhotoViewAttacher mAttacher;
	
	private LinearLayout mMenuLayout;
	private ImageView mMenuCamera;
	private ImageView mMenuGallery;
	private ImageView mMenuSpectrum;
	
	private HUD mHUD;
	private FrameLayout mMainLayout;
	private ImageView mImageMain;
	
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
	
	private void allocateMainLayout() {
		this.mMainLayout = (FrameLayout)findViewById(R.id.eyedropper_main);
		mImageMain = (ImageView)findViewById(R.id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		mHUD = new HUD(this);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_fullscreen);
        this.allocateMainLayout();
        this.allocateMenu();
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
	
	private void updateColorReadout(ColorPoint color) {
		mHUD.setBaseColor(color);
		mHUD.updateColors();
	}

    private void dispatchTakePictureIntent() {
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    File photo;
	    try {
	        photo = this.createTemporaryFile("picture", ".jpg");
	        photo.delete();
	    } catch(Exception e) {
	        Log.e("CaptureImage", "Can't create file to take picture!");
	        return;
	    }
	    mImageUri = Uri.fromFile(photo);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
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
        	updateColorReadout(color);
        }
    };
}
