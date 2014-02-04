package com.jamasan.eyedropper;

import android.app.Fragment;

public interface ImageFetcher {
	public void startGalleryIntent();
	public void startCameraIntent();
	public void showSpectrum();
	public void showFavorites();
	public void showEditor();
	public void showDrawerMenu(boolean animate);
	public Fragment getActiveFragment();
	public void setActiveFragment(Fragment fragment);
	public void clearFragmentStack();
}
