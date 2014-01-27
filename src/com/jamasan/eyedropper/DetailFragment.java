package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DetailFragment extends Fragment {

	private ColorSample mColor;
	private ImageView mColorSwatch;
	private Activity mActivity;
	
	private ColorRAL mRAL;
	private ColorPantone mPantone;
	
	private SeekBar mSeekBarRed;
	private SeekBar mSeekBarGreen;
	private SeekBar mSeekBarBlue;
	
	private ListView mRelatedColors;
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		Bundle args = getArguments();
		if (args != null) {
			mColor = new ColorSample(args);
		}
		
		mRAL = new ColorRAL(mActivity);
		mPantone = new ColorPantone(mActivity);
		
		return inflater.inflate(R.layout.detail_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TextView colorTitle = (TextView)mActivity.findViewById(R.id.color_detail_title);
		mColorSwatch = (ImageView)mActivity.findViewById(R.id.color_detail_swatch);
		
		colorTitle.setText(mColor.getName());
		this.setColorSwatch(mColor.getARGB());
		
		mSeekBarRed = (SeekBar)mActivity.findViewById(R.id.color_editor_red);
		mSeekBarGreen = (SeekBar)mActivity.findViewById(R.id.color_editor_green);
		mSeekBarBlue = (SeekBar)mActivity.findViewById(R.id.color_editor_blue);
		
		mSeekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
		
		mSeekBarRed.setProgress(mColor.getR());
		mSeekBarGreen.setProgress(mColor.getG());
		mSeekBarBlue.setProgress(mColor.getB());
		
		mListRowItems = new ArrayList<CustomListItem>();
		mRelatedColors = (ListView)mActivity.findViewById(R.id.list_related_colors);
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mRelatedColors.setAdapter(mCustomAdapter);
		
		this.updateRelatedColours();
	}
	
	private void setColorSwatch(int color) {
		mColorSwatch.setBackgroundColor(color);
	}
	
	private void updateRelatedColours() {
		mListRowItems.clear();
		mListRowItems.add(new CustomListItem(mColor.getWebSafeColor()));
		mListRowItems.add(new CustomListItem(mRAL.getClosestColor(mColor)));
		mListRowItems.add(new CustomListItem(mPantone.getClosestColor(mColor)));
		((CustomAdapter)mRelatedColors.getAdapter()).notifyDataSetChanged();
	}
	
	OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			updateRelatedColours();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			switch (seekBar.getId()) {
			case R.id.color_editor_red:
				mColor.setR(progress);
				setColorSwatch(mColor.getARGB());
				break;
			case R.id.color_editor_green:
				mColor.setG(progress);
				setColorSwatch(mColor.getARGB());
				break;
			case R.id.color_editor_blue:
				mColor.setB(progress);
				setColorSwatch(mColor.getARGB());
				break;
			default:
				break;
			}
		}
	};
}
