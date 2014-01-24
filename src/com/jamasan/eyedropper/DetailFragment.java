package com.jamasan.eyedropper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {

	private ColorSample mColor;
	private Activity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		Bundle args = getArguments();
		if (args != null) {
			mColor = new ColorSample(args);
		}
			
		return inflater.inflate(R.layout.color_detail, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TextView colorTitle = (TextView)mActivity.findViewById(R.id.color_detail_title);
		ImageView colorSwatch = (ImageView)mActivity.findViewById(R.id.color_detail_swatch);
		
		colorTitle.setText(mColor.getName());
		colorSwatch.setBackgroundColor(mColor.getARGB());
	}
}
