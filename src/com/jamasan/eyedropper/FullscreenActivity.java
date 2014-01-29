package com.jamasan.eyedropper;

import java.io.File;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FullscreenActivity extends BaseActivity implements ImageFetcher {
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	
	private PickerFragment mPickerFragment;
	private DetailFragment mDetailFragment;
	private Uri mImageUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		mPickerFragment = new PickerFragment();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_fragment, mPickerFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_fullscreen);
    }
	
	@Override
	public void onBackPressed() {
	    FragmentManager fm = getFragmentManager();
	    if (mDetailFragment != null && mDetailFragment.isAdded()) {
	    	FragmentTransaction transaction = fm.beginTransaction();
	    	transaction.remove(mDetailFragment);
	    	transaction.commit();
	    } else {
	        super.onBackPressed();  
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode) {
        case REQUEST_IMAGE_LOAD:
            if(resultCode == Activity.RESULT_OK){  
                Uri uri = data.getData();
                mPickerFragment.showImageURI(uri);
            }
            return;
        case REQUEST_IMAGE_CAPTURE:
        	if(resultCode == Activity.RESULT_OK) {
        		mPickerFragment.grabImage(mImageUri);
            }
        }
	}
	
	public void startGalleryIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_IMAGE_LOAD);
		getSlidingMenu().showContent();
		hideDetailedView();
		showPickerFragment();
	}
	
	public void startCameraIntent() {
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
        getSlidingMenu().showContent();
        hideDetailedView();
        showPickerFragment();
	}
	
	public void setImageToSpectrum() {
		getSlidingMenu().showContent();
		hideDetailedView();
		showPickerFragment();
		mPickerFragment.setImageToSpectrum();
	}
	
	public void showFavorites() {
		getSlidingMenu().showContent();
		FavoritesFragment fragment = new FavoritesFragment();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_fragment, fragment);
		transaction.commit();
	}
	
	public void showCalculator() {
		getSlidingMenu().showContent();
		DetailFragment fragment = new DetailFragment();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_fragment, fragment);
		transaction.commit();
		this.setDetailedView(fragment);
	}
	
	private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }
	
	private void showPickerFragment() {
		mPickerFragment = new PickerFragment();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_fragment, mPickerFragment);
		transaction.commit();	
	}
	
	public void setDetailedView(DetailFragment detailFragment) {
		mDetailFragment = detailFragment;
	}
	
	public void hideDetailedView() {
		if (mDetailFragment != null && mDetailFragment.isAdded()) {
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.remove(mDetailFragment);
			transaction.commit();
		}
	}
}
