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
		this.mRAL = new ColorRAL();
		mListRowItems = new ArrayList<CustomListItem>();
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mListColors = (ListView)activity.findViewById(R.id.list_colors);
		mListColors.setAdapter(mCustomAdapter);
	}
	
	public void setBaseColor(ColorPoint color) {
		this.mBaseColor = color;
	}
	
	public void updateColors() {
		mColors.clear();
		mListRowItems.clear();
		
		mRAL.getClosestColor(mBaseColor.getR(), mBaseColor.getG(), mBaseColor.getB());
		CustomListItem listItem = new CustomListItem(mBaseColor);
		mListRowItems.add(listItem);
	}
}
