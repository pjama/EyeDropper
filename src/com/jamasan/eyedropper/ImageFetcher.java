package com.jamasan.eyedropper;

public interface ImageFetcher {
	public void startGalleryIntent();
	public void startCameraIntent();
	public void setImageToSpectrum();
	public void showFavorites();
	public void showCalculator();
	public void setDetailedView(DetailFragment detailFragment);
}
