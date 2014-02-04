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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FullscreenActivity extends BaseActivity implements ImageFetcher {
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	
	private URI mImageUri;
	private Uri mUri;
	
	private Stack<Fragment> mFragments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// Initialize Home Navigation icons
		((ImageView)findViewById(R.id.nav_home_camera)).setOnClickListener(listener);
		((ImageView)findViewById(R.id.nav_home_editor)).setOnClickListener(listener);
		((ImageView)findViewById(R.id.nav_home_favorites)).setOnClickListener(listener);
		((ImageView)findViewById(R.id.nav_home_gallery)).setOnClickListener(listener);
		((ImageView)findViewById(R.id.nav_home_spectrum)).setOnClickListener(listener);
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
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
    	super.onActivityResult(reqCode, resultCode, data);
    	switch(reqCode) {
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
        		args.putSerializable("image_capture_uri", mImageUri);
        		Fragment fragment = new PickerFragment(); 
        		fragment.setArguments(args);
        		setActiveFragment(fragment);
        		
        		//Update Gallery
        		String[] mediaType = {"image/jpeg"};
        		String[] paths = new String[] { mUri.getPath() };
        		MediaScannerConnection.scanFile(this, paths, mediaType, null);
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
    	String imgName = "Photo";
    	String ext = ".jpg";
    	
    	File photo = createImageFile(imgName, ext);
    	photo.delete();
    
        try {
			mImageUri = new URI(Uri.fromFile(photo).toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        mUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        getSlidingMenu().showContent();
	}
	
	public void showSpectrum() {
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
	
	public void showEditor() {
		getSlidingMenu().showContent();
		Bundle args = new Bundle();
		ColorSample color = new ColorSample(255, 255, 255);
		color.setName(getString(R.string.custom_color));
		color.setSource(getString(R.string.color_editor));
		args.putAll(color.toBundle());
		Fragment fragment = new DetailFragment();
		fragment.setArguments(args);
		setActiveFragment(fragment);
	}
	
	public void showDrawerMenu(boolean animate) {
		getSlidingMenu().showMenu(animate);
	}
	
	private File createImageFile(String part, String ext) {
		File dir = null;
		try {
			dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)).getPath());
	        if (!dir.exists()) {
	            dir.mkdir();
	        }
	        return File.createTempFile(part, ext, dir);
		} catch (Exception e) {
			Log.e("CreateFile", "Cannot create file: " + (dir.getAbsolutePath() == null ? "" : dir.getAbsolutePath()));
			return null;
		}
    }
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.nav_home_camera:
				startCameraIntent();
				break;
			case R.id.nav_home_editor:
				showEditor();
				break;
			case R.id.nav_home_favorites:
				showFavorites();
				break;
			case R.id.nav_home_gallery:
				startGalleryIntent();
				break;
			case R.id.nav_home_spectrum:
				showSpectrum();
				break;
			default:
				break;
			}
		}
	}; 
}
