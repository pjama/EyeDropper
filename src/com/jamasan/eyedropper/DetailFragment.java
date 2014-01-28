package com.jamasan.eyedropper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DetailFragment extends Fragment {

	private ColorSample mColor;
	private TextView mColorTitle;
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
		} else {
			mColor = new ColorSample(0);
		}
		
		mRAL = new ColorRAL(mActivity);
		mPantone = new ColorPantone(mActivity);
		
		return inflater.inflate(R.layout.detail_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mColorTitle = (TextView)mActivity.findViewById(R.id.color_detail_title);
		mColorSwatch = (ImageView)mActivity.findViewById(R.id.color_detail_swatch);
		
		mSeekBarRed = (SeekBar)mActivity.findViewById(R.id.color_editor_red);
		mSeekBarGreen = (SeekBar)mActivity.findViewById(R.id.color_editor_green);
		mSeekBarBlue = (SeekBar)mActivity.findViewById(R.id.color_editor_blue);
		
		mSeekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
		
		mListRowItems = new ArrayList<CustomListItem>();
		mRelatedColors = (ListView)mActivity.findViewById(R.id.list_related_colors);
		mCustomAdapter = new CustomAdapter(mActivity, R.layout.row_custom, mListRowItems);
		mRelatedColors.setAdapter(mCustomAdapter);
		mRelatedColors.setOnItemClickListener(onClickRelatedColor);
		
		setColorDetail(mColor);
	}
	
	private void setColorDetail(ColorPoint color) {
		mColorTitle.setText(color.getName());
		this.setColorSwatch(color.getARGB());

		mSeekBarRed.setProgress(color.getR());
		mSeekBarGreen.setProgress(color.getG());
		mSeekBarBlue.setProgress(color.getB());
		
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
			mColorTitle.setText(mActivity.getText(R.string.custom_color));
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
	
	OnItemClickListener onClickRelatedColor = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomListItem item = adapter.getItem(pos);
			ColorPoint color = item.getColor();
			setColorDetail(color);
		}
	};
}
