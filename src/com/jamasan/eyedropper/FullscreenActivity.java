package com.jamasan.eyedropper;

import android.content.res.Configuration;
import android.os.Bundle;

public class FullscreenActivity extends BaseActivity {
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		PickerFragment fragment = new PickerFragment();
		getFragmentManager().beginTransaction().add(R.id.main_fragment, fragment).commit();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_fullscreen);
    }
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
}
