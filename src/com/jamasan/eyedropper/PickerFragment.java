package com.jamasan.eyedropper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PickerFragment extends Fragment {
	
	private PhotoViewAttacher mAttacher;
	private ImageView mImageMain;
	private HUD mHUD;
	private Drawable mDrawable;
	private ColorPoint mColor;
	
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	static final int REQUEST_SPECTRUM = 1003;

	static private final double MAX_BITMAP_SIZE = 4096.0;
	
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
		mHUD = new HUD(getActivity());
		ImageView drawerNavIcon = (ImageView)getActivity().findViewById(R.id.picker_nav_drawer_icon);
		drawerNavIcon.setOnClickListener(listener);
		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey("image_resource")) {
				setImageToResource(args.getInt("image_resource"));
			} else if (args.containsKey("image_uri")) {
				URI javaUri = (URI)args.get("image_uri");
				Uri androidUri = Uri.parse(javaUri.toString());
				showImageURI(androidUri);
			} else if (args.containsKey("image_capture_uri")) {
				URI javaUri = (URI)args.get("image_capture_uri");
				Uri androidUri = Uri.parse(javaUri.toString());
				loadImageFromMedia(androidUri);
			}
		}
		if (mColor != null) {
			updateColorReadout(mColor);
		}
	}
	
	@Override
	public void onDestroyView() {
		mAttacher.cleanup();
		mHUD = null;
		mImageMain = null;
		mAttacher = null;
		super.onDestroyView();
	}
	
	public void showImageURI(Uri uri) {
		File file = getImage(uri);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        mImageMain.setImageBitmap(getScaledBitmap(bitmap));
        mAttacher.update();
        mHUD.reset();
	}
	
	// http://stackoverflow.com/questions/11764392
	private File getImage(Uri imageUri) {
        File result = null;
        final String[] cursorColumns = { MediaStore.Images.Media.DATA };
        // some devices (OS versions return an URI of com.android instead of com.google.android
        if (imageUri.toString().startsWith("content://com.android.gallery3d.provider"))  {
            // use the com.google provider, not the com.android provider.
            imageUri = Uri.parse(imageUri.toString().replace("com.android.gallery3d","com.google.android.gallery3d"));
        }

        Cursor cursor = getActivity().getContentResolver().query(imageUri, cursorColumns, null, null, null);
        if (cursor != null) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            cursor.moveToFirst();
            // if it is a picasa image on newer devices with OS 3.0 and up
            if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
                result = downloadImage(imageUri);                       
            } else { // it is a regular local image file
                result = new File(cursor.getString(dataColumnIndex));
            }
            cursor.close();

        } else {
            result = downloadImage(imageUri);
        }
        return result;          
	}
	
	private File downloadImage(Uri imageUri) {
	    File cacheDir;
	    // if the device has an SD card
	    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
	        cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),".OCFL311");
	    } else {
	        // it does not have an SD card
	        cacheDir = getActivity().getCacheDir();
	    }

	    if(!cacheDir.exists()) cacheDir.mkdirs();
	    File f = new File(cacheDir, "downloaded_img");

	    try {

	        InputStream is = null;
	        if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
	            is = getActivity().getContentResolver().openInputStream(imageUri);
	        } else {
	            is = new URL(imageUri.toString()).openStream();
	        }

	        OutputStream  os = new FileOutputStream(f);
	        Utils.InputToOutputStream(is, os);

	        return f;
	    } catch (Exception ex) {
	        Log.d(this.getClass().getName(), "Exception: " + ex.getMessage());
	        ex.printStackTrace();
	        return null;
	    }
	}
	
    public void loadImageFromMedia(Uri uri) {
    	Activity activity = getActivity();
        ContentResolver cr = activity.getContentResolver();
        cr.notifyChange(uri, null);
        try {
        	Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
            mImageMain.setImageBitmap(getScaledBitmap(bitmap));
        } catch (Exception e) {
            Log.d("GrabImage", "Failed to load", e);
        }
        mHUD.reset();
    }
    
    private Bitmap getScaledBitmap(Bitmap bitmap) {
    	if (bitmap == null) return bitmap;

    	if(bitmap.getHeight() > MAX_BITMAP_SIZE || bitmap.getWidth() > MAX_BITMAP_SIZE) {
        	double scaleFactor = MAX_BITMAP_SIZE / Math.max(bitmap.getWidth(), bitmap.getHeight());
        	int scaledHeight = (int)(bitmap.getHeight() * scaleFactor);
        	int scaledWidth = (int)(bitmap.getWidth() * scaleFactor);
        	return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
        } else {
        	return bitmap;
        }
    }
    
    public void setImageToResource(int res) {
    	mDrawable = getResources().getDrawable(res);
    	mImageMain.setImageDrawable(mDrawable);
    	mAttacher.update();
    	mHUD.reset();
    }
    
    public void setImageToSpectrum() {
    	setImageToResource(R.drawable.spectrum_circular);
    }
    
    private void updateColorReadout(ColorPoint color) {
		mHUD.setBaseColor(color);
		mHUD.updateColors();
	}
    
    OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.picker_nav_drawer_icon:
				((FullscreenActivity)getActivity()).showDrawerMenu(true);
				break;
			default:
				break;
			}
			
		}
	};
    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            ImageView im = ((ImageView)view);
			BitmapDrawable drawable = ((BitmapDrawable)im.getDrawable());
			Bitmap bitmap = drawable.getBitmap();
			int posX = (int)(bitmap.getWidth() * x);
			int posY = (int)(bitmap.getHeight() * y);
			int pixelColor = bitmap.getPixel(posX, posY);
			mColor = new ColorPoint(pixelColor);
			updateColorReadout(mColor);
        }
    };
}
