package com.jamasan.eyedropper;

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
		
		adapter.add(new SampleItem("Gallery", android.R.drawable.ic_menu_gallery));
		adapter.add(new SampleItem("Camera", android.R.drawable.ic_menu_camera));
		adapter.add(new SampleItem("Spectrum", R.drawable.gallery));
		//adapter.add(new SampleItem("Favorites", android.R.drawable.star_off));
		//adapter.add(new SampleItem("Calculator", android.R.drawable.star_big_off));
		
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
		}
	}
}
