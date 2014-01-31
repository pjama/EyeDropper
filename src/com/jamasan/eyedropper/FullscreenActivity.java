package com.jamasan.eyedropper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

import android.app.Activity;
import android.app.Fragment;
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
	
	private URI mImageUri;
	
	private Stack<Fragment> mFragments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_fullscreen);
    }
	
	@Override
	public void onBackPressed() {
		if (mFragments == null) mFragments = new Stack<Fragment>();
		if (mFragments.empty()) {
	    	super.onBackPressed();
	    } else {
	    	popFragment();  
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode) {
        case REQUEST_IMAGE_LOAD:
            if(resultCode == Activity.RESULT_OK){  
            	Uri uri = data.getData();
            	Bundle args = new Bundle();
        		try {
					args.putSerializable("image_uri", new URI(uri.toString()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
        		Fragment fragment = new PickerFragment(); 
        		fragment.setArguments(args);
        		setActiveFragment(fragment);
            }
            return;
        case REQUEST_IMAGE_CAPTURE:
        	if(resultCode == Activity.RESULT_OK) {
        		Bundle args = new Bundle();
        		args.putSerializable("image_uri", mImageUri);
        		Fragment fragment = new PickerFragment(); 
        		fragment.setArguments(args);
        		setActiveFragment(fragment);
            }
        }
	}
	
	public Fragment getActiveFragment() {
		return mFragments.peek();
	}
	
	public void setActiveFragment(Fragment fragment) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_fragment, fragment);
		transaction.commit();
		
		if (mFragments == null) mFragments = new Stack<Fragment>();
		mFragments.add(fragment);
	}
	
	public void clearFragmentStack() {
		if (mFragments == null || mFragments.empty())  return;
		FragmentManager manager = getFragmentManager();
		while (!mFragments.empty()) {
			FragmentTransaction transaction = manager.beginTransaction();
			Fragment fragment = mFragments.pop();
			transaction.remove(fragment).commit();
		}
	}
	
	private Fragment popFragment() {
		if (mFragments == null || mFragments.empty()) return null;

		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment fragment = mFragments.pop();
		transaction.remove(fragment);
		
		if (!mFragments.empty()) {
			transaction.replace(R.id.main_fragment, mFragments.peek());
		}
		transaction.commit();
		return fragment;
	}
	
	public void startGalleryIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_IMAGE_LOAD);
		getSlidingMenu().showContent();
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
        try {
			mImageUri = new URI(Uri.fromFile(photo).toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        getSlidingMenu().showContent();
        setActiveFragment(new PickerFragment());
	}
	
	public void setImageToSpectrum() {
		getSlidingMenu().showContent();
		Fragment fragment = new PickerFragment();
		Bundle args = new Bundle();
		args.putInt("image_resource", R.drawable.spectrum_circular);
		fragment.setArguments(args);
		setActiveFragment(fragment);
	}
	
	public void showFavorites() {
		getSlidingMenu().showContent();
		setActiveFragment(new FavoritesFragment());
	}
	
	public void showCalculator() {
		getSlidingMenu().showContent();
		setActiveFragment(new DetailFragment());
	}
	
	private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }
}
