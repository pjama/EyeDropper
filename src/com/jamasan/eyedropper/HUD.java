package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HUD {
	private FullscreenActivity mActivity;
	private PickerFragment mPickerFragment;
	
	private FrameLayout mColorFrame;
	private ImageView mColorSwatch;
	private TextView mColorTitle;
	private TextView mColorSubitle;
	
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
		
		mColorFrame = (FrameLayout)activity.findViewById(R.id.color_picker_frame);
		mColorSwatch = (ImageView)activity.findViewById(R.id.picker_color_swatch);
		mColorTitle = (TextView)activity.findViewById(R.id.picker_color_title);
		mColorSubitle = (TextView)activity.findViewById(R.id.picker_color_description);
		
		mColorFrame.setOnClickListener(onClickListener);
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
		
		mColorSwatch.setBackgroundColor(mBaseColor.getARGB());
		mColorTitle.setText("0x"+mBaseColor.getHex());
		mColorSubitle.setText(mBaseColor.getRGB());
		
		ColorPoint colorWebSafe = mBaseColor.getWebSafeColor();
		mListRowItems.add(new CustomListItem(colorWebSafe));
		
		ColorPoint colorRAL = mRAL.getClosestColor(mBaseColor); 
		mListRowItems.add(new CustomListItem(colorRAL));
		
		ColorPoint colorPantone = mPantone.getClosestColor(mBaseColor); 
		mListRowItems.add(new CustomListItem(colorPantone));
		
		((CustomAdapter)mListColors.getAdapter()).notifyDataSetChanged();
	}
	
	private void showColorDetail(ColorPoint color) {
		ColorSample colorSample = new ColorSample(color.toBundle());
		colorSample.setSource(mPickerFragment.getColorSource().getSourceType());
		DetailFragment fragment = new DetailFragment();
		fragment.setArguments(color.toBundle());
		mActivity.setActiveFragment(fragment);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.color_picker_frame:
				showColorDetail(mBaseColor);
				break;
			default:
				break;
			}
		}
	};
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorSample color = new ColorSample(item.getColor().toBundle());
			showColorDetail(color);
		}
	};
}
