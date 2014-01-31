package com.jamasan.eyedropper;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuListFragment extends ListFragment {
	static final int REQUEST_IMAGE_CAPTURE = 1001;
	static final int REQUEST_IMAGE_LOAD = 1002;
	static final int REQUEST_SPECTRUM = 1003;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuListAdapter adapter = new MenuListAdapter(getActivity());
		Activity activity = getActivity();
		adapter.add(new SampleItem(activity.getString(R.string.menu_gallery), android.R.drawable.ic_menu_gallery));
		adapter.add(new SampleItem(activity.getString(R.string.menu_camera), android.R.drawable.ic_menu_camera));
		adapter.add(new SampleItem(activity.getString(R.string.menu_spectrum), R.drawable.ic_menu_spectrum));
		adapter.add(new SampleItem(activity.getString(R.string.menu_favorites), android.R.drawable.star_big_off));
		adapter.add(new SampleItem(activity.getString(R.string.menu_color_editor), android.R.drawable.ic_menu_edit));
		
		setListAdapter(adapter);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class MenuListAdapter extends ArrayAdapter<SampleItem> {

		public MenuListAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			return convertView;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		ImageFetcher parent = (ImageFetcher)getActivity();
		parent.clearFragmentStack();
		switch (position) {
		case 0:
			parent.startGalleryIntent();
			break;
		case 1:
			parent.startCameraIntent();
			break;
		case 2:
			parent.setImageToSpectrum();
			break;
		case 3:
			parent.showFavorites();
			break;
		case 4:
			parent.showCalculator();
			break;
		}
	}
}
