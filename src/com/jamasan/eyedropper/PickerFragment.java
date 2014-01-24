package com.jamasan.eyedropper;

import java.io.File;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PickerFragment extends Fragment {
	private Uri mImageUri;
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
		Bundle args = getArguments();
		if (args != null) {
			int action = args.getInt("action");
			
			switch(action) {
			case REQUEST_IMAGE_CAPTURE:
				dispatchTakePictureIntent();
				break;
			case REQUEST_IMAGE_LOAD:
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_IMAGE_LOAD);
				break;
			case REQUEST_SPECTRUM:
				mDrawable = getResources().getDrawable(R.drawable.spectrum);
			}
		}
		return inflater.inflate(R.layout.picker, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageMain = (ImageView)getActivity().findViewById(R.id.image_main);
		mAttacher = new PhotoViewAttacher(mImageMain);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		mHUD = new HUD(getActivity());
		
		if (mDrawable != null) {
	    	mImageMain.setImageDrawable(mDrawable);
	    	mAttacher.update();
	    	
	    	//BaseActivity baseActivity = (BaseActivity)getActivity();
	    	//baseActivity.mListFrag.
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
        		this.grabImage(mImageMain);
                mAttacher.update();
            }
        }
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
	}
	
	private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath() + "/.temp/");
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }
	
    public void grabImage(ImageView imageView)
    {
    	Activity activity = getActivity();
        ContentResolver cr = activity.getContentResolver();
        cr.notifyChange(mImageUri, null);
        
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.d("GrabImage", "Failed to load", e);
        }
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
