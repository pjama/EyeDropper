package com.jamasan.eyedropper;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private ImageView mSaveFavorite;
	
	private ColorRAL mRAL;
	private ColorPantone mPantone;
	
	private SeekBar mSeekBarRed;
	private SeekBar mSeekBarGreen;
	private SeekBar mSeekBarBlue;
	
	private TextView mTextDateCaptured;
	private TextView mTextSource;
	
	private ListView mRelatedColors;
	private CustomAdapter mCustomAdapter;
	private ArrayList<CustomListItem> mListRowItems;
	
	private SQLiteManager mSQL;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			mColor = new ColorSample(args);
		} else {
			mColor = new ColorSample(0);
		}
		mRAL = new ColorRAL(getActivity());
		mPantone = new ColorPantone(getActivity());
		mSQL = new SQLiteManager(getActivity());
		
		return inflater.inflate(R.layout.detail_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mColorTitle = (TextView)getActivity().findViewById(R.id.color_detail_title);
		mColorSwatch = (ImageView)getActivity().findViewById(R.id.color_detail_swatch);
		mSaveFavorite = (ImageView)getActivity().findViewById(R.id.color_detail_favorite);
		mSaveFavorite.setOnClickListener(onClickListener);

		mSeekBarRed = (SeekBar)getActivity().findViewById(R.id.color_editor_red);
		mSeekBarGreen = (SeekBar)getActivity().findViewById(R.id.color_editor_green);
		mSeekBarBlue = (SeekBar)getActivity().findViewById(R.id.color_editor_blue);
		
		mSeekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
		mSeekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
		
		mTextDateCaptured = (TextView)getActivity().findViewById(R.id.color_detail_date);
		mTextSource = (TextView)getActivity().findViewById(R.id.color_detail_source);
		
		mListRowItems = new ArrayList<CustomListItem>();
		mRelatedColors = (ListView)getActivity().findViewById(R.id.list_related_colors);
		mCustomAdapter = new CustomAdapter(getActivity(), R.layout.row_custom, mListRowItems);
		mRelatedColors.setAdapter(mCustomAdapter);
		mRelatedColors.setOnItemClickListener(onClickRelatedColor);
		
		setColorDetail(mColor);
	}
	
	private void setColorDetail(ColorPoint color) {
		if (color instanceof ColorSample) {
			mColor = (ColorSample)color;
		} else {
			mColor = new ColorSample(color.toBundle());
		}
		mColorTitle.setText(mColor.getName());
		setColorSwatch(mColor.getARGB());
		
		if (mColor.getDateCapturedString() != null) {
			mTextDateCaptured.setText(
					String.valueOf(getActivity().getText(R.string.date_captured))
					+ ": " + mColor.getDateCapturedString());
		} else {
			mTextDateCaptured.setText("");
		}
		
		if (mColor.getSource() != null) {
			mTextSource.setText(
					String.valueOf(getActivity().getText(R.string.source))
					+ ": " + mColor.getSource());
		} else {
			mTextSource.setText("");
		}
		
		mSeekBarRed.setProgress(mColor.getR());
		mSeekBarGreen.setProgress(mColor.getG());
		mSeekBarBlue.setProgress(mColor.getB());
		
		setFavoriteIcon();
		updateRelatedColours();
	}
	
	private void setFavoriteIcon() {
		if(isFavorite(mColor)) {
			mSaveFavorite.setBackgroundResource(R.drawable.star_on);
		} else {
			mSaveFavorite.setBackgroundResource(R.drawable.star_off);
		}
	}
	
	private boolean isFavorite(ColorSample color) {
		Long colorId = color.getId();
		if (colorId >= 0) {
			return true;
		} else {
			ColorSample colorSample = mSQL.isColorSaved(color.getARGB());
			if (colorSample == null || colorSample.getId() < 0) {
				return false; 
			} else {
				return true;
			}
		}
	}
	
	private ColorSample findFavorite(int argb) {
		ColorSample colorSample = mSQL.isColorSaved(argb);
		return colorSample;
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
			mColor.setId(-1);
			mColor.setSource(getActivity().getString(R.string.color_editor));
			mColor.setDate(Calendar.getInstance().getTime());
			setColorDetail(mColor);
			updateRelatedColours();
			setFavoriteIcon();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mColor.setName(getActivity().getText(R.string.custom_color));
			mColorTitle.setText(mColor.getName());
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			switch (seekBar.getId()) {
			case R.id.color_editor_red:
				mColor.setR(progress);
				break;
			case R.id.color_editor_green:
				mColor.setG(progress);
				break;
			case R.id.color_editor_blue:
				mColor.setB(progress);
				break;
			default:
				break;
			}
			setColorSwatch(mColor.getARGB());
		}
	};
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.color_detail_favorite:
				long colorId = mColor.getId();
				if (colorId > -1 && mSQL.isColorIdSaved(colorId)) {
					mSQL.deleteColor(colorId);
					mColor.setId(-1);
				} else {
					long id = mSQL.saveColor(mColor);
					mColor.setId(id);
				}
				setFavoriteIcon();
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
			ColorSample colorSample = findFavorite(color.getARGB());
			if (colorSample != null) {
				setColorDetail(colorSample);
			} else {
				colorSample = new ColorSample(color.toBundle());
				colorSample.setSource(getActivity().getString(R.string.color_editor));
				setColorDetail(colorSample);
			}
		}
	};
}
