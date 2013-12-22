package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.widget.ListView;

public class HUD {
	private Activity mActivity;
	private ListView mListColors;
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	private ColorPoint mBaseColor;
	private ArrayList<ColorPoint> mColors;
	
	private ColorRAL mRAL;
	
	public HUD(Activity activity) {
		mActivity = activity;
		mColors = new ArrayList<ColorPoint>();
		this.mRAL = new ColorRAL(mActivity);
		mListRowItems = new ArrayList<CustomListItem>();
		mListColors = (ListView)activity.findViewById(R.id.list_colors);
		
	}
	
	public void setBaseColor(ColorPoint color) {
		this.mBaseColor = color;
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
		
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mListColors.setAdapter(mCustomAdapter);
	}
}
