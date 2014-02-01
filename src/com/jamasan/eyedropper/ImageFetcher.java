package com.jamasan.eyedropper;

import android.app.Fragment;

public interface ImageFetcher {
	public void startGalleryIntent();
	public void startCameraIntent();
	public void setImageToSpectrum();
	public void showFavorites();
	public void showCalculator();
	public void showDrawerMenu(boolean animate);
	public Fragment getActiveFragment();
	public void setActiveFragment(Fragment fragment);
	public void clearFragmentStack();
}
