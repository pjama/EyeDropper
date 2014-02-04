package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HUD {
	private FullscreenActivity mActivity;
	private PickerFragment mPickerFragment;
	private ListView mListColors;
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	private ColorPoint mBaseColor;
	private ArrayList<ColorPoint> mColors;
	
	private ColorStandardBase mRAL;
	private ColorStandardBase mPantone;
	
	public HUD(Activity activity, PickerFragment picker) {
		mActivity = (FullscreenActivity)activity;
		mPickerFragment = picker;
		mColors = new ArrayList<ColorPoint>();
		mRAL = new ColorRAL(mActivity);
		mPantone = new ColorPantone(mActivity);
		
		mListRowItems = new ArrayList<CustomListItem>();
		mListColors = (ListView)activity.findViewById(R.id.list_colors);
		
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mListColors.setAdapter(mCustomAdapter);
		mListColors.setOnItemClickListener(onItemClickListener);
	}
	
	public void reset() {
		mColors.clear();
		mListRowItems.clear();
		if (mCustomAdapter != null) {
			mCustomAdapter.clear();
		}
	}
	
	public void setBaseColor(ColorPoint color) {
		this.mBaseColor = color;
	}
	
	public ColorSource getColorSource() {
		ColorSource colorSource = mPickerFragment.getColorSource();
		return colorSource;
	}
	
	public void updateColors() {
		mColors.clear();
		mListRowItems.clear();
		
		mBaseColor.setName(mBaseColor.getRGB());
		mBaseColor.setDescription("0x"+mBaseColor.getHex());
		mListRowItems.add(new CustomListItem(mBaseColor));
		
		ColorPoint colorWebSafe = mBaseColor.getWebSafeColor();
		mListRowItems.add(new CustomListItem(colorWebSafe));
		
		ColorPoint colorRAL = mRAL.getClosestColor(mBaseColor); 
		mListRowItems.add(new CustomListItem(colorRAL));
		
		ColorPoint colorPantone = mPantone.getClosestColor(mBaseColor); 
		mListRowItems.add(new CustomListItem(colorPantone));
		
		((CustomAdapter)mListColors.getAdapter()).notifyDataSetChanged();
	}
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorSample color = new ColorSample(item.getColor().toBundle());
			color.setSource(mPickerFragment.getColorSource().getSourceType());
			
			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(color.toBundle());
			
			mActivity.setActiveFragment(fragment);
		}
	};
}
