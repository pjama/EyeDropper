package com.jamasan.eyedropper;

import android.net.Uri;

public class ColorSource {
	private String mSourceType;
	private Uri mUri;
	
	public ColorSource(String sourceType, Uri uri) {
		mSourceType = sourceType;
		mUri = uri;
	}
	
	public String getSourceType() {	
		return mSourceType;
	}
	
	public Uri getUri() { 
		return mUri;
	}
}
