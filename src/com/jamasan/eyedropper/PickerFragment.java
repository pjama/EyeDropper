package com.jamasan.eyedropper;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PickerFragment extends Fragment {
	
	private PhotoViewAttacher mAttacher;
	private ImageView mImageMain;
	private HUD mHUD;
	private Drawable mDrawable;
	
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	static final int REQUEST_SPECTRUM = 1003;
	
	public PickerFragment() { 
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.picker_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageMain = (ImageView)getActivity().findViewById(R.id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		if (mHUD == null) {
			mHUD = new HUD(getActivity());
		}
	}
	
	@Override
	public void onDestroyView() {
		mAttacher.cleanup();
		super.onDestroyView();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode) {
        case REQUEST_IMAGE_LOAD:
            if(resultCode == Activity.RESULT_OK){  
                Uri uri = data.getData();
                this.showImageURI(uri);
            }
            return;
        case REQUEST_IMAGE_CAPTURE:
        	if(resultCode == Activity.RESULT_OK) {
        		Uri uri = data.getData();
        		this.grabImage(uri);
                mAttacher.update();
            }
        }
	}
	
	public void showImageURI(Uri uri) {
		String[] filePathColumn = {MediaStore.Images.Media.DATA};

        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor;
        cursor = resolver.query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mImageMain.setImageBitmap(bitmap);
        mAttacher.update();
        mHUD.reset();
	}
	
    public void grabImage(Uri uri) {
    	Activity activity = getActivity();
        ContentResolver cr = activity.getContentResolver();
        cr.notifyChange(uri, null);
        
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
            mImageMain.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.d("GrabImage", "Failed to load", e);
        }
        mHUD.reset();
    }
    
    public void setImageToSpectrum() {
    	mDrawable = getResources().getDrawable(R.drawable.spectrum);
    	mImageMain.setImageDrawable(mDrawable);
    	mAttacher.update();
    	mHUD.reset();
    }
    
    private void updateColorReadout(ColorPoint color) {
		mHUD.setBaseColor(color);
		mHUD.updateColors();
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
