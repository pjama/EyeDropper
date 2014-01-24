package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HUD {
	private Activity mActivity;
	private ListView mListColors;
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	private ColorPoint mBaseColor;
	private ArrayList<ColorPoint> mColors;
	
	private ColorStandardBase mRAL;
	private ColorStandardBase mPantone;
	
	public HUD(Activity activity) {
		mActivity = activity;
		mColors = new ArrayList<ColorPoint>();
		mRAL = new ColorRAL(mActivity);
		mPantone = new ColorPantone(mActivity);
		
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
		
		ColorPoint colorPantone = mPantone.getClosestColor(mBaseColor); 
		mListRowItems.add(new CustomListItem(colorPantone));
		
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mListColors.setAdapter(mCustomAdapter);
		mListColors.setOnItemClickListener(onItemClickListener);
	}
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorPoint color = item.getColor();
			
			Fragment fragment = new DetailFragment();
			fragment.setArguments(color.toBundle());
			
			FragmentManager manager = mActivity.getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.main_fragment, fragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	};
}
